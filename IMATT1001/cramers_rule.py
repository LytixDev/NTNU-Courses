import numpy as np

A = np.zeros((5,5))
for i in range(5):
    for j in range(5):
        A[i,j]=(i + 1)**(4 - j)

detA = round(np.linalg.det(A))
b = np.array([2, 0, 1, 0, 0])
A_copy = A.copy()
#print(A)
#print(detA)
#print(b)

A_copy[:, 1] = b
detA_2_b = np.linalg.det(A_copy)
x_2 = detA_2_b/detA

print(detA_2_b)
print(x_2)
