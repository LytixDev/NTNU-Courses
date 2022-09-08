/* Written by Nicolai H. Brand (lytix.dev) 2022
 * The single pivot quicksort implementation is taken from the course material.
 * The dual pivot quicksort is a slighly modified version taken from:
 * GeeksForGeeks https://www.geeksforgeeks.org/dual-pivot-quicksort/
 */
#include <stdio.h>
#include <stdbool.h>
#include <stdint.h>
#include <stddef.h>
#include <stdlib.h>
#include <sys/time.h>
#include <time.h>
#include <assert.h>

#define NANO 1 / 1000000000.0
#define MILLION 1000000
#define MEGABYTE 1048576


static int32_t checksum(void *data, size_t bytes)
{
    int32_t checksum = 0;
    for (size_t i = 0; i < bytes; i++)
        checksum += ((int32_t *)data)[i];

    return checksum;
}

static bool is_sorted(int *array, size_t n)
{
    for (size_t i = 0; i < n - 1; i++)
        if (array[i] > array[i + 1])
            return false;

    return true;
}

static void rand_arr(int *arr, size_t n)
{
    srand(time(NULL));

    for (size_t i = 0; i < n; i++)
        arr[i] = (rand() << 16);
}

static void duplicates(int *arr, size_t n)
{
    srand(time(NULL));

    for (size_t i = 0; i < n; i++)
        arr[i] = (rand() << 16) % 10;
}

static void swap(int *a, int *b)
{
    int tmp = *a;
    *a = *b;
    *b = tmp;
}

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

static ssize_t dp_partition(int *arr, ssize_t low, ssize_t high, ssize_t *lp)
{
    if (arr[low] > arr[high])
        swap(&arr[low], &arr[high]);

    // p is the left pivot, and q is the right pivot.
    ssize_t j = low + 1;
    ssize_t g = high - 1, k = low + 1;
    ssize_t p = arr[low], q = arr[high];

    while (k <= g) {
        // if elements are less than the left pivot
        if (arr[k] < p) {
            swap(&arr[k], &arr[j]);
            j++;
        }
        // if elements are greater than or equal
        // to the right pivot
        else if (arr[k] >= q) {
            while (arr[g] > q && k < g)
                g--;
            swap(&arr[k], &arr[g]);
            g--;
            if (arr[k] < p) {
                swap(&arr[k], &arr[j]);
                j++;
            }
        }
        k++;
    }
    j--;
    g++;
 
    // bring pivots to their appropriate positions.
    swap(&arr[low], &arr[j]);
    swap(&arr[high], &arr[g]);
 
    // returning the indices of the pivots.
    *lp = j; // because we cannot return two elements
    // from a function.
    return g;
}

void dual_pivot_qsort(int *arr, ssize_t low, ssize_t high)
{
    if (low < high) {
        /* lp means left pivot, and rp means right pivot */
        ssize_t lp, rp;
        rp = dp_partition(arr, low, high, &lp);
        dual_pivot_qsort(arr, low, lp - 1);
        dual_pivot_qsort(arr, lp + 1, rp - 1);
        dual_pivot_qsort(arr, rp + 1, high);
    }
}

typedef enum sort_alg {
    QSORT,
    DUAL_PIVOT_QSORT,
} sort_alg;

const char *sort_alg_str[] = {
    "quicksort",
    "dual pivot quicksort"
};

void run(int *arr, size_t n, enum sort_alg sa, bool silent)
{
    bool has_allocated = false;
    if (arr == NULL) {
        arr = (int *)malloc(n * sizeof(int));
        //printf("Allocated %.2f megabytes\n", (n * sizeof(int)) / (double)MEGABYTE);
        rand_arr(arr, n);
        has_allocated = true;
    }

    int32_t checksum_pre = checksum(arr, n);

    /* for timing */
    struct timespec start, end;
    double elapsed;
    clock_gettime(CLOCK_MONOTONIC, &start);

    if (sa == QSORT)
        quick_sort(arr, 0, n - 1);
    else if (sa == DUAL_PIVOT_QSORT)
        dual_pivot_qsort(arr, 0, (ssize_t)n - 1);
    else
        goto cleanup;

    clock_gettime(CLOCK_MONOTONIC, &end);
    elapsed = (end.tv_sec - start.tv_sec) + (end.tv_nsec - start.tv_nsec) / (double)NANO;
    int32_t checksum_post = checksum(arr, n);

    if (!silent && false) {
        if (checksum_pre == checksum_post && is_sorted(arr, n))
            printf("Array with size %zu million successfully sorted in %fs using %s\n", n / MILLION, elapsed, sort_alg_str[sa]);
        else
            fprintf(stderr, "Array with size %zu million unsuccessfully sorted using %s\n", n / MILLION, sort_alg_str[sa]);
    }

cleanup:
    if (!silent)
        putchar('\n');
    if (has_allocated)
        free(arr);
}

int main()
{
    size_t n, N;
    /* complete random arrays */
    for (n = 1; n <= 10; n++) {
        N = n * 10 * MILLION;
        run(NULL, N, DUAL_PIVOT_QSORT, false);
        run(NULL, N, QSORT, false);
    }

    /* using arrays with many duplicates */
    for (n = 1; n <= 10; n++) {
        N = n * 10 * MILLION;
        int *arr = (int *)malloc(N * sizeof(int));
        duplicates(arr, N);
        run(arr, N, DUAL_PIVOT_QSORT, false);
        /* reshuffle array */
        duplicates(arr, N);
        run(arr, N, QSORT, false);
        free(arr);
    }

    /* using already sorted arrays */
    for (n = 1; n <= 10; n++) {
        N = n * 10 * MILLION;
        int *arr = (int *)malloc(N * sizeof(int));
        /* sort array silently */
        run(arr, N, DUAL_PIVOT_QSORT, true);

        /* perform quicksort on sorted arrays */
        assert(is_sorted(arr, N) == true);
        run(arr, N, DUAL_PIVOT_QSORT, false);

        assert(is_sorted(arr, N) == true);
        run(arr, N, QSORT, false);
        free(arr);
    }
}
