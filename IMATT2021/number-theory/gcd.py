def gcd(a, b):
    while a != 0 and b != 0:
        if a > b:
            c = b
            b = a
            a = c

        b -= a

    if a == 0:
        return b
    
    return a


assert 3 == gcd(15, 12)
assert 2 == gcd(22, 12)
assert 1 == gcd(192, 11)
