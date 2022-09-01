/*
 * Written by Nicolai H. Brand <lytix.dev> 2022  
 * Build: cc oblig2.c -lm
 */
#include <stdio.h>
#include <math.h>
#include <sys/time.h>

#define ITER 1000


double my_pow1(double x, size_t n)
{
    if (n == 0)
        return 1;

    return x * my_pow1(x, --n);
}

double my_pow2(double x, size_t n)
{
    if (n == 0)
        return 1;
    
    if (n % 2 == 0)
        return my_pow2(x * x, n / 2);

    return x * my_pow2(x * x, (n - 1) / 2);
}

void test_case(double x, size_t n)
{
    double res1, res2, res3;
    res1 = my_pow1(x, n);
    res2 = my_pow2(x, n);
    res3 = pow(x, n);
    printf("%f^%zu = %f = %f = %f\n", x, n, res1, res2, res3);
}

void performance(double x, size_t n, double (*pow_func)(double, size_t))
{
    struct timeval start, end;
    double time_diff;
    /* start måling */
    gettimeofday(&start, NULL);

    for (size_t i = 0; i < ITER; i++)
        pow_func(x, n);

    /* stopp måling */
    gettimeofday(&end, NULL);

    time_diff = (double)(end.tv_usec - start.tv_usec) / 1000000 + 
                (double)(end.tv_sec - start.tv_sec);
    printf("%f^%zu took %fs for %d iterations\n", x, n, time_diff, ITER);
}

int main()
{
    /*
    test_case(4.8, 11);
    test_case(10, 3);
    test_case(1.001, 5000);
    */

    double x = 1.001;
    size_t exponent = 10;

    for (int i = 2; i < 8; i++) {
        exponent = pow(10, i);
        printf("2.1-1\t\t");
        performance(x, exponent, my_pow1);

        printf("2.2-3\t\t");
        performance(x, exponent, my_pow2);

        printf("libc pow\t");
        /*
         * pow() tar egentlig en double som andre parameter, men vi bruker
         * en implisitt konversjon til int slik at det passer bedre med sammenligningen.
         */
        performance(x, exponent, pow);

        putchar('\n');
    }
}

