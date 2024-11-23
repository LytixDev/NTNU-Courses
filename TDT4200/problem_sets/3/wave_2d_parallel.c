#define _XOPEN_SOURCE 600
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdint.h>
#include <math.h>
#include <sys/time.h>
#include <assert.h>

#include "argument_utils.h"

// TASK: T1a
// Include the MPI headerfile
// BEGIN: T1a
#include <mpi.h>
// END: T1a



// Convert 'struct timeval' into seconds in double prec. floating point
#define WALLTIME(t) ((double)(t).tv_sec + 1e-6 * (double)(t).tv_usec)

    struct timeval T_S, T_E;
    double save = 0;
    double exchange = 0;
    double boundary = 0;
    double step = 0;

    static void time_start()
    {
        gettimeofday(&T_S, NULL);
    }

    static double time_end()
    {
        gettimeofday (&T_E, NULL);
        double time = WALLTIME(T_E) - WALLTIME(T_S);
        T_S = (struct timeval){0};
        T_E = (struct timeval){0};
        return time;
    }

    // Option to change numerical precision
    typedef int64_t int_t;
    typedef double real_t;

    // Custome MPI datatypes
    MPI_Datatype Col_Type;
    MPI_Datatype Row_Type;
    MPI_Datatype File_Type;

    // Cartesian communicator handle
    MPI_Comm cart_comm;

    // Buffers for three time steps, indexed with 2 ghost points for the boundary
    real_t
        *buffers[3] = { NULL, NULL, NULL };

    // TASK: T1b
    // Declare variables each MPI process will need
    // BEGIN: T1b
    int world_size;
    int world_rank;

    // Dimensions of the 2d cartesian communicator. Initialising to 0 lets MPI chose the dimensions
    int dims[2] = {0};

    // Coords of the process in the current cartesian communicator
    int coords[2]; // 0 is m/row/i, 1 is n/col/j

    // Neighbors. Neighbors that are off-grid will become MPI_PROC_NULL.
    int left, right, up, down;

    // END: T1b

    // Simulation parameters: size, step count, and how often to save the state
    int_t
        M = 256,    // rows
        N = 256,    // cols
        max_iteration = 4000,
        snapshot_freq = 20;

    // Macros to get how much work (simulation points/numbers) each process will need to process in each direction
    // NOTE(Nicolai): From the assignment text we can guarantee this division will result in an integer
#define get_local_M() (int_t)(M / dims[0])
#define get_local_N() (int_t)(N / dims[1])

#define my_i() coords[0]
#define my_j() coords[1]

    // NOTE(Nicolai): Modified macros to use the local_N size
