#include <stdio.h>
#include <stdlib.h>

int var1 = 0;

int main()
{
    int var2 = 1;
    int *var3 = (int *)malloc(sizeof(int));
    *var3 = 2;
    printf("Address: %x; Value: %d\n", &var1, var1);
    printf("Address: %x; Value: %d\n", &var2, var2);
    printf("Address: %x; Address: %x; Value: %d\n", &var3, var3, *var3);
}

