package edu.ntnu.idatt2001.nicolahb.filehandling;

import edu.ntnu.idatt2001.nicolahb.Army;
import edu.ntnu.idatt2001.nicolahb.exceptions.CorruptedArmyFileException;
import edu.ntnu.idatt2001.nicolahb.units.*;

import java.io.*;

/**
 * Class ArmyFileHandler.
 * This class deals with writing an army to a value separated file.
 * It also reads a file that contains data about an army and instantiates an army based on said data.
 *
 * Instead of breaking normal program executions, I think it makes more sense to continue
 * in the event that a unit has an invalid health or type. In effect, a unit whose health or type is invalid is
 * skipped and not added to the army.
 * @author Nicolai H. Brand.
 * @version 21.05.2022
 */
public class ArmyFileHandler {

    private final static String DELIMITER = ",";
    /**
     * Write army data to a given file.
     * Uses the csvRepresentation() method in the army as the data to be written.
     * Values are separated by a delimiter set in the field DELIMITER.
     *
     * @param army     the army to be written to the file.
     * @param filePath path to the file. Can both be a full path and a relative path.
     * @throws IOException              the io exception if can't open or write to file
     * @throws IllegalArgumentException illegal argument exception if army is null
     */
    public static void writeArmyData(Army army, String filePath) throws IOException, IllegalArgumentException {
        if (army == null) throw new IllegalArgumentException("Cannot write null army to file.");

        try (FileWriter fw = new FileWriter(filePath)) {
            fw.write(army.dataRepresentation(DELIMITER));
        }
    }

    /**
     * Parse Army from given csv file.
     * The file strictly doesn't need to have the .csv file extension as long as the data is formatted correctly. For
     * this reason I do not check if the file extension is csv as I deem it superfluous. Any error in the format will
     * have to be dealt with in any case.
     *
     * @param filePath String, the path to the file. Can be both relative or absolute.
     * @return Army, the parsed Army.
     * @throws IllegalArgumentException   If filePath is blank or if can't parse Unit objects.
     * @throws IOException                the io exception
     * @throws CorruptedArmyFileException the corrupted army file exception
     */
    public static Army parseArmy(String filePath) throws IllegalArgumentException, IOException, CorruptedArmyFileException {
        if (filePath.isBlank()) throw new IllegalArgumentException("File path cannot be blank");

        String line;
        int lineCount = 0;
        Army army;

        try (BufferedReader bf = new BufferedReader(new FileReader(filePath))) {
            /* we know the first line in the file contains the unitName of the army */
            army = new Army(bf.readLine());

            while ((line = bf.readLine()) != null) {
                lineCount++;
                String[] split = line.split(DELIMITER);

                if (split.length != 3 && split.length != 4) {
                    throw new CorruptedArmyFileException(filePath + " is corrupted. All lines apart from the first" +
                            " should contain three or four elements, but line " + lineCount + " contains " + split.length +
                            " elements.");
                }

                String unitType = split[0].trim();
                String unitName = split[1].trim();
                int unitHealth;
                int amountOfUnitsToCreate = 1;

                /*
                 * Instead of breaking normal program executions, I think it makes more sense to continue
                 * in the event that a unit has an invalid health. In effect, a unit whose health is invalid is
                 * skipped and not added to the army.
                 */
                try {
                    unitHealth = Integer.parseInt(split[2].trim());
                } catch (Exception e) {
                    continue;
                }

                if (unitHealth < 1)
                    continue;

                /* Attempt to parse amount of units. This field being blank is valid, and in this case we create 1 unit. */
                if (split.length == 4) {
                    try {
                        amountOfUnitsToCreate = Integer.parseInt(split[3].trim());
                        if (amountOfUnitsToCreate < 1)
                            amountOfUnitsToCreate = 1;
                    } catch (Exception ignored) {}
                }

                /*
                 * Thrown exception is dealt with as described in javadoc.
                 * It would be advantageous to inform the user that an error occurred.
                 */
                try {
                    army.addAllUnits(UnitFactory.buildUnits(unitType, unitName, unitHealth, amountOfUnitsToCreate));
                } catch (IllegalArgumentException ignored) {}
            }
        }

        return army;
    }
}