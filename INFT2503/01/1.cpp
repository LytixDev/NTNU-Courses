#include <cstdio>
#include <iostream>

const int length = 5;

int main(void)
{
    std::printf("Skriv inn %d tall\n", length);

    int low_count = 0, mid_count = 0, high_count = 0;

    double temperature;
    for (int i = 0; i < length; i++) {
        std::printf(">");
        std::cin >> temperature;
        std::printf("temperatur %d er %f\n", i, temperature);

        if (temperature < 10)
            low_count++;
        else if (temperature > 20)
            high_count++;
        else
            mid_count++;
    }

    std::printf("Antall under 10 er %d\n", low_count);
    std::printf("Antall mellom 10 og 20 er %d\n", mid_count);
    std::printf("Antall over 20 er %d\n", high_count);
}
