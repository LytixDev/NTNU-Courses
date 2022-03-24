#!/bin/env python
import math

DIGITS = 5

def average(measurements: list) -> float:
    return round(sum(measurements) / len(measurements), DIGITS)


def standard_deviation(measurements: list) -> float:
    std_dev = 0
    avg = average(measurements)
    for i in measurements:
        std_dev += (i - avg) ** 2

    std_dev /= len(measurements) - 1
    std_dev = math.sqrt(std_dev)
    return round(std_dev, DIGITS)


def standard_error(measurements: list) -> float:
    return round(standard_deviation(measurements) / math.sqrt(len(measurements)), DIGITS)


def main():
    test_input = [2.3, 2.2, 2, 2.2, 2.1, 2.2]
    print(f"Input data: {test_input}")
    print(f"Average: {average(test_input)}")
    print(f"Standard deviation: {standard_deviation(test_input)}")
    print(f"Standard error: {standard_error(test_input)}")


if __name__ == "__main__":
    main()
