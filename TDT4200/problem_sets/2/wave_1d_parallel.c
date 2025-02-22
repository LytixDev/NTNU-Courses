#define _XOPEN_SOURCE 600
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdint.h>
#include <math.h>
#include <sys/time.h>

// TASK: T1a
// Include the MPI headerfile
// BEGIN: T1a
#include <mpi.h>
// END: T1a


// Option to change numerical precision.
typedef int64_t int_t;
typedef double real_t;


// TASK: T1b
// Declare variables each MPI process will need
// BEGIN: T1b
int world_size;
int world_rank;
// END: T1b


// Simulation parameters: size, step count, and how often to save the state.
const int_t
    N = 65536,
    max_iteration = 100000,
    snapshot_freq = 500;

int_t step = 0;
int_t extra_work_amount = 0;
/* 
 * The extra work/data the current process will handle 
 * Will be equal to extra_work_amount for the final rank/process and 0 for all others.
 * Certainly not the most sophisticated solution, however
 * with N being a lot larger than the number of proccesses, it works out fine.
 */
int_t extra_work = 0;

// Wave equation parameters, time step is derived from the space step.
const real_t
    c  = 1.0,
    dx = 1.0;
real_t
    dt;

// Buffers for three time steps, indexed with 2 ghost points for the boundary.
real_t
    *buffers[3] = { NULL, NULL, NULL };


#define U_prv(i) buffers[0][(i)+1]
#define U(i)     buffers[1][(i)+1]
#define U_nxt(i) buffers[2][(i)+1]


// Convert 'struct timeval' into seconds in double prec. floating point
#define WALLTIME(t) ((double)(t).tv_sec + 1e-6 * (double)(t).tv_usec)


// TASK: T8
// Save the present time step in a numbered file under 'data/'.
void domain_save ( int_t step )
{
// BEGIN: T8
    if (world_rank != 0) {
        return;
    }
    char filename[256];
    sprintf ( filename, "data/%.5ld.dat", step );
    FILE *out = fopen ( filename, "wb" );
    fwrite ( &U(0), sizeof(real_t), N, out );
    fclose ( out );
// END: T8
}


// TASK: T3
// Allocate space for each process' sub-grids
// Set up our three buffers, fill two with an initial cosine wave,
// and set the time step.
void domain_initialize ( void )
{
// BEGIN: T3
    /* The root process will always need the entire domain */
    if (world_rank == 0) {
        buffers[0] = malloc ( (N+2)*sizeof(real_t) );
        buffers[1] = malloc ( (N+2)*sizeof(real_t) );
        buffers[2] = malloc ( (N+2)*sizeof(real_t) );
    } else {
        buffers[0] = malloc ((step + extra_work + 2)*sizeof(real_t));
        buffers[1] = malloc ((step + extra_work + 2)*sizeof(real_t));
        buffers[2] = malloc ((step + extra_work + 2)*sizeof(real_t));
    }

    for ( int_t i=0; i < step + extra_work; i++ )
    {
        U_prv(i) = U(i) = cos ( M_PI*(i + world_rank * step) / (real_t)N );
    }
// END: T3

    // Set the time step for 1D case.
    dt = dx / c;
}


// Return the memory to the OS.
void domain_finalize ( void )
{
    free ( buffers[0] );
    free ( buffers[1] );
    free ( buffers[2] );
}


// Rotate the time step buffers.
void move_buffer_window ( void )
{
    real_t *temp = buffers[0];
    buffers[0] = buffers[1];
    buffers[1] = buffers[2];
    buffers[2] = temp;
}


// TASK: T4
// Derive step t+1 from steps t and t-1.
void time_step ( void )
{
// BEGIN: T4
    for ( int_t i=0; i < step + extra_work; i++ )
    {
        U_nxt(i) = -U_prv(i) + 2.0*U(i)
                 + (dt*dt*c*c)/(dx*dx) * (U(i-1)+U(i+1)-2.0*U(i));
    }
// END: T4
}


// TASK: T6
// Neumann (reflective) boundary condition.
void boundary_condition ( void )
{
// BEGIN: T6
    if (world_rank == 0) {
        U(-1) = U(1);
    }
    if (world_rank == world_size - 1) {
        U(step + extra_work) = U(step + extra_work - 2);
    }
// END: T6
}


// TASK: T5
// Communicate the border between processes.
void border_exchange( void )
{
// BEGIN: T5
    if (world_rank != 0) {
        /* Exhange left-most ghost pointers */
        MPI_Send(&U(0), 1, MPI_DOUBLE, world_rank - 1, 0, MPI_COMM_WORLD);
        MPI_Recv(&U(-1), 1, MPI_DOUBLE, world_rank - 1, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
    }
    if (world_rank != world_size - 1) {
        /* Exhange right-most ghost pointers */
        MPI_Send(&U(step - 1), 1, MPI_DOUBLE, world_rank + 1, 0, MPI_COMM_WORLD);
        MPI_Recv(&U(step), 1, MPI_DOUBLE, world_rank + 1, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
    }
// END: T5
}


// TASK: T7
// Every process needs to communicate its results
// to root and assemble it in the root buffer
void send_data_to_root()
{
// BEGIN: T7
    ;
    int final_rank = world_size - 1;
    if (world_rank == 0) {
        for (int i = 1; i < final_rank; i++) {
            MPI_Recv(&U(i * step), step + 1, MPI_DOUBLE, i, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
        }
        MPI_Recv(&U(final_rank * step), step + extra_work_amount + 1, MPI_DOUBLE, final_rank, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
    } else {
        if (world_rank == final_rank) {
            MPI_Send(&U(0), step + extra_work_amount + 1, MPI_DOUBLE, 0, 0, MPI_COMM_WORLD);
        } else {
            MPI_Send(&U(0), step + 1, MPI_DOUBLE, 0, 0, MPI_COMM_WORLD);
        }
    }
// END: T7
}


// Main time integration.
void simulate( void )
{
    // Go through each time step.
    for ( int_t iteration=0; iteration<=max_iteration; iteration++ )
    {
        if ( (iteration % snapshot_freq)==0 )
        {
            if (world_size > 1) {
                send_data_to_root();
            }
            domain_save ( iteration / snapshot_freq );
        }

        // Derive step t+1 from steps t and t-1.
        border_exchange();
        boundary_condition();
        time_step();

        move_buffer_window();
    }
}


int main ( int argc, char **argv )
{
// TASK: T1c
// Initialise MPI
// BEGIN: T1c
    MPI_Init(&argc, &argv);
    MPI_Comm_size(MPI_COMM_WORLD, &world_size);
    MPI_Comm_rank(MPI_COMM_WORLD, &world_rank);

    step = N / world_size;
    extra_work_amount = N - (world_size * step);
    if (world_rank == world_size - 1) {
        extra_work = extra_work_amount;
    }
// END: T1c
    
    struct timeval t_start, t_end;

    domain_initialize();

// TASK: T2
// Time your code
// BEGIN: T2
    if (world_rank == 0) {
        gettimeofday(&t_start, NULL);
    }

    simulate();

    if (world_rank == 0) {
        gettimeofday (&t_end, NULL);
        printf ("Total elapsed time for root process: %lf seconds\n",
                WALLTIME(t_end) - WALLTIME(t_start));
    }
// END: T2
   
    domain_finalize();

// TASK: T1d
// Finalise MPI
// BEGIN: T1d
    MPI_Finalize();
// END: T1d

    exit ( EXIT_SUCCESS );
}
