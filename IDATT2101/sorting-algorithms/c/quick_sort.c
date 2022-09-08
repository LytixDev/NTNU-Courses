/* Written by Nicolai H. Brand (lytix.dev) 2022 */ 
#include <stdio.h>
#include <stddef.h>
#include "common.h"


/* translated from the course material */
static ssize_t median3sort(int *array, ssize_t left, ssize_t right)
{
    /* can this overflow? */
    ssize_t m = (left + right) / 2;

    if (array[left] > array[m])
        swap(&array[left], &array[m]);

    if (array[m] > array[right]) {
        swap(&array[m], &array[right]);
        if (array[left] > array[m])
            swap(&array[left], &array[m]);
    }

    return m;
}

/* translated from the course material */
static ssize_t partition(int *array, ssize_t left, ssize_t right)
{
    ssize_t i_left, i_right;
    ssize_t m = median3sort(array, left, right);
    int pivot = array[m];
    swap(&array[m], &array[right - 1]);

    for (i_left = left, i_right = right - 1;;) {
        while (array[++i_left] < pivot);
        while (array[--i_right] > pivot);
        if (i_left >= i_right)
            break;

        swap(&array[i_left], &array[i_right]);
    }
    swap(&array[i_left], &array[right - 1]);
    return i_left;
}

/* translated from the course material */
void quick_sort(int *array, ssize_t left, ssize_t right)
{
    if (right - left > 2) {
        ssize_t pivot = partition(array, left, right);

        /* recursive call on left side of pivot */
        quick_sort(array, left, pivot - 1);

        /* recursive call on right side of pivot */
        quick_sort(array, pivot + 1, right);
    } else {
        median3sort(array, left, right);
    }
}

int main()
{
    int arr[] = {8, 7, 2, 1, 0, 9, 6, -4, -1, 3, 11, 8, -2};
    ssize_t n = sizeof(arr) / sizeof(arr[0]);
    quick_sort(arr, 0, n - 1);
    //print_arr(arr, n);
    if (is_sorted(arr, n))
        printf("Array sorted\n");
    else
        printf("Array not sorted\n");

}
