# written by Nicolai H. Brand - 26.04.2022
import random


#primality test
def is_prime(n: int) -> bool:
    if not n % 2 or not n % 3:
        return False

    i = 5
    stop = int(n**0.5)

    while i <= stop:
        if not n%i or not n%(i + 2):
            return False
        i += 6
    return True


def get_two_distinct_primes(start=100, end=1_000):
    p = _get_distinct_prime(start, end)
    q = _get_distinct_prime(start, end, used=[p])

    return p, q


def _get_distinct_prime(start, end, used=[]):
    candidate = random.randint(start, end)
    while not is_prime(candidate) and candidate not in used:
        candidate = random.randint(start, end)

    return candidate
