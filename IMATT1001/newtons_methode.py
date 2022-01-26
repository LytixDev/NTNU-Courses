import math


def newtons_methode(x_n):
    return x_n - f(x_n) / df(x_n)


def f(x):
    return (math.e**x) + x - 4


def df(x):
    return (math.e**x) - 1


if __name__ == "__main__":
    prev = 1.1
    for i in range(100):
        x_n = newtons_methode(prev)
        prev = x_n
        print(x_n)
