import numpy as np
import math
from matplotlib import pyplot as plt


def f(x, y):
    return y

def main():
    x_0 = 0
    y_0 = 1
    b = 1
    n = 10
    ant_des = 5
    h = (b - x_0) / n  # steg lengde

    x_vector = np.linspace(x_0, b, n + 1)
    y_vector = np.zeros(len(x_vector))
    y_vector[0] = y_0

    for i in range(n):
        y_vector[i+1] = y_vector[i] + h * f(x_vector[i], y_vector[i])

    print(round(y_vector[-1], ant_des))
    plt.plot(x_vector, y_vector)
    plt.grid(True)
    plt.show()


if __name__ == "__main__":
    main()
