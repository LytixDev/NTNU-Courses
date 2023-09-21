#pragma once

#include <vector>

class Set {
public:
    std::vector<int> collection;

    Set();
    void add(int num);
    void operator+=(int num);
    Set &operator=(Set &other);
    Set set_union(Set &other); // union identifier used by something else ??? 
    void print(); // I suspect this is not very c++ idiomatic
};
