package edu.ntnu.idatt2001.nicolahb.filehandling;

import edu.ntnu.idatt2001.nicolahb.Army;
import edu.ntnu.idatt2001.nicolahb.exceptions.CorruptedArmyFileException;
import edu.ntnu.idatt2001.nicolahb.units.*;

import java.io.*;

/**
 * Class CSVFileHandler.
 * This class deals with writing an army to a comma separated value file.
 * It also reads a csv file that contains data about an army and instantiates an army based on said data.
 *
 * Instead of breaking normal program executions, I think it makes more sense to continue
 * in the event that a unit has an invalid health or type. In effect, a unit whose health or type is invalid is
 * skipped and not added to the army.
 * @author Nicolai H. Brand.
 * @version 28.03.2022
 */
public class CSVFileHandler {

    /**
     * Write army data to a given file.
     * Uses the csvRepresentation() method in the army as the data to be written.
     *
     * @param army     the army to be written to the file.
     * @param filePath path to the file. Can both be a full path and a relative path.
     * @throws IOException              the io exception if can't open or write to file
     * @throws IllegalArgumentException illegal argument exception if army is null
     */
    public static void writeArmyData(Army army, String filePath) throws IOException, IllegalArgumentException {
        if (army == null) throw new IllegalArgumentException("Cannot null army to file.");

        try (FileWriter fw = new FileWriter(filePath)) {
            fw.write(army.csvRepresentation());
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
                String[] split = line.split(",");

                if (split.length != 3) {
                    throw new CorruptedArmyFileException(filePath + " is corrupted. All lines apart from the first" +
                            " should contain three elements, but line " + lineCount + " contains " + split.length +
                            " elements.");
                }

                String unitType = split[0];
                String unitName = split[1];
                int unitHealth;

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

                /*
                 * Thrown exception is dealt with as described in javadoc.
                 * It would be advantages to inform the user that an error occurred.
                 */
                try {
                    army.addUnit(UnitFactory.buildUnit(unitType, unitName, unitHealth));
                } catch (IllegalArgumentException ignored) {}
            }
        }

        return army;
    }
}