#define _XOPEN_SOURCE 600
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdint.h>
#include <math.h>
#include <sys/time.h>


// TASK: T1
// Include the cooperative groups library
// BEGIN: T1
#include <cooperative_groups.h>
namespace cg = cooperative_groups;
// END: T1


// Convert 'struct timeval' into seconds in double prec. floating point
#define WALLTIME(t) ((double)(t).tv_sec + 1e-6 * (double)(t).tv_usec)

// Option to change numerical precision
typedef int64_t int_t;
typedef double real_t;

// TASK: T1b
// The three grids used for the calculations. Allocated on the device. 
real_t *d_prv;
real_t *d_cur;
real_t *d_nxt;
#define d_U_prv(i,j) d_prv[((i)+1)*(N+2)+(j)+1]
#define d_U(i,j)     d_cur[((i)+1)*(N+2)+(j)+1]
#define d_U_nxt(i,j) d_nxt[((i)+1)*(N+2)+(j)+1]

// The h_grid variable holds enough space for exactly one grid.
// Used for the initial condition, and to copy data from the device before storing to file.
real_t *h_grid;
#define h_U(i,j)     h_grid[((i)+1)*(N+2)+(j)+1]
// BEGIN: T1b
#define BLOCK_DIM_X 16
#define BLOCK_DIM_Y 16

// Simulation parameters: size, step count, and how often to save the state
int_t
    N = 128,
    M = 128,
    max_iteration = 1000000,
    snapshot_freq = 1000;

// Wave equation parameters, time step is derived from the space step
const real_t
    c  = 1.0,
    dx = 1.0,
    dy = 1.0;
real_t
    dt;

// END: T1b

#define cudaErrorCheck(ans) { gpuAssert((ans), __FILE__, __LINE__); }
inline void gpuAssert(cudaError_t code, const char *file, int line, bool abort=true)
{
    if (code != cudaSuccess) {
        fprintf(stderr,"GPUassert: %s %s %d\n", cudaGetErrorString(code), file, line);
        if (abort) exit(code);
    }
}

// Rotate the time step buffers.
void move_buffer_window() {
    // Swap the pointers
    real_t* temp = d_prv;
    d_prv = d_cur;
    d_cur = d_nxt;
    d_nxt = temp;
}


// Save the present time step in a numbered file under 'data/'
void domain_save ( int_t step )
{
    char filename[256];
    sprintf ( filename, "data/%.5ld.dat", step );
    FILE *out = fopen ( filename, "wb" );
    if (out == NULL) {
        printf("ERROR: Could not open file %s\n", filename);
        exit(EXIT_FAILURE);
    }
    // TODO: Probably a way to write everything in one go.
    for ( int_t i=0; i<M; i++ )
    {
        fwrite ( &h_U(i,0), sizeof(real_t), N, out );
    }
    fclose ( out );
}


// TASK: T4
// Get rid of all the memory allocations
void domain_finalize ( void )
{
// BEGIN: T4
    free(h_grid);
    cudaFree(d_prv);
    cudaFree(d_cur);
    cudaFree(d_nxt);
// END: T4
}


// TASK: T6
// Neumann (reflective) boundary condition
// BEGIN: T6
__device__ void boundary_condition_gpu(real_t *d_cur, int i, int j, int M, int N) {
    if (i == 0) {
        d_U(-1, j) = d_U(1, j);
    }
    if (i == M - 1) {
        d_U(M, j) = d_U(M-2, j);
    }

    if (j == 0) {
        d_U(i, -1) = d_U(i, 1);
    }
    if (j == N - 1) {
        d_U(i, N) = d_U(i, N-2);
    }
}

// TASK: T5
// Integration formula
// BEGIN; T5
__global__ void time_step_gpu(real_t *d_prv, real_t *d_cur, real_t *d_nxt, int M, int N, real_t dt, real_t c, real_t dx, real_t dy)
{
    // Indices of the thread in the grid
    int i = blockIdx.y * blockDim.y + threadIdx.y;
    int j = blockIdx.x * blockDim.x + threadIdx.x;

    // Return if the thread is out of bounds
    if (i < 0 || i >= M || j < 0 || j >= N) {
        return;
    }

    boundary_condition_gpu(d_cur, i, j, M, N);

    d_U_nxt(i,j) = -d_U_prv(i,j) + 2.0*d_U(i,j)
                     + (dt*dt*c*c)/(dx*dy) * (
                        d_U(i-1,j)+d_U(i+1,j)+d_U(i,j-1)+d_U(i,j+1)-4.0*d_U(i,j)
                     );

    // Since each time step is dependent on calculations from the previous timetstep, we
    // need to ensure every computation of the current timestep is completed before any thread 
    // can continue onto the next timestep.
    cg::grid_group grid = cg::this_grid();
    grid.sync();
}

