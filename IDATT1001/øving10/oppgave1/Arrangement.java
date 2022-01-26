public class Arrangement {
    private static int nextNr = 1;
    private int nr;
    private String navn;
    private String sted;
    private String arrangør;
    private String type;
    private long dato;
   
    //public enum Type {
    //    KONSERT, BARNETEATER, FOREDRAG
    //}

    public Arrangement(String navn, String sted, String arrangør, String type, long dato) {
        setNr(); 
        this.navn = navn;
        this.sted = sted;
        this.arrangør = arrangør;
        this.type = type;
        this.dato = dato;
    }

    public void setNr() {
        this.nr = nextNr;
        this.nextNr++;
    }

    public int getNr() {
        return nr;
    }

    public String getSted() {
        return sted;
    }

    public String getType() {
        return type;
    }

    public long getDato() {
        return dato;
    }

    public boolean equals(Arrangement a) {
        return (nr == a.getNr());
    }

    @Override
    public String toString() {
        return "\nNr: " + nr + "\n" +
            "Navn: " + navn + "\n" +
            "Sted: " + sted + "\n" +
            "Arrangør: " + arrangør + "\n" +
            "Type: " + type + "\n" +
            "Dato: " + dato + "\n";
    }
}
