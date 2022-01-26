import math

def f(x):
    return math.sqrt(1+abs(x))

def derivert(x, h):
    return (f(x+h) - f(x)) / h


print(derivert(-2, 0.00000001))
