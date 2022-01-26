import java.util.Scanner;

public class Klient {

    public static void leggTilArrangement(Register r) { 
        Scanner in = new Scanner(System.in);

        System.out.println("Navn: ");
        String navn = in.nextLine();
        System.out.println("Sted: ");
        String sted = in.nextLine();
        System.out.println("Arrangør: ");
        String arrangør = in.nextLine();
        System.out.println("Type: ");
        String type = in.nextLine();
        System.out.println("Dato (long): ");
        long dato = in.nextLong();

        r.nyttArrangement(navn, sted, arrangør, type, dato);
        System.out.println("La til nytt arrangement");
    }

    public static void finnOgSorter(Register r, int type) {
        Scanner in = new Scanner(System.in);
        if (type == 1) {
            System.out.println("Skriv inn et sted: ");
            String sted = in.nextLine();
            Register ny = r.finnAlleArrangement(sted);
            System.out.println(ny);

        } else if (type == 2) {
            System.out.println("Skriv inn startdato: ");
            long start = in.nextLong();
            System.out.println("Skriv inn sluttdato: ");
            long slutt = in.nextLong();
            Register ny = r.finnAlleArrangement(start, slutt);
            System.out.println(ny);

        } else if (type == 3) {
            System.out.println("Skriv inn type: ");
            String typeArrangement = in.nextLine();
            Register ny = r.finnAlleArrangementType(typeArrangement);
            System.out.println(ny);
        }
    }

    public static void main(String[] args) {
        Register r = new Register();
        r.nyttArrangement("Bedpress med abc", "Realfagbygget", "abc", "Bedpress", 190220221800l);
        r.nyttArrangement("Bedpress med xyz", "Elektrobygget", "xyz", "Bedpress", 231120211930l);
        r.nyttArrangement("Kurs med xyz", "Elektrobygget", "xyz", "Kurs", 301120211930l);

        boolean running = true;
        Scanner in = new Scanner(System.in);
        while (running) {
            System.out.println("[0] -> legg til nytt arrangement\n" +
                    "[1] -> finn og sorter alle arrengementer gitt et sted\n" +
                    "[2] -> finn og sorter alle arrangementer gitt en dato\n" +
                    "[3] -> finn og sorter allle arrangementer gitt en type\n" +
                    "[4] -> exit\n");

            int valg = in.nextInt();
            switch (valg) {
                case 0:
                    leggTilArrangement(r);
                    break;
                case 1:
                    finnOgSorter(r, 1);
                    break;
                case 2:
                    finnOgSorter(r, 2);
                    break;
                case 3:
                    finnOgSorter(r, 3);
                    break;
                case 4:
                    running = false;
                    break;
                default:
                    System.out.println("SKRIV RIKTIG TALL NOOB!!!");
                    break;
            }

        }
    }
}
