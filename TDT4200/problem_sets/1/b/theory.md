# TDT4200 - PS 1b - Nicolai H. Brand


### 1

T :: time
T_s :: time to run all operations in sequence
f :: fraction of sequential operations
p :: number of processors

T_s = f * T + (1 - f) * T

Assuming that the prallel operations can be evenly distributed among the processors, we get the formula for parallel execution time:

T_p = f * T + ((1 - f) * T) / p

Speedup is defined as the slow solution divided by the fast solution:

Speedup = T_slow / T_fast

In our case, we assume the fast solution should the solution that exploits parallelism.

Speedup = T_s / T_p
        = (f * T + (1 - f) * T) /  (f * T + ((1 - f) * T) / p)

Now for this particular task, there appear to be no sequential dependencies. Each pixel can be calculated independently of the others. As such we can calculate what happens to the speedup equation when f approaches 0 (lim f -> 0). Doing the math we get:

T / T/p which becomes p.

In other words, the speed up is linear S(p) = p.

Of course, this is not totally correct because 1. f is not actually 0 (but rather very small), and 2. when we increase p we actually increase f since the code for sending and receiving data is blocking.

I did some testing. The calcualte function took:
```
Calculate took 0.644983 seconds for rank 1
Calculate took 0.645744 seconds for rank 0
Calculate took 0.647996 seconds for rank 2
```
for the parallel program, while taking:
```
Calculate took 1.784329 seconds
```
for the completely sequential program. 

Assuming each rank is ran simultaniously on different cores, we achieve a 2.75x speedup which aligns with what we expect (S(p) = p, which becomes 3x in our example with 3 processors).

### 2

Strong scaling measures how the runtime of a decreases as the number of processors increases, while weak scaling looks at how the runtime changes when the problem size and processors increase.


### 3
SIMD means Single Instruction Multiple Data, while SPMD means Single Program Multiple Data. The key difference lies in their names of course. While SIMD takes one instruction and applies it to multiple data, SPMD takes the entire program and applies it to multiple data. The latter can be used to distribute a larger dataset over multiple processors. This is the idea behind MPI. Run the same program on multiple processes which in turn can be ran on multiple processors in parallel.
