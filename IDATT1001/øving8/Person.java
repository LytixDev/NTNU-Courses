public class Person {

    private final String firstName;
    private final String secondName;
    private final int birthYear;

    public Person(String firstName, String secondName, int birthYear) {
        this.firstName = firstName;
        this.secondName = secondName;
        this.birthYear = birthYear;
    }

    @Override
    public String toString() {
        return "Navn: " + this.firstName + " " + this.secondName + ",\n" + 
            "Fødselsår: " + this.birthYear + ",";
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getSecondName() {
        return this.secondName;
    }

    public int getBirthYear() {
        return this.birthYear;
    }
}
