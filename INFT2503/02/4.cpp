int main(void)
{
    int a = 5;
    /* referanse krever en initialiserer */
    int &b = a;
    /* en peker kan helt fint ikke ha en initialiserer */
    int *c;
    c = &b;

    /* 
     * prøver å "dereference" a som om det var en peker, men det er en int så det går ikke 
     * syntaxen får å hente verdien til en reference er ikke lik som peker. kan sløyfe "*"
     */
    a = b + *c;
    /* kan ikke sette refereransen til b til en int */
    b = 2;
}
