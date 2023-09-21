#include <vector>
#include <algorithm>
#include <cstdio>
#include "set.hpp"

Set::Set() : collection() {}

void Set::add(int num) {
    if (std::find(collection.begin(), collection.end(), num) == collection.end())
        collection.push_back(num);
}

void Set::operator+=(int num) {
    // copy
    Set *set = this;
    set->add(num);
}

Set &Set::operator=(Set &other) {
    this->collection = other.collection;
    return *this;
}

Set Set::set_union(Set &other) {
    // boring solution.
    Set new_set = *this;

    for (int item : other.collection)
        new_set.add(item);

    return new_set;
}

void Set::print() {
    std::printf("{ ");
    for (int item : this->collection)
        std::printf("%d ", item);
    std::printf("}\n");
}
