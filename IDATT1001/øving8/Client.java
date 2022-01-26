import java.util.Scanner;

public class Client {
   
    private static Scanner in = new Scanner(System.in); 

    public static void main(String[] args) {
        Person person = new Person("Gunnar", "Hansen", 1969);
        Employer employer = new Employer(person, 1, 2011, 40000, 0.4);

        boolean running = true;

        while (running == true) {
            System.out.println(employer);

            double monthlyTax = employer.monthlyTax();
            System.out.println("Månedlig skattetrekk: " + monthlyTax);

            double grossWage = employer.grossYearlyWage();
            System.out.println("Årlig bruttolønn: " + grossWage);

            String name = employer.namePerson();
            System.out.println("Person navn: " + name);

            int age = employer.age();
            System.out.println("Alder: " + age);

            int yearsInCompany = employer.yearsInCompany();
            System.out.println("År i bedriften: " + yearsInCompany);

            boolean inCompanyLongerThan10 = employer.hasBeenEmployedLongerThan(10);
            System.out.println("Har vært i bedriften lenger enn 10 år: " + inCompanyLongerThan10);

            System.out.println("Vil du gjøre endringer? [y/N]");       
            String inp = in.nextLine().trim();
            if (inp.equals("y")) {
                System.out.println("Ny månedslønn: ");
                employer.changeMonthlyWage(in.nextDouble());
                System.out.println("Ny skatteprosent (skriv som desimal): ");
                employer.changeTaxPercent(in.nextDouble());
            }

            System.out.println("\n\n");
        }
    }
}
