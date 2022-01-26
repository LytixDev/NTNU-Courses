import java.util.*;

public class Employer {
    private Person person;
    private int employerNumber;
    private int appointmentYear;
    private double monthlyWage;
    private double taxPercent;  // stored in decimal -> 25% = 0.25

    public Employer(Person person, int employerNumber, int appointmentYear, double monthlyWage, double taxPercent) {
        this.person = person;
        this.employerNumber = employerNumber;
        this.appointmentYear = appointmentYear;
        this.monthlyWage = monthlyWage;
        this.taxPercent = taxPercent;
    }
    
    @Override
    public String toString() {
        return "Person:" + this.person + "\n" +
            "arbTakerNr:" + this.employerNumber + ",\n" +
            "ansettelsesÅr:" + this.appointmentYear + ",\n" +
            "månedsLønn:" + this.monthlyWage + ",\n" +
            "skatteprosent:" + this.taxPercent + ",\n" + 
            "årlig skatt:" + this.yearlyTax() + ",";
    }

    public double monthlyTax() {
        return this.monthlyWage * this.taxPercent;
    }

    public double grossYearlyWage() {
        return this.monthlyWage * 12;
    }

    public double yearlyTax() {
        return this.monthlyTax() * 10.5;
    }

    public String namePerson() {
        return this.person.getSecondName() + " " + this.person.getFirstName();
    }

    public int age() {
        GregorianCalendar calendar = new GregorianCalendar(); 
        int currentYear = calendar.get(Calendar.YEAR);
        return currentYear - this.person.getBirthYear();
    }

    public int yearsInCompany() {
        GregorianCalendar calendar = new GregorianCalendar(); 
        int currentYear = calendar.get(Calendar.YEAR);
        return currentYear - this.appointmentYear;
    }

    public boolean hasBeenEmployedLongerThan(int y) {
        return this.yearsInCompany() > y;
    }

    // get methods
    public String getPerson() {
        return this.person.toString();
    }

    public int getEmployerNumber() {
        return this.employerNumber;
    }

    public int getAppointmentYear() {
        return this.appointmentYear;
    }

    public double getMonthlyWage() {
        return this.monthlyWage;
    }

    public double getTaxPercent() {
        return this.taxPercent;
    }
    
    // set methods
    public void changeMonthlyWage(double newWage) {
        this.monthlyWage = newWage;
    }

    public void changeTaxPercent(double newTaxPercent) {
        this.taxPercent = newTaxPercent;
    }


}
