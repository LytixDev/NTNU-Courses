public class Student {
    
    private String navn;
    private int antOppg;


    public Student(String navn, int antOppg) {
        this.navn = navn;
        this.antOppg = antOppg;
    }

    public static void main(String[] args) {
        Student stud1 = new Student("Gorm", 8);
        System.out.println(stud1);
        System.out.println("Øker antall godkjente oppgaver med 3");
        stud1.økAntOppg(3);
        System.out.println(stud1);
    }

    public String getNavn() {
        return this.navn;
    }

    public int getAntOppg() {
        return this.antOppg;
    }

    public void økAntOppg(int økning) {
        this.antOppg += økning;
    }
    
    @Override
    public String toString() {
        return "Navn: " + this.navn + ", antOppg: " + this.antOppg + "\n";
    }

}
