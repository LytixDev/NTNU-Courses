Subtraksjon er en binær expression som evalueres venstre til høyre.
Dermed blir `5 - 3 - fraction1 - 7 - fraction2` representert som `((((5 - 3) - fraction1)) - 7) - fraction2)`.

Dette blir evaulert i denne rekkefølgen:
- `5 - 3` -> `resultat1: int`. Her brukes vanlig `-` for to ints.

- `resultat1 - fraction1` -> `resultat2: Fraction`. Her brukes overloaden vi skrev selv av vanlig `-`: `Fraction operator-(int integer, const Fraction &other)`

- `resultat2 - 7` -> `resultat3: Fraction`: Her brukes en overload av `operator-` for Fraction klassen som tar inn en int.

- `resultat3 - fraction2` -> `resultat4: Fraction`: Her brukes en annen overload av `operator-`for Fraction klassen som tar inn et annet Fraction objekt.
