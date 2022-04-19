# automating a tedious task

def S1(x):
    return [1 + x * 2, 2 + x * -1]

def S2(x):
    return [2 + x * -4, 2 + x * 2]

def S3(x):
    return [x * -1, 3 + x * 0.5]

for t in range(-5, 5):
    for s in range(-5, 5):
        for k in range(-5, 5):
            if S1(t) == S2(s) == S3(k):
                print("e")
            elif S2(s) == S3(k):
                print("d")
            elif S1(t) == S3(k):
                print("c")
            elif S1(t) == S2(s):
                print("b")

