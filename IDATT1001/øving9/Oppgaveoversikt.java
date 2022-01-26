import java.util.*;

public class Oppgaveoversikt {
    private ArrayList<Student> studenter;
    private int antStud;

    public Oppgaveoversikt() {
        this.studenter = new ArrayList<Student>();
        this.antStud = studenter.size();
    }

    public int getAntStud() {
        return this.studenter.size();
    }

    public void registrerStudent(String navn, int antOppg) {
        boolean entydig = true;
        for (Student student : this.studenter) {
            if (student.getNavn().equals(navn)) {
                entydig = false;
                break;
            }
        }
        if (entydig) { 
            Student ny = new Student(navn, antOppg);
            studenter.add(ny);
        }
   }

    public int getAntOppgGittStudent(String navn) {
        Student student = finnStudent(navn);
        return student.getAntOppg();
    }

    public void økAntOppgGittStudent(String navn, int økning) {
        Student student = finnStudent(navn);
        student.økAntOppg(økning);
    }

    public Student finnStudent(String navn) {
        for (Student student : this.studenter) {
            if (student.getNavn().equals(navn)) {
                return student; 
            }
        }
        return null;
    }

    @Override
    public String toString() {
        String out = "";
        for (int i = 0; i < this.studenter.size(); i++) {
            out = out + studenter.get(i).toString();
        }
        return out;
    }

    public static void main(String[] args) {
        Oppgaveoversikt oversikt = new Oppgaveoversikt();
        oversikt.registrerStudent("Gorm", 3);
        oversikt.registrerStudent("Gunnar", 5);
        oversikt.registrerStudent("Ole", 9);
        oversikt.registrerStudent("Ole", 9);

        System.out.println(oversikt);
        System.out.println("Øker antOppg hos Gorm med 3");
        oversikt.økAntOppgGittStudent("Gorm", 3);
        System.out.println(oversikt);

    }
}
        
