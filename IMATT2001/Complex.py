class Complex:
    def __init__(self, x=0, y=0):
        # definition: 
        # z = x + iy, where i is the imaginary unit
        self.x = x
        self.y = y

    def __str__(self):
        return "({0},{1})".format(self.x, self.y)

    def conj(self):
        return Complex(self.x, -self.y)

    def __add__(self, other):
        x = self.x + other.x
        y = self.y + other.y
        return Complex(x,y)

    def __sub__(self, other):
        x = self.x - other.x
        y = self.y - other.y
        return Complex(x,y)

    def __mul__(self, other):
        x = self.x * other.x - self.y * other.y
        y = self.y * other.x + self.x * other.y
        return Complex(x,y)

    def __truediv__(self, other):
        x = (self.x * other.x + self.y * other.y) / (other.x * other.x + other.y * other.y)
        y = (self.y * other.x - self.x * other.y) / (other.x * other.x + other.y * other.y)
        return Complex(x, y)
