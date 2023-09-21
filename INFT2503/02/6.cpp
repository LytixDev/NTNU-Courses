#include <cstdio>


int find_sum(const int *table, int length)
{
    int sum = 0;
    for (int i = 0; i < length; i++)
        sum += table[i];

    return sum;
}

int main(void)
{
    int table[] = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
                    11, 12, 13, 14, 15, 16, 17, 18, 19, 20 };
    
    int sum = find_sum(table, 10);
    std::printf("sum 0-10: %d\n", sum);

    sum = find_sum(table + 10, 5);
    std::printf("sum 10-5: %d\n", sum);

    sum = find_sum(table + 15, 5);
    std::printf("sum 15-20: %d\n", sum);
}
