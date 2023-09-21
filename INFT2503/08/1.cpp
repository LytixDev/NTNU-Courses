#include <assert.h>

template <typename Type>
bool equal(Type a, Type b) {
    return a == b;
}

bool equal(double a, double b) {
    double tolerance = 0.00001;
    return a - b > tolerance || a - b > -tolerance;
}

int main(void) {
    assert(equal(1, 1));
    assert(equal(true, true));
    assert(!equal(false, true));
    assert(!equal(11, -1));

    assert(equal(3.1415, 3.1415));
    assert(equal(3.14150, 3.14151));
    assert(!equal(3.14150, 3.14152));
}
