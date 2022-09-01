#include <stdio.h>
#include <stdint.h>
#include <stdlib.h>
#include <sys/time.h>

#define I32_MIN 1 << 31
#define N 10000


void rand_arr(int32_t *arr, uint32_t n)
{
    /* bruker samme seed, fjerner en variabel fra som kan skape variasjon */
    srand(420);

    for (size_t i = 0; i < n; i++)
        arr[i] = (rand() << 16) % 500;
}

int main()
{
    int32_t prices[N];
    int32_t stonk = 0, best = I32_MIN;
    uint32_t buy, sell;
    struct timeval start, end;

    /* fyll listen med tilfeldig data */
    rand_arr(prices, N);

    /* start tidsmåling */
    gettimeofday(&start, NULL);

    /* naiv "brute force" */
    for (uint32_t i = 0; i < N; i++) {         /* n */
        stonk = 0;
        for (uint32_t j = i; j < N; j++) {     /* n * n */
            stonk += prices[j];
            if (stonk > best) {
                best = stonk;
                buy = i;
                sell = j;
            }
        }
    }

    /* slutt tidsmåling */
    gettimeofday(&end, NULL);

    printf("kjøpsdag: %d, salgsdag: %d, profitt: %d\n", buy, sell + 1, best);
    printf("bruke %f sekunder på n=%d\n", (double)(end.tv_usec - start.tv_usec) / 1000000 +
           (double)(end.tv_sec - start.tv_sec), N);

    /*
     * n = 1000   -> t = 0.0008 s
     * n = 10000  -> t = 0.0889 s
     * n = 100000 -> t = 8.5489s
     */

    return 0;
}
