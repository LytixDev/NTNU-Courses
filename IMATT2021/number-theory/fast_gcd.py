# faster and recursive !


def gcd(a, b):
    # where b >= a
    if a == 0:
        return b
    if b == 0:
        return a

    r = b % a
    return gcd(r, a)


assert 3 == gcd(15, 12)
assert 2 == gcd(22, 12)
assert 1 == gcd(192, 11)
