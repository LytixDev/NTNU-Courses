import torch
import matplotlib.pyplot as plt

with open("day_length_weight.csv") as f:
    data = [i.split(",") for i in f.readlines()]

days = [float(r[1]) for r in data[1:]]
lengths = [float(r[1]) for r in data[1:]]
weights = [float(r[2]) for r in data[1:]]

x_train = torch.tensor([[l, r] for l, r in zip(lengths, weights)]).reshape(-1, 2)
y_train = torch.tensor(days).reshape(-1, 1)

fig = plt.figure(figsize=(10, 6))
ax = fig.add_subplot(projection='3d')

# Scatter plot in 3D space
ax.scatter(days, lengths, weights, c='blue', marker='o', label='Data Points')
ax.set_xlabel('Day')
ax.set_ylabel('Length')
ax.set_zlabel('Weight')
ax.set_title('Day, Length, and Weight')
ax.legend()
plt.show()

class LinearRegressionModel3D:
    def __init__(self):
        # self.W = torch.tensor([[0, 0]], requires_grad=True).reshape(-1, 2)
        self.W = torch.rand((2, 1), requires_grad=True)
        self.b = torch.tensor([[0.0]], requires_grad=True)

    def f(self, x):
        return x @ self.W + self.b
        
    def loss(self, x, y):
        return torch.mean(torch.square(self.f(x) - y))  # Can also use torch.nn.functional.mse_loss(self.f(x), y) to possibly increase numberical stability

model = LinearRegressionModel3D()
optimizer = torch.optim.SGD([model.W, model.b], lr=0.0001)

epochs = 1_000_000
for epoch in range(epochs):
    if epoch % (epochs // 10)  == 0:
        print("Done:", epoch)
    model.loss(x_train, y_train).backward()
    optimizer.step()
    optimizer.zero_grad()

print("DONE !!!")
print("W = %s, b = %s, loss = %s" % (model.W, model.b, model.loss(x_train, y_train)))

# Visualize result
ax = plt.figure().add_subplot(projection="3d")

ax.scatter(x_train[:, 0], x_train[:, 1], y_train, 'o', label='$(x^{(i)},y^{(i)}, z^{(i)})$')
ax.scatter(x_train[:, 0], x_train[:, 1], model.f(x_train).detach(), color="red", label="$\\hat y = f(x) = xW+b$")

ax.set_xlabel("x: Length")
ax.set_ylabel("y: Weight")
ax.set_zlabel("z: Day")

plt.show()
