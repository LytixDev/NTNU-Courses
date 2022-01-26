import numpy as np
from scipy.linalg import lu


def til_trappeform(A):
    return lu(A, permute_l=True)[1]


def solve(A: np.ndarray):
    a = A[:, 0:-1]
    b = A[:, -1]

    return np.linalg.solve(a, b)


if __name__ == "__main__":
    A = np.array([
        [3, 2, -1, 6],
        [2, -1, 5, .4],
        [1, 0, 2, -1]])
    soln = solve(A)
    #soln = til_trappeform(A)
    print(soln)
