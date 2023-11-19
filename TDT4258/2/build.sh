#!/bin/sh
CFLAGS="-Wall -Wpedantic -Wextra -Wshadow -std=gnu11 -lm -g"
gcc $CFLAGS -o cache_sim cache_sim.c
