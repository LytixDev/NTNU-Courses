package com.company;

import java.time.DateTimeException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;
import static javax.swing.JOptionPane.*;

/**
 * Class Main.
 * This class is the client program. Its purpose is to show information to the user, get input from the user, and
 * call methods from the other class corresponding to what the user has inputted.
 * @author <censored>
 */
public class Main {
    private static final Scanner sc = new Scanner(System.in);
    private static final LongJumpRegister longJumpRegister = new LongJumpRegister();

    /* I have defined invalid input to be -1. This will be used in the method parseDoubleWrapper.
       The method returns double, and due to its primitive type cannot return null, hence the need
       for a way to check if the exception was caught. */
    private static final int INVALID_INPUT = -1;
    private final static int ADD_RESULT = 0;
    private final static int LIST_ALL_RESULTS = 1;
    private final static int SHOW_RESULT_BY_ATHLETE = 2;
    private final static int SHOW_BEST_RESULT = 3;
    private final static int CALCULATE_AVERAGE_RESULT = 4;
    private final static int EXIT = 5;

    /**
     * The main method. Called when program is executed.
     * @param args String[], arguments passed when running, unused in this program.
     */
    public static void main(String[] args) {
        String VERSION = "v1.0-SNAPSHOT";

        String[] menuItems
                = {
                "1. Register a long jump result",
                "2. List all results",
                "3. Show all results by a given athlete",
                "4. Show the best result",
                "5. Calculate the average result",
                "6. Exit"
        };

        /*
            addTestData();
            I have added som data for testing purposes. Will not be included in the client program as I assume one would
            want to start a new register without any pre-existing data.
         */
        int menuSelection = showOptionDialog(null, "Long Jump Competition \nSelect", "Exam des 2021", YES_NO_OPTION, INFORMATION_MESSAGE, null, menuItems, menuItems[0]);
        while (menuSelection != EXIT) {
            switch (menuSelection) {
                case ADD_RESULT:
                    if (addResult())
                        showMessageDialog(null, "Successfully added new long jump");
                    else
                        showMessageDialog(null, "Could not add new long jump. Please make sure" +
                                "you have your input is correct.");
                    break;

                case LIST_ALL_RESULTS:
                    String info = "Start number - name - result (meter) - faul jump - time";
                    String str = longJumpRegister.listAll().toString();
                    showMessageDialog(null, info + "\n" + str);
                    break;

                case SHOW_RESULT_BY_ATHLETE:
                    ArrayList<LongJump> resultsByAthlete = showResultByAthlete();
                    String message = (resultsByAthlete.size() == 0) ? "Athlete has no results" : resultsByAthlete.toString();
                    showMessageDialog(null, message);
                    break;

                case SHOW_BEST_RESULT:
                    double bestResult = longJumpRegister.findBestResult();
                    showMessageDialog(null, (bestResult == 0) ? "No results found" : "Best result is: " + bestResult + " meters.");
                    break;

                case CALCULATE_AVERAGE_RESULT:
                    double averageResult = longJumpRegister.findAverageResult();
                    showMessageDialog(null, "Average result for all valid long jumps are " + averageResult + " meters.");
                    break;

                case EXIT:
                    System.out.println("\nThank you for using the Long Jump Application "
                            + VERSION + ". Bye!\n");
                    break;

                default:
                    System.ou.println(
                            "\nERROR: Please provide a number between 1 and " + menuItems.length + "..\n");
            }
            menuSelection = showOptionDialog(null, "Long Jump Competition \nSelect", "Exam des 2021", YES_NO_OPTION, INFORMATION_MESSAGE, null, menuItems, menuItems[0]);
        }
    }

    /**
     * Tries to add a new LongJump to LongJumpRegister given user input.
     * @return boolean, true if successfully added new LongJump, false if else.
     */
    private static boolean addResult() {
        String response;
        double result;
        boolean isFault;

        String startNr = showInputDialog("Please type in start number:");
        String athleteName = showInputDialog("Please type in the name of the athlete (firstname surname):");
        response = showInputDialog("Please type in result of long jump:");
        result = parseDoubleWrapper(response);
        response = showInputDialog("Was the jump faulty (not valid)? [Y/n]");

        // here I set it to be false as default
        if (response == null)
            isFault = false;
        else
            isFault = !response.toLowerCase(Locale.ROOT).equals("n");

        response = showInputDialog("Please type in time of jump in format 'hh:mm':");
        LocalTime time = null;
        try {
            time = LocalTime.parse(response);
        } catch (DateTimeException e) {
            showMessageDialog(null, "Time inputted (" + response + ") not valid in" +
                    "format 'hh:mm'");
        }

        LongJump toAdd = new LongJump(startNr, athleteName, result, isFault, time);
        return longJumpRegister.addNewLongJump(toAdd);
    }

    private static ArrayList<LongJump> showResultByAthlete() {
        String athleteName = showInputDialog("Please type in name of athlete (firstname surname):");
        return longJumpRegister.findAllForAthlete(athleteName);
    }

    /**
     * Tries to parse input String from user as a double.
     * @return Double. If exception was caught then it will return INVALID_INPUT.
     * @throws NumberFormatException if input can't be parsed into a double.
     */
    private static double parseDoubleWrapper(String input) throws NumberFormatException {
        if (input == null)
            return INVALID_INPUT;
        try {
            return Double.parseDouble(input);
        } catch (NumberFormatException e) {
            return (double) INVALID_INPUT;
        }
    }

    private static void addTestData() {
        LongJump toAdd = new LongJump("210", "Malaika Mihambo", 6.98, false, LocalTime.parse("10:15"));
        longJumpRegister.addNewLongJump(toAdd);
        toAdd = new LongJump("211", "Tara Davis", 6.85, false, LocalTime.parse("10:17"));
        longJumpRegister.addNewLongJump(toAdd);

    }
}
