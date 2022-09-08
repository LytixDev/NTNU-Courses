/* Written by Nicolai H. Brand (lytix.dev) 2022 */
#include <stdio.h>
#include <stddef.h>

#include "common.h"

 
/* O(n^2) */
void bubble_sort(int *arr, int n)
{
    for (int i = 0; i < n - 1; i++)
        for (int j = 0; j < n - i - 1; j++)
            if (arr[j] > arr[j + 1])
                swap(&arr[j], &arr[j + 1]);
}

int main()
{
    int a[] = { 2, 1, 8, 3 };
    bubble_sort(a, 4);
    print_arr(a, 4);
}
