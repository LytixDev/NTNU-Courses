#include <iostream>
#include "set.hpp"


int main(void) {
    Set set_a;
    set_a.add(1);
    set_a.add(2);
    set_a += 3;
    set_a += 4;
    std::cout << "Set a: ";
    set_a.print();

    Set set_b;
    set_b += 3;
    set_b += 4;
    set_b += 5;
    std::cout << "Set b: ";
    set_b.print();

    Set set_c = set_a.set_union(set_b);
    std::cout << "Union av A og B: ";
    set_c.print();

    set_b = set_a;
    std::cout << "B er nå A: ";
    set_b.print();
}
