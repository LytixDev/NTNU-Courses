#!/bin/sh

set -e

mpicc -o mandel mandel_multi.c
printf "===Compilation success===\n"
mpirun -np 3 ./mandel