#define U_prv(i,j) buffers[0][((i)+1)*(get_local_N()+2)+(j)+1]
#define U(i,j)     buffers[1][((i)+1)*(get_local_N()+2)+(j)+1]
#define U_nxt(i,j) buffers[2][((i)+1)*(get_local_N()+2)+(j)+1]


    // Wave equation parameters, time step is derived from the space step
    const real_t
        c  = 1.0,
        dx = 1.0,
        dy = 1.0;
    real_t
        dt;


    // Helper function to insert an entire row at row_idx
    static void insert_row(real_t *row, int_t row_idx)
    {
        assert(row_idx < get_local_M() + 2);
        int_t local_N = get_local_N();

        real_t *target_row_start = &U(row_idx - 1, -1);//&get_row_first_elem(1);
        memcpy(target_row_start, row, (local_N + 2) * sizeof(real_t));
    }

    // Helper function to insert an entire column at col_idx
    void insert_col(real_t *col, int_t col_idx)
    {
        assert(col_idx < get_local_N() + 2);
        int_t local_M = get_local_M();
        int_t local_N = get_local_N();

        // Insert the column into the buffer
        for (int_t i = 0; i < local_M + 2; i++) {
            // RIP cache locality :-(
            buffers[1][i * (local_N + 2) + col_idx] = col[i];
        }
    }

    // Pack a column into contiguous memory
    // NOTE(Nicolai): Ideally I would be able to use an MPI datatype to automatically do this.
    //                After bickering with it for some time, I had to give up, and do the
    //                simple solution isntead.
    void pack_col_contigious(real_t col_out[get_local_M()], int_t col_idx)
    {
        int_t local_M = get_local_M();
        int_t local_N = get_local_N();
        for (int_t i = 0; i < local_M + 2; i++) {
            col_out[i] = buffers[1][i * (local_N + 2) + col_idx];
        }
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
    // Set up our three buffers, and fill two with an initial perturbation
    // and set the time step.
    void domain_initialize ( void )
{
// BEGIN: T4
    int_t local_M = get_local_M();
    int_t local_N = get_local_N();

    buffers[0] = malloc ( (local_M+2)*(local_N+2)*sizeof(real_t) );
    buffers[1] = malloc ( (local_M+2)*(local_N+2)*sizeof(real_t) );
    buffers[2] = malloc ( (local_M+2)*(local_N+2)*sizeof(real_t) );

    for ( int_t i=0; i<local_M; i++ )
    {
        for ( int_t j=0; j<local_N; j++ )
        {
            // Find the actual i and j values based on the position of the process in the grid
            int_t calculation_i = i + local_M * my_i();
            int_t calculation_j = j + local_N * my_j();

            // Calculate delta (radial distance) adjusted for M x N grid
            real_t delta = sqrt ( ((calculation_i - M/2.0) * (calculation_i - M/2.0)) / (real_t)M +
                                  ((calculation_j - N/2.0) * (calculation_j - N/2.0)) / (real_t)N );

            U_prv(i,j) = U(i,j) = exp ( -4.0*delta*delta );
        }
    }

    // Set the time step for 2D case
    dt = dx*dy / (c * sqrt (dx*dx+dy*dy));
// END: T4
}

// Get rid of all the memory allocations
void domain_finalize ( void )
{
    free ( buffers[0] );
    free ( buffers[1] );
    free ( buffers[2] );
}


// TASK: T5
// Integration formula
void time_step ( void )
{
// BEGIN: T5
    int_t local_M = get_local_M();
    int_t local_N = get_local_N();

    for ( int_t i=0; i<local_M; i++ )
    {
        for ( int_t j=0; j<local_N; j++ )
        {
            U_nxt(i,j) = -U_prv(i,j) + 2.0*U(i,j)
                     + (dt*dt*c*c)/(dx*dy) * (
                        U(i-1,j)+U(i+1,j)+U(i,j-1)+U(i,j+1)-4.0*U(i,j)
                    );
        }
    }
// END: T5
}

// TASK: T6
// Communicate the border between processes.
void border_exchange ( void )
{
    // NOTE(Nicolai): Some reflections on this:
    //                1. I use blocking I/O. I think I have set it up in a way to avoid deadlocks,
    //                   but I can't prove it.
    //                2. Columns are first packed into a VLA before being sent. It would be
    //                   prefereable to setup an MPI datatype to do this automatically, but I could
    //                   not figure it out in time.
    int_t local_M = get_local_M();
    int_t local_N = get_local_N();

    // Send row 1 upwards
    if (up != MPI_PROC_NULL) {
        real_t *to_up = &U(0, -1);
        MPI_Send(to_up, 1, Row_Type, up, 0, cart_comm);
    }
    // Recvieve row 1 from downwards neighbour
    if (down != MPI_PROC_NULL) {
        real_t row_from_down[local_N + 2];
        MPI_Recv(row_from_down, 1, Row_Type, down, 0, cart_comm, MPI_STATUS_IGNORE);
        insert_row(row_from_down, local_M + 1);
    }

    // Send row M to down neighbour
    if (down != MPI_PROC_NULL) {
        real_t *to_down = &U(local_M - 1, -1);
        MPI_Send(to_down, 1, Row_Type, down, 0, cart_comm);
    }
    // Recvieve
    if (up != MPI_PROC_NULL) {
        real_t row_from_up[local_N + 2];
        MPI_Recv(row_from_up, 1, Row_Type, up, 0, cart_comm, MPI_STATUS_IGNORE);
        insert_row(row_from_up, 0);
    }

    // Send column 1 to left
    if (left != MPI_PROC_NULL) {
        real_t to_left[local_M + 2];
        pack_col_contigious(to_left, 1);
        MPI_Send(to_left, 1, Col_Type, left, 0, cart_comm);
    }
    // Recvieve
    if (right != MPI_PROC_NULL) {
        real_t col_from_right[local_M + 2];
        MPI_Recv(col_from_right, 1, Col_Type, right, 0, cart_comm, MPI_STATUS_IGNORE);
        insert_col(col_from_right, local_N + 1);
    }

    // Send column N to left
    if (right != MPI_PROC_NULL) {
        real_t to_right[local_M + 2];
        pack_col_contigious(to_right, local_N);
        MPI_Send(to_right, 1, Col_Type, right, 0, cart_comm);
    }
    // Recvieve
    if (left != MPI_PROC_NULL) {
        real_t col_from_left[local_M + 2];
        MPI_Recv(col_from_left, 1, Col_Type, left, 0, cart_comm, MPI_STATUS_IGNORE);
        insert_col(col_from_left, 0);
    }
// END: T6
}

// TASK: T7
// Neumann (reflective) boundary condition
void boundary_condition ( void )
{
// BEGIN: T7
    int_t local_M = get_local_M();
    int_t local_N = get_local_N();

    // Left boundary
    if (my_j() == 0) {
        for (int_t i = 0; i < local_M; i++) {
            U(i, -1) = U(i, 1);
        }
    }
    // Right boundary
    if (my_j() == dims[1] - 1) {
        for (int_t i = 0; i < local_M; i++) {
            U(i, local_N) = U(i, local_N - 2);
        }
    }
    // Up boundary
    if (my_i() == 0) {
        for (int_t i = 0; i < local_N; i++) {
            U(-1, i) = U(1, i);
        }
    }
    // Down boundary
    if (my_i() == dims[0] - 1) {
        for (int_t i = 0; i < local_N; i++) {
            U(local_M, i) = U(local_M - 2, i);
        }
    }
// END: T7
}


// TASK: T8
// Creates a custom MPI datatype to describe the portion of the file which each rank is 
// "responsible" for and can write contiguously to (MPI will place the data properly). 
void file_type_initialize(void)
{
    int_t local_M = get_local_M();
    int_t local_N = get_local_N();
    
    MPI_Type_create_subarray(2, (int[]){M, N}, (int[]){local_M, local_N}, 
                             (int[]){my_i() * local_M, my_j() * local_N}, 
                             MPI_ORDER_C, MPI_DOUBLE, &File_Type);
    MPI_Type_commit(&File_Type);
}

// Save the present time step in a numbered file under 'data/'
// OLD domain_save
#if 0
void domain_save ( int_t step )
{
// BEGIN: T8
    char filename[256];
    sprintf ( filename, "data/%.5ld.dat", step );

    int_t local_M = get_local_M();
    int_t local_N = get_local_N();

    MPI_File fp;
    MPI_File_open(cart_comm, filename, MPI_MODE_CREATE | MPI_MODE_WRONLY, MPI_INFO_NULL, &fp);

    // A file view tells MPI how each process will see the file.
    MPI_File_set_view(fp, 0, MPI_DOUBLE, File_Type, "native", MPI_INFO_NULL);

    // NOTE(Nicolai): I would prefer to write everything in one single write call.
    //                Intuitively, I suspect this would be better for performance.
    //                Due to to ghost pointers I found this difficult.
    // Write each row, skipping ghost elements at the start and end of each row
    for (int_t i = 1; i <= local_M; i++) {
        MPI_File_write_all(fp, &buffers[1][i * (local_N + 2) + 1], local_N, MPI_DOUBLE, MPI_STATUS_IGNORE);
    }

    MPI_File_close(&fp);
// END: T8
}
#endif

void domain_save ( int_t step )
{
    char filename[256];
    sprintf ( filename, "data/%.5ld.dat", step );

    int_t local_M = get_local_M();
    int_t local_N = get_local_N();

    MPI_File fp;
    MPI_File_open(cart_comm, filename, MPI_MODE_CREATE | MPI_MODE_WRONLY, MPI_INFO_NULL, &fp);

    // A file view tells MPI how each process will see the file.
    MPI_File_set_view(fp, 0, MPI_DOUBLE, File_Type, "native", MPI_INFO_NULL);

    // Datatype that represents the data to be written (excluding ghost cells)
    MPI_Datatype Mem_Type;
    int mem_sizes[2] = {local_M + 2, local_N + 2};
    int mem_subsizes[2] = {local_M, local_N};
    int mem_starts[2] = {1, 1}; // Start AFTER the ghost cells

    MPI_Type_create_subarray(2, mem_sizes, mem_subsizes, mem_starts, MPI_ORDER_C, MPI_DOUBLE, &Mem_Type);
    MPI_Type_commit(&Mem_Type);

    MPI_File_write_at_all(fp, 0, buffers[1], 1, Mem_Type, MPI_STATUS_IGNORE);

    MPI_Type_free(&Mem_Type);
    MPI_File_close(&fp);
}


// Main time integration.
void simulate( void )
{
    // Go through each time step
    for ( int_t iteration=0; iteration<=max_iteration; iteration++ )
    {
        if ( (iteration % snapshot_freq)==0 )
        {
            if (world_rank == 0) {
                time_start();
            }
            domain_save ( iteration / snapshot_freq );
            if (world_rank == 0) {
                save += time_end();
            }
        }

        // Derive step t+1 from steps t and t-1
        if (world_rank == 0) {
            time_start();
        }
        border_exchange();
        if (world_rank == 0) {
            exchange += time_end();
        }

        if (world_rank == 0) {
            time_start();
        }
        boundary_condition();
        if (world_rank == 0) {
            boundary += time_end();
        }

        if (world_rank == 0) {
            time_start();
        }
        time_step();
        if (world_rank == 0) {
            step += time_end();
        }

        // Rotate the time step buffers
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
// END: T1c

// TASK: T3
// Distribute the user arguments to all the processes
// BEGIN: T3
    OPTIONS opts;
    if (world_rank == 0) {
        OPTIONS *options = parse_args( argc, argv );
        if ( !options )
        {
            fprintf( stderr, "Argument parsing failed\n" );
            exit( EXIT_FAILURE );
        }

        opts = *options;
        free(options);
    }

    // NOTE(Nicolai): Since OPTIONS is just 4 int_t's packed together, we can just pretend its a list
    //                / buffer of int_t's. No fancy stuff needed. :-) 
    assert(sizeof(int_t) == 8 && "Sizeof int_t must be 8 (aka long aka MPI_LONG)");
    MPI_Bcast(&opts, 4, MPI_LONG, 0, MPI_COMM_WORLD);
    M = opts.M;
    N = opts.N;
    max_iteration = opts.max_iteration;
    snapshot_freq = opts.snapshot_frequency;

    // Setup the 2D cartesian communicator
    int periods[2] = {0}; // Initialising to 0 means no wrap
    MPI_Dims_create(world_size, 2, dims); // Creates the 2d-grid based on the num of processes
    MPI_Cart_create(MPI_COMM_WORLD, 2, dims, periods, 0, &cart_comm);

    // Get the coordinates of the current process
    MPI_Cart_coords(cart_comm, world_rank, 2, coords);

    // Get the neighbors of the current process
    MPI_Cart_shift(cart_comm, 0, 1, &up, &down); // First dimension
    MPI_Cart_shift(cart_comm, 1, 1, &left, &right); // Second dimension
    // printf("[%d] i: %d j: %d : up %d down %d left %d right %d\n", world_rank, my_i(), my_j(), up, down, left, right);
// END: T3

    // Create custom MPI datatypes to make sending and reciving rows & cols easier
    int_t local_M = get_local_M();
    int_t local_N = get_local_N();
    MPI_Type_contiguous(local_M + 2, MPI_DOUBLE, &Col_Type);
    MPI_Type_commit(&Col_Type);

    MPI_Type_contiguous(local_N + 2, MPI_DOUBLE, &Row_Type);
    MPI_Type_commit(&Row_Type);

    // Initializes the MPI datatype `File_Type`
    file_type_initialize();

    // Set up the initial state of the domain
    domain_initialize();

    struct timeval t_start, t_end;

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

        printf("Time spent in:\n border_exchange %lf\n boundary_condition %lf\n time_step %lf\n save %lf\n",
                exchange, boundary, step, save);
    }
// END: T2

    // Clean up and shut down
    domain_finalize();

// TASK: T1d
// Finalise MPI
// BEGIN: T1d
    MPI_Comm_free(&cart_comm);
    MPI_Type_free(&Col_Type);
    MPI_Type_free(&Row_Type);
    MPI_Type_free(&File_Type);
    MPI_Finalize();
// END: T1d

    exit ( EXIT_SUCCESS );
}