// TASK: T7
// Main time integration.
void simulate( void )
{
// BEGIN: T7
    dim3 blockDim(BLOCK_DIM_X, BLOCK_DIM_Y);
    dim3 gridDim(M / BLOCK_DIM_X, N / BLOCK_DIM_Y);

    void *args[] = {(void *)&d_prv, (void *)&d_cur, (void *)&d_nxt, (void *)&M, (void *)&N, (void *)&dt, (void *)&c, (void *)&dx, (void *)&dy};

    // Go through each time step
    for ( int_t iteration=0; iteration<=max_iteration; iteration++ )
    {
        if ( (iteration % snapshot_freq)==0 )
        {
            cudaErrorCheck(cudaMemcpy(h_grid, d_cur, (M+2)*(N+2) * sizeof(real_t), cudaMemcpyDeviceToHost));
            domain_save ( iteration / snapshot_freq );
        }

        // Perform the time step using on the GPU
        cudaLaunchCooperativeKernel((void*)time_step_gpu, gridDim, blockDim, args);
        // time_step_gpu<<<gridDim, blockDim>>>(d_prv, d_cur, d_nxt, M, N, dt, c, dx, dy);
        // cudaDeviceSynchronize();

        // Rotate the time step buffers
        move_buffer_window();
    }
// END: T7
}


// TASK: T8
// GPU occupancy
void occupancy( void )
{
// BEGIN: T8
    int threads_per_block = BLOCK_DIM_X * BLOCK_DIM_Y;
    int blocks_per_grid = (M / BLOCK_DIM_X) * (N / BLOCK_DIM_Y);

    cudaDeviceProp p;
    cudaGetDeviceProperties(&p, 0);

    int max_active_blocks_per_sm;
    cudaOccupancyMaxActiveBlocksPerMultiprocessor(
        &max_active_blocks_per_sm,
        time_step_gpu,
        threads_per_block,
        0
    );

    int active_warps_per_sm = max_active_blocks_per_sm * (BLOCK_DIM_X * BLOCK_DIM_Y / 32);
    int max_warps_per_sm = p.maxThreadsPerMultiProcessor / 32;
    float occupancy = (float)active_warps_per_sm / (float)max_warps_per_sm;

    printf("--- Occupancy ---\n");
    printf("Grid size: %d\n", blocks_per_grid);
    printf("Launched blocks of size: %d\n", threads_per_block);
    printf("Theoretical occupancy: %f\n", occupancy);
// END: T8
}


// TASK: T2
// Make sure at least one CUDA-capable device exists
static bool init_cuda()
{
    cudaDeviceProp p;
    int device_count = 0;
    if (cudaGetDeviceCount(&device_count) != cudaSuccess || device_count == 0) {
        printf("ERROR: No CUDA devices found!\n");
        return false;
    }

    cudaSetDevice(0);
    cudaGetDeviceProperties(&p, 0);

    if (!p.cooperativeLaunch) {
        printf("ERROR: Device does not support cooperative kernel launch!\n");
        return false;
    }

    printf("Name: %s\n", p.name);
    printf("Compute capability: %d.%d\n", p.major, p.minor);
    printf("Multiprocessors: %d\n", p.multiProcessorCount);
    printf("Warp size: %d\n", p.warpSize);
    return true;
}


// TASK: T3
// Set up our three buffers, and fill two with an initial perturbation
void domain_initialize ( void )
{
// BEGIN: T3
    bool locate_cuda = init_cuda();
    if (!locate_cuda)
    {
        exit( EXIT_FAILURE );
    }

    // Host data to store the state of a single grid
    h_grid = (real_t *) malloc ( (M+2)*(N+2)*sizeof(real_t) );

    // The three grids on the GPU we will use to do the calculations 
    cudaErrorCheck(cudaMalloc(&d_prv, (M+2)*(N+2) * sizeof(real_t)));
    cudaErrorCheck(cudaMalloc(&d_cur, (M+2)*(N+2) * sizeof(real_t)));
    cudaErrorCheck(cudaMalloc(&d_nxt, (M+2)*(N+2) * sizeof(real_t)));

    for ( int_t i=0; i<M; i++ )
    {
        for ( int_t j=0; j<N; j++ )
        {
            // Calculate delta (radial distance) adjusted for M x N grid
            real_t delta = sqrt ( ((i - M/2.0) * (i - M/2.0)) / (real_t)M +
                                ((j - N/2.0) * (j - N/2.0)) / (real_t)N );
            h_U(i,j) = exp ( -4.0*delta*delta );
        }
    }

    // Send initial condition from host to device
    cudaErrorCheck(cudaMemcpy(d_prv, h_grid, (M+2)*(N+2) * sizeof(real_t), cudaMemcpyHostToDevice));
    cudaErrorCheck(cudaMemcpy(d_cur, h_grid, (M+2)*(N+2) * sizeof(real_t), cudaMemcpyHostToDevice));

    // Set the time step for 2D case
    dt = dx*dy / (c * sqrt (dx*dx+dy*dy));
// END: T3
}


int main ( void )
{
    // Set up the initial state of the domain
    domain_initialize();

    struct timeval t_start, t_end;

    gettimeofday ( &t_start, NULL );
    simulate();
    gettimeofday ( &t_end, NULL );

    printf ( "Total elapsed time: %lf seconds\n",
        WALLTIME(t_end) - WALLTIME(t_start)
    );

    occupancy();

    // Clean up and shut down
    domain_finalize();
    exit ( EXIT_SUCCESS );
}
