#include <stdio.h>
#include <stdint.h>

#define I32_MIN 1 << 31

int main()
{
    int32_t prices[] = {-1, 3, -9, 2, 2, -1, 2, -1};
    int32_t len = 8;
    int32_t buy, sell, stonk = 0, best = I32_MIN;

    for (int32_t i = 0; i < len; i++) {         /* n */
        stonk = 0;
        for (int32_t j = i; j < len; j++) {     /* n * n */
            stonk += prices[j];
            if (stonk > best) {
                best = stonk;
                buy = i;
                sell = j;
            }
        }
    }

    printf("kjøpsdag: %d, salgsdag: %d, profit: %d\n", buy, sell + 1, best);
    return 0;
}
