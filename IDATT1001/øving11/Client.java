import java.util.Scanner;

/**
 * Class Client.
 * Creates an empty PropertyRegister object.
 * Fills the PropertyRegister object with test data.
 * Starts a menu-based client program that runs until the user quits.
 */
public class Client {
    public static PropertyRegister propertyRegister;

    /**
     * Main
     * @param args String[]
     */
    public static void main(String[] args) {
        start();
    }

    /**
     * Assigns propertyRegister to a new PropertyRegister object.
     * Fills propertyRegister with test data.
     * Starts the menu-based client program.
     */
    public static void start(){
        propertyRegister = new PropertyRegister();
        addTestData();
        boolean running = true;
        int choice;
        Scanner sc = new Scanner(System.in);
        while (running) {
            choice = showMenu();

            switch (choice) {
                case 1:
                    int municipalityNr;
                    String municipalityName;
                    int lotNr;
                    int sectionNr;
                    String name;
                    float area;
                    String ownerName;
                    System.out.println("MunicipalityNr: ");
                    if (sc.hasNextInt()) {
                        municipalityNr = sc.nextInt();
                    }
                    else {
                        System.out.println("Unrecognized input");
                        break;
                    }

                    System.out.println("MunicipalityName: ");
                    if (sc.hasNext()) {
                        municipalityName = sc.next();
                    }
                    else {
                        System.out.println("Unrecognized input");
                        break;
                    }

                    System.out.println("LotNr: ");
                    if (sc.hasNextInt()) {
                        lotNr = sc.nextInt();
                    }
                    else {
                        System.out.println("Unrecognized input");
                        break;
                    }

                    System.out.println("SectionNr: ");
                    if (sc.hasNextInt()) {
                        sectionNr = sc.nextInt();
                    }
                    else {
                        System.out.println("Unrecognized input");
                        break;
                    }

                    System.out.println("Name: ");
                    if (sc.hasNext()) {
                        name = sc.next();
                    }
                    else {
                        System.out.println("Unrecognized input");
                        break;
                    }

                    System.out.println("Area: ");
                    if (sc.hasNextFloat()) {
                        area = sc.nextFloat();
                    }
                    else {
                        System.out.println("Unrecognized input");
                        break;
                    }

                    System.out.println("OwnerName: ");
                    if (sc.hasNext()) {
                        ownerName = sc.next();
                    }
                    else {
                        System.out.println("Unrecognized input");
                        break;
                    }

                    propertyRegister.addProperty(municipalityNr, municipalityName, lotNr, sectionNr, name,
                            area, ownerName);
                    break;
                case 2:
                    System.out.println(propertyRegister);
                    break;
                case 3:
                    System.out.println("MunicipalityNr: ");
                    if (sc.hasNextInt()) {
                        municipalityNr = sc.nextInt();
                    }
                    else {
                        System.out.println("Unrecognized input");
                        break;
                    }

                    System.out.println("LotNr: ");
                    if (sc.hasNextInt()) {
                        lotNr = sc.nextInt();
                    }
                    else {
                        System.out.println("Unrecognized input");
                        break;
                    }

                    System.out.println("SectionNr: ");
                    if (sc.hasNextInt()) {
                        sectionNr = sc.nextInt();
                    }
                    else {
                        System.out.println("Unrecognized input");
                        break;
                    }

                    System.out.println(propertyRegister.findProperty(municipalityNr, lotNr, sectionNr));
                    break;
                case 4:
                    System.out.println("LotNr: ");
                    if (sc.hasNextInt()) {
                        lotNr = sc.nextInt();
                    }
                    else {
                        System.out.println("Unrecognized input");
                        break;
                    }
                    System.out.println(propertyRegister.findPropertyByLotNr(lotNr));
                    break;
                case 5:
                    System.out.println(propertyRegister.avgArea());
                    break;
                case 6:
                    System.out.println("MunicipalityNr: ");
                    if (sc.hasNextInt()) {
                        municipalityNr = sc.nextInt();
                    }
                    else {
                        System.out.println("Unrecognized input");
                        break;
                    }

                    System.out.println("LotNr: ");
                    if (sc.hasNextInt()) {
                        lotNr = sc.nextInt();
                    }
                    else {
                        System.out.println("Unrecognized input");
                        break;
                    }

                    System.out.println("SectionNr: ");
                    if (sc.hasNextInt()) {
                        sectionNr = sc.nextInt();
                    }
                    else {
                        System.out.println("Unrecognized input");
                        break;
                    }
                    propertyRegister.removeProperty(municipalityNr, lotNr, sectionNr);
                    break;
                case 7:
                    System.out.println(propertyRegister.getAmountProperties());
                    break;
                case 9:
                    System.out.println("Quitting");
                    running = false;
                    break;
                default:
                    System.out.print("Input not recognized");
                    break;
            }
        }
        sc.close();
    }

    /**
     * Fills the propertyRegister with test data
     */
    private static void addTestData() {
        propertyRegister.addProperty(1445, "Gloppen", 77, 631, "", 1018.6f, "Jens Olsen");
        propertyRegister.addProperty(1445, "Gloppen", 77, 131, "Syningom", 661.3f, "Nicolay Madsen");
        propertyRegister.addProperty(1445, "Gloppen", 75, 19, "Fugletun", 650.6f, "Evilyn Jensen");
        propertyRegister.addProperty(1445, "Gloppen", 75, 188, "", 1457.2f, "Karl Ove Bråten");
        propertyRegister.addProperty(1445, "Gloppen", 69, 47, "Høiberg", 1339.4f, "Elsa Indregård");
    }

    /**
     * Gets input about what to next from the user.
     * @return int, what to do next in the menu-based client program
     */
    private static int showMenu(){
        int menuChoice = 0;
        System.out.println("\n***** Property Register Application v0.1 *****\n");
        System.out.println("1. Add property");
        System.out.println("2. List all properties");
        System.out.println("3. Search property");
        System.out.println("4. Search all properties by lotNr");
        System.out.println("5. Calculate average area");
        System.out.println("6. Remove property");
        System.out.println("7. Get total properties");
        System.out.println("8. ");
        System.out.println("9. Quit");
        System.out.println("Enter a number between 1 and 9: ");

        Scanner sc = new Scanner(System.in);
        if (sc.hasNextInt())
            menuChoice = sc.nextInt();
        else
            System.out.println("You must enter a number, not text");

        return menuChoice;
    }
}
