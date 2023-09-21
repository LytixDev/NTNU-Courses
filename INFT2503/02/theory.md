## oppgave 2
Seg fault. strcpy antar pekeren den får inn peker til minne som kan holde strengen som skal kopieres 

## oppgave 3
```cpp
char text[5];
char *pointer = text;
char search_for = 'e';
cin >> text;
while (*pointer != search_for) {
  *pointer = search_for;
  pointer++;
}
```

1. `cin >> text` antar jeg er skummelt fordi det vi leser kan være større enn det text kan holde. I.E buffer overflow.
2. while løkken har ingen "bounds-check". Denne kan gi seg fault fordi vi prøver å lese minne vi ikke nødvendigvis eier. Om det ikke gir seg fault er det uansett UB fordi det kan overskride minne, eller lese minne, som ikke er en del a text variabelen.
