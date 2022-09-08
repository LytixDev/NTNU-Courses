/* Written by Nicolai H. Brand (lytix.dev) 2022 */
#ifndef COMMON_H
#define COMMON_H

#include <stddef.h>
#include <stdio.h>
#include <stdbool.h>

/* functions definitions in header file, yolo */
void print_arr(int *arr, size_t n)
{
    for (size_t i = 0; i < n; i++)
        printf("[%zu] %d\n", i, arr[i]);

    putchar('\n');
}

void swap(int *a, int *b)
{
    int tmp = *a;
    *a = *b;
    *b = tmp;
}

bool is_sorted(int *array, size_t n)
{
    for (size_t i = 0; i < n - 1; i++)
        if (array[i] > array[i + 1])
            return false;

    return true;
}

#endif /* COMMON_H */
