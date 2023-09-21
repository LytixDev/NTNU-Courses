#include <string>
#include <iostream>
#include <assert.h>

int main(void) {

    std::string word1, word2, word3;

    std::cout << "Skriv inn tre ord separert med \\n" << std::endl;
    std::cin >> word1;
    std::cin >> word2;
    std::cin >> word3;

    std::string sentence = word1 + " " + word2 + " " + word3 + ".";
    // std::cout << "Skjøtet sammen setning: " << sentence << std::endl;

    std::cout << "Lengden til ord1: " << word1.length() << std::endl;
    std::cout << "Lengden til ord2: " << word2.length() << std::endl;
    std::cout << "Lengden til ord3: " << word3.length() << std::endl;
    std::cout << "Lengden til skjøtet sammen setning: " << sentence.length() << std::endl;

    std::string sentence2 = sentence;

    assert(sentence2.length() > 12);

    sentence2.replace(9, 3, "xxx");

    // vet allerede at sentence må ha minst 13 tegn
    std::string sentence_start = sentence.substr(0, 5);
    std::cout << sentence_start << " - " << sentence << std::endl;

    size_t first_pos = sentence.find("hallo");
    if (first_pos != std::string::npos)
        std::cout << "hallo finnes i " << sentence << std::endl;

    size_t occurences = 0;
    size_t pos = sentence.find("er");
    while (pos != std::string::npos) {
        occurences++;
        pos = sentence.find("er", ++pos);
    }

    std::cout << "Fant " << occurences << std::endl;
}
