#pragma once

template <typename Type1, typename Type2>
class Pair {
public:
    Type1 first;
    Type2 second;

    Pair(Type1 a, Type2 b) : first(a), second(b) {}

    bool operator>(Pair &other) {
        return this->first + this->second > other.first + other.second;
    }

    Pair operator+(Pair &other) {
        // return { .first = this->first + other.first, .second = this->second + other.second };
        Pair new_pair = *this;
        new_pair.first += other.first;
        new_pair.second += other.second;
        return new_pair;
    }
};
