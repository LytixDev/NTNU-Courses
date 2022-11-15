#!/bin/sh

FILES="1.c"
OUT="a.out"
PREPROCESSED_FILES_DIR="preprocessed"
BUILD_CMD="gcc -o $OUT $FILES -D PREPROCESSED_DIR=\"$PREPROCESSED_FILES_DIR\" -D VERBOSE"

mkdir -p "$PREPROCESSED_FILES_DIR" && echo "mkdir -p $PREPROCESSED_FILES_DIR"

if [ "$1" = "debug" ]
then
    $BUILD_CMD -g -D DEBUG && echo "$BUILD_CMD -g -D DEBUG"
else
    $BUILD_CMD -O3 && echo "$BUILD_CMD -O3 -D VERBOSE"
fi
