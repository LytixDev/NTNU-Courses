# wargames

## avhengigheter
- java runtime environment 17 - til å kjøre programmet. Eldre versjoner kan gi uventet resultat.
- maven - byggesystem brukt til å laste ned og holde styr på eksterne bibloteker
- git - til å laste ned kildekoden
- ssh - til å laste ned kildekoden


## installer og kjør

`$ git clone git@gitlab.stud.idi.ntnu.no:nicolahb/wargames.git `

`$ cd Wargames && mvn javafx:run`

## filformat til en armé

Arméer er lagret som en verdi-separert-textsfil. Komma blir brukt som default separator. Eksempel:

Navn på arméen

Type unit,navn,helse,antall

--------

Eksempel:

My Army

RangedUnit,Archer,10,5

CommanderUnit,Chief,50


Er det ikke spesifisert et antall blir det opprettet én enhet.
