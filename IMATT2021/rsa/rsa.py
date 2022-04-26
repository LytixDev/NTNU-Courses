# uses Euler's totient function and not Carmichael's totient function
# written by Nicolai H. Brand - 26.04.2022
import random
import math

import keygen


E_LOWER_BOUND = 1


def _chose_public_e(upper_bound: int) -> int:
    e = random.randint(E_LOWER_BOUND, upper_bound)

    while math.gcd(e, upper_bound) != 1:
        e = random.randint(E_LOWER_BOUND, upper_bound)

    return e


# return message as list of ascii values
def _str_to_int(message: str) -> list[int]:
    return [ord(c) for c in message]
    

def _int_to_str(ascii_message: list[int]) -> str:
    return "".join(chr(c) for c in ascii_message)


# returns a list of encrypted ascii characters int int format
def encrypt(message: str, public_key: tuple[int, int]) -> list[int]:
    n, e = public_key
    msg = _str_to_int(message)
    c = [(m ** e) % n for m in msg]

    return c


def decrypt(encrypted_message: list[int], private_key: tuple[int, int]) -> str:
    n, d = private_key
    m = [(c ** d) % n for c in encrypted_message]
    print(m)
    decrypted_msg = _int_to_str(m)
    return decrypted_msg


def test(msg_to_encrypt: str, verbose=False) -> str:
    p, q = keygen.get_two_distinct_primes()
    n = p * q
    totient = (p-1) * (q-1)
    e = _chose_public_e(totient)
    d = pow(e, -1, totient)

    public_key = (n, e)
    private_key = (n, d)

    # discard p, q and totient
    # dont know if this step is necessary as garbage collection should remove it for us
    #print("p, q, totient, d")
    #print(p, q, totient, d)
    p = q = totient = None

    cipher_text = encrypt(msg_to_encrypt, public_key)
    msg = decrypt(cipher_text, private_key)

    if verbose:
        print("To encrypt: ", msg_to_encrypt)
        print("Cipher text: ", cipher_text)
        print("Decrypted cipher text: ", msg)

    return msg


def main():
    msg_to_encrypt = "hello"
    res = test(msg_to_encrypt, verbose=True)
    assert msg_to_encrypt == res


if __name__ == "__main__":
    main()
