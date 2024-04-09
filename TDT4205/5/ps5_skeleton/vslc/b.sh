#!/bin/bash
cmake --build build
./build/vslc -c < vsl_programs/ps5-codegen1/"$1".vsl > out.S
gcc out.S -o out
