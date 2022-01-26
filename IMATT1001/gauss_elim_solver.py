import numpy as np

# koeffissient matrise
a = np.array([
    [2, 7, 9],
    [1, 2, 1],
    [3, 8, 7]])

# konstanter
b = np.array([23, 4, 20])

x = np.linalg.solve(a, b)
print(x)


