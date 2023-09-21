#include <cstdio>
#include <iostream>

int main(void)
{
    int i = 3;
    int j = 5;
    int *p = &i;
    int *q = &j;

    std::printf("i addr = %p, val = %d\n", &i, i);
    std::printf("j addr = %p, val = %d\n", &j, j);
    std::printf("p addr = %p, val = %d\n", p, *p);
    std::printf("q addr = %p, val = %d\n", q, *q);

    // p og i er nå 7
    *p = 7;
    // q og j er nå 9
    *q += 4;
    // q og j er nå 8
    *q = *p + 1;
    // i, j, p, q er nå 8
    p = q;
    std::cout << *p << " " << *q << std::endl;
}

