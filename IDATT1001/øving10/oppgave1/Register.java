import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Register {
  
    private ArrayList<Arrangement> arr;

    public Register() {
        this.arr = new ArrayList<Arrangement>();
    }

    public void nyttArrangement(String navn, String sted, String arrangør, String type, long dato) {
        Arrangement nytt = new Arrangement(navn, sted, arrangør, type, dato);
        if (!arr.contains(nytt)) {
            arr.add(nytt);
        } else throw new IllegalArgumentException("Arrangementet lagt til fra før");
    }

    public void nyttArrangement(Arrangement arrangement) {
        if (!arr.contains(arrangement)) {
            arr.add(arrangement);
        } else throw new IllegalArgumentException("Arrangementet lagt til fra før");
    }

    public Register finnAlleArrangement(String sted){
        Register out = new Register();
        for (Arrangement a : arr) {
            if (a.getSted().equals(sted)) {
                out.nyttArrangement(a);
            }
        }
        return out;
    }

    public Register finnAlleArrangementType(String type){
        Register out = new Register();
        for (Arrangement a : arr) {
            if (a.getType().equals(type)) {
                out.nyttArrangement(a);
            }
        }
        return out;
    }
    
    public Register finnAlleArrangement(long dato){
        Register out = new Register();
        for (Arrangement a : arr) {
            if (a.getDato() == dato) {
                out.nyttArrangement(a);
            }
        }
        return out;
    }

    public Register finnAlleArrangement(long startDato, long sluttDato){
        Register out = new Register();
        for (Arrangement a : arr) {
            if (a.getDato() >= startDato && a.getDato() <= sluttDato) {
                out.nyttArrangement(a);
            }
        }
        out.sorterArrangementer("dato");
        return out;
    }

    public void sorterArrangementer(String etterHva) {
        if (etterHva.equals("dato")) {
            Comparator<Arrangement> sorter = (Arrangement a1, Arrangement a2) -> (int)a1.getDato() - (int)a2.getDato();
            Collections.sort(arr, sorter);
            //Comparator.comparing(Arrangement::getDato());

        } else if (etterHva.equals("sted")) {
            Comparator<Arrangement> sorter = (Arrangement a1, Arrangement a2) -> a1.getSted().compareTo(a2.getSted());
            Collections.sort(arr, sorter);

        } else if (etterHva.equals("type")) {
            Comparator<Arrangement> sorter = (Arrangement a1, Arrangement a2) -> a1.getType().compareTo(a2.getType());
            Collections.sort(arr, sorter);

        } else {
            System.out.println("Kan ikke sortere etter typen: " + etterHva);
        }
    }

    @Override
    public String toString() {
        String out = "";
        for (Arrangement a : arr)
            out += a;
        return out;
    }

}

