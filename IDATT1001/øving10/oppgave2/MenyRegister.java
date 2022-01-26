import java.util.*;

public class MenyRegister {
    private ArrayList<Rett> arr;

    public MenyRegister() {
        arr = new ArrayList<Rett>();
    }

    public MenyRegister(ArrayList<Rett> arr) {
        this.arr = arr;
    }

    public void registrerNyRett(String navn, String type, double pris) {
        Rett ny = new Rett(navn, type, pris);
        arr.add(ny);
    }

    public Rett finnRett(String navn) {
        for (Rett r : arr) {
            if (r.getNavn().equals(navn))
                return r;
        }

        return null;
    }

    public ArrayList<Rett> finnRettGittType(String type) {
        ArrayList<Rett> retter = new ArrayList<Rett>();

        for (Rett r : arr) {
            if (r.getType().equals(type))
                retter.add(r);
        }

        return retter;
    }
    
    public static void main(String[] args) {
        MenyRegister a = new MenyRegister(); 
        a.registrerNyRett("Grandiosa", "Delikatesse", 59);
        a.registrerNyRett("Pasta", "Hovedrett", 100);
        a.registrerNyRett("Spaghetti", "Hovedrett", 80);
        Rett ønsket = a.finnRett("Grandiosa");
        System.out.println("Finn Grandiosa:");
        System.out.println(ønsket);

        System.out.println("Finn alle hovedretter");
        ArrayList<Rett> ny = a.finnRettGittType("Hovedrett");
        MenyRegister b = new MenyRegister(ny);
        System.out.println(b);
    }

    @Override
    public String toString() {
        String out = "";

        for (Rett r : arr)
            out += r;

        return out;
    }
}
