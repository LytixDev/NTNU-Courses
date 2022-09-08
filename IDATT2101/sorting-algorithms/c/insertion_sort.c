/* Written by Nicolai H. Brand (lytix.dev) 2022 */
#include <stdio.h>
#include <stddef.h>

#include "common.h"


void insertion_sort(int *array, size_t n)
{
    size_t j;
    int h;
    for (size_t i = 1; i < n; i++) {
        h = array[i];
        j = i - 1;

        while (j >= 0 && array[j] > h) {
            swap(&array[j + 1], &array[j]);
            j--;
        }

        array[j + 1] = h;
    }
}

int main()
{
    int a[] = { 4, 9, 2, 1, 8, 3 };
    size_t n = sizeof(a) / sizeof(a[0]);
    insertion_sort(a, n);
    print_arr(a, n);
}
