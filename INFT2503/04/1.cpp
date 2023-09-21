#include <iostream>
#include <vector>
#include <algorithm>

int main(void) {
    std::vector<double> vec;
    vec.emplace_back(1.0);
    vec.emplace_back(1.1);
    vec.emplace_back(1.2);
    vec.emplace_back(1.3);
    vec.emplace_back(1.4);

    double front = vec.front();
    double back = vec.back();
    std::cout << "front : " << front << std::endl;
    std::cout << "back : " << back << std::endl;

    vec.emplace(vec.begin(), 0.9);
    front = vec.front();
    std::cout << "front : " << front << std::endl;

    auto found = std::find(vec.begin(), vec.end(), 1.1);
    if (found != vec.end())
        std::cout << "fant : " << *found << std::endl;
}
