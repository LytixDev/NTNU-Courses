import math


def f(x):
    return 2*math.pi * x * (math.sqrt(x)+2-x)


def riemann_sum(n, dx, typer="upper"):
    if typer == "lower":
        prev = dx
    elif typer == "mid":
        prev = dx / 2
    else:
        prev = 0

    sum_r= 0
    for _ in range(n):
        sum_r += f(prev)
        prev += dx

    sum_r *= dx
    return sum_r


def main():
    interval = (0, 4)
    n = 20
    dx = (interval[1] - interval[0]) / n
    sum_upper = riemann_sum(n, dx)
    sum_lower = riemann_sum(n, dx, typer="lower")
    sum_mid = riemann_sum(n, dx, typer="mid")
    #print(sum_upper)
    #print(sum_lower)
    print("Med n=20, er arealet:")
    print(sum_mid)

if __name__ == "__main__":
    main()
