public class Rett {
    
    private String navn;
    private String type;
    private double pris;

    public Rett(String navn, String type, double pris) {
        this.navn = navn;
        this.type = type;
        this.pris = pris;
    }

    public String getNavn() { return navn; }
    public String getType() { return type; }
    public Double getPris() { return pris; }

    @Override
    public String toString() {
        return "Navn: " + navn + "\n" +
            "Type: " + type + "\n" +
            "Pris: " + pris + "\n";
    }
}
