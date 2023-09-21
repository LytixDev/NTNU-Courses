#include <cstdio>
#include <iostream>
#include <fstream>


void read_temperatures(double temperatures[], int length);

int main(void)
{
    const int length = 5;

    int low_count = 0, mid_count = 0, high_count = 0;
    double temperatures[length];
    read_temperatures(temperatures, length);

    for (int i = 0; i < length; i++) {
        std::printf("temperatur %d er %f\n", i, temperatures[i]);
        if (temperatures[i] < 10)
            low_count++;
        else if (temperatures[i] > 20)
            high_count++;
        else
            mid_count++;
    }

    std::printf("Antall under 10 er %d\n", low_count);
    std::printf("Antall mellom 10 og 20 er %d\n", mid_count);
    std::printf("Antall over 20 er %d\n", high_count);
}

void read_temperatures(double temperatures[], int length)
{
    std::ifstream fp;
    /* \n seperated file of temperatures */
    fp.open("temperatures");

    if (!fp) {
        std::fprintf(stderr, "could not open file :-(");
        exit(1);
    }

    double temperature;
    for (int i = 0; i < length; i++) {
        fp >> temperature;
        temperatures[i] = temperature;
    }
}
