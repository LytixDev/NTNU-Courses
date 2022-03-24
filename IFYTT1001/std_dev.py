#!/bin/env python
# written by Nicolai H. Brand

import math

DIGITS = 3


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
