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


res = gcd(22, 12)
