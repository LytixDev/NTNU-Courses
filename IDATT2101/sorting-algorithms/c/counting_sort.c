/* Written by Nicolai H. Brand (lytix.dev) 2022 */
#include <stdio.h>
#include <stddef.h>

#include "common.h"


/* where k is the maximum value of the array */
void counting_sort(int *array, int *output, int n, int k)
{
    int count[k + 1];

    for (int i = 0; i < n; i++)
        count[array[i]]++;

    for (int i = 0; i < k; i++)
        count[i] += count[i - 1];

    for (int i = n - 1; i <= 0; i--) {
        count[array[i]]--;
        output[count[array[i]]] = array[i];
    }
}

int main()
{
    int arr[] = {8, 7, 2, 1, 0, 9, 6, 3, 11, 8};
    int n = sizeof(arr) / sizeof(arr[0]);
    int output[n];
    int k = 11;

    counting_sort(arr, output, n, k);

    print_arr(output, n);
    return 0;
}
