#include <algorithm>
#include <iostream>
#include <string>
#include <vector>

// fra leksjonen
std::ostream &operator<<(std::ostream &out, const std::vector<int> &table) {
    for (auto &e : table)
        out << e << " ";
    return out;
}

int main() {
    // fra leksjonen
    std::vector<int> v1 = {3, 3, 12, 14, 17, 25, 30};
    std::cout << "v1: " << v1 << std::endl;
    std::vector<int> v2 = {2, 3, 12, 14, 24};
    std::cout << "v2: " << v2 << std::endl;

    auto predicate = [](int i) { return i > 15; };
    // a)
    auto pos = find_if(v1.begin(), v1.end(), [](int num) { return num > 15; });

    if (pos != end(v1))
        std::cout << "Første tall større enn 15 er: " << *pos << std::endl;

    // b)
    auto almost_eq_func = [](int i, int j){ return abs(i - j) <= 2; };
    auto pos2 = equal(v1.begin(), v1.begin() + 5, v2.begin(), almost_eq_func);
    std::cout << "Første 5 omtrent lik:" << pos2 << std::endl;
    std::cout << "Første 4 omtrent lik: " << equal(v1.begin(), v1.begin() + 4, v2.begin(), almost_eq_func) << std::endl;

    // c)
    std::vector<int> copy;
    copy.resize(v1.size());
    replace_copy_if(v1.begin(), v1.end(), copy.begin(), [](int num){ return num % 2 == 0; }, 100);
    std::cout << copy << std::endl;
}
