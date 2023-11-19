#include <stdlib.h>
#include <string.h>
#include <stdbool.h>
#include <assert.h>

char to_lower(char c)
{
    if (c >= 'A' && c <= 'Z')
        return c + 32;
    return c;
}

bool check_if_palindrome(char *str, size_t len)
{
    char *l = str;
    char *r = str + len;

    do {
        while (*l == ' ')
            l++;
        while (*r == ' ')
            r--;

        /* edge case */
        if (r - l <= 0)
            break;

        if (to_lower(*l) != to_lower(*r))
            return false;
    } while (--r - ++l > 0);

    return true;
}

void check(char *str, bool expected)
{
    size_t len = strlen(str) - 1;
    bool actual = check_if_palindrome(str, len);
    assert(actual == expected);
}

int main(void)
{
    check("level", true);
    check("8448", true);
    check("step on no pets", true);
    check("My gym", true);
    check("Was it a car or a cat I saw", true);

    check("palindrome", false);
    check("first level", false);

    return 0;
}
