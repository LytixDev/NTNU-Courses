public class Client {

    public static void main(String[] args) {
        TextHandler a = new TextHandler("Dette er en setning, med ord! en enn En.");
        System.out.println();
        System.out.println("Setning: " + a + "\n");
        System.out.println("Antall ord : " + a.amountWords() + "\n");
        System.out.println("Gjennomsnittlig ordlengde : " + a.avgWordLen() + "\n");
        System.out.println("Til store bokstaver : " + a.toBIG() + "\n");
        System.out.println("Bytter 'en' med 'ei' : " + a.searchAndReplace("en", "ei") + "\n");
        System.out.println("Gjennomsnittlig ord per periode : " + a.avgPeriodLen() + "\n");
    }

}
