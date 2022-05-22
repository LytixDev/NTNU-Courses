package edu.ntnu.idatt2001.nicolahb;

import edu.ntnu.idatt2001.nicolahb.exceptions.CorruptedArmyFileException;
import edu.ntnu.idatt2001.nicolahb.filehandling.ArmyFileHandler;
import edu.ntnu.idatt2001.nicolahb.units.InfantryUnit;
import edu.ntnu.idatt2001.nicolahb.units.RangedUnit;
import edu.ntnu.idatt2001.nicolahb.units.Unit;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class FileHandlingTest {

    @Nested
    public class WrittenArmyEqualsReadArmy {

        @Test
        public void writeArmyAndReadIsSameArmy() {
            /*
             * This test passes.
             * For this reason I have not made tests to make sure every component of reading and writing an army
             * works as intended, since the system as a whole works as intended. This being said, I have seen it
             * necessary to create tests for the cases where the csv file is corrupted or has unexpected data.
             */

            Army army = new Army("According to all known laws of aviation ...");

            Unit unit1 = new InfantryUnit("Unit 1", 68);
            Unit unit2 = new RangedUnit("Unit 2", 70);

            army.addUnit(unit1);
            army.addUnit(unit2);

            String filePath;
            if (System.getProperty("os.name").equals("Linux"))
                filePath = "/tmp/test_army.csv";
            else
                filePath = "test_army.csv";

            try {
                ArmyFileHandler.writeArmyData(army, filePath);
                Army parsedArmy = ArmyFileHandler.parseArmy(filePath);

                /*
                 * The parsedArmy will contain the same units as the army, but they will not be considered equal
                 * as defined in the equals() method.
                 * This is because I have decided to be able to have equal units in the same army, and so the
                 * tradeoff is that it's a bit more work to check for equality. This is never an issue in the main code,
                 * only a slight inconvenience when for testing equality.
                 */

                assertNotNull(parsedArmy);
                assertEquals(army.getName(), parsedArmy.getName());
                assertEquals(army.getUnits().size(), parsedArmy.getUnits().size());
                assertEquals(army.getInfantryUnits().size(), parsedArmy.getInfantryUnits().size());
                assertEquals(army.getRangedUnits().size(), parsedArmy.getRangedUnits().size());

                /* Remove newly created file */
                File file = new File(filePath);
                file.delete();

            } catch (Exception e) {
                fail();
            }
        }
    }


    @Nested
    public class BadDataIsProperlyDealtWith {
        String filePath;

        private void prepareTest() {
            if (System.getProperty("os.name").equals("Linux"))
                filePath = "/tmp/test_army.csv";
            else
                filePath = "test_army.csv";
        }

        private void endTest() {
            /* Clean up file made from test */
            File file = new File(filePath);
            file.delete();
        }

        @Test
        public void unknownUnitIsIgnored() {
            prepareTest();

            /*
             * Simulate writing an army to a file.
             * First line contains army name.
             * Subsequent lines contain unit information.
             * We expect trying to write and then read an unknown unit to be ignored.
             * We expect a known unit to be read properly
             */
            try (FileWriter fw = new FileWriter(filePath)){
                fw.write("Army name\n");
                fw.write("InfantryUnit,Authentic,10\n");
                fw.write("MadeUpUnit,Counterfeit,10");
            } catch (IOException e) {
                fail();
            }

            try {
                Army parsedArmy = ArmyFileHandler.parseArmy(filePath);
                assertNotEquals(2, parsedArmy.getUnits().size());
                assertEquals(1, parsedArmy.getUnits().size());
            } catch (Exception e) {
                fail();
            }
            endTest();
        }

        @Test
        public void legalUnitButWrongCapitalizationIsAdded() {
            /*
             * My implementation of UnitFactory is done in a way that makes sure
             * that wrong capitalization yields intended result.
             * This indirectly tests the UnitFactory class.
             */
            prepareTest();

            try (FileWriter fw = new FileWriter(filePath)){
                fw.write("Army name\n");
                /* small 'i' */
                fw.write("infantryUnit,Authentic,10");
            } catch (IOException e) {
                fail();
            }

            try {
                Army parsedArmy = ArmyFileHandler.parseArmy(filePath);
                assertEquals(1, parsedArmy.getUnits().size());
                /* 'infantryUnit' is interpreted as 'InfantryUnit' */
                assertTrue(parsedArmy.getUnits().get(0) instanceof InfantryUnit);
            } catch (Exception e) {
                fail();
            }
            endTest();
        }

        @Test
        public void spacesBetweenValuesAreIgnored() {
            /* Spaces between commas should be ignored or "swallowed" by the parser */
            prepareTest();

            try (FileWriter fw = new FileWriter(filePath)){
                fw.write("Army name\n");
                fw.write("infantryUnit ,  Name, 10 ");
            } catch (IOException e) {
                fail();
            }

            try {
                Army parsedArmy = ArmyFileHandler.parseArmy(filePath);
                assertNotEquals(0, parsedArmy.getUnits().size());
                assertTrue(parsedArmy.getInfantryUnits().get(0).equalFields(new InfantryUnit("Name", 10)));
            } catch (Exception e) {
                fail("Could not parsed army with extra whitespace that should have been ignored.");
            }
            endTest();
        }

        @Test
        public void corruptedDataThrowsException() {
            prepareTest();

            /*
             * A single unit is represented using three fields in the csv file.
             * If a line after splitting on the delimiter ',' does not contain three elements, the data is corrupted,
             * and the program should terminate.
             */
            try (FileWriter fw = new FileWriter(filePath)){
                fw.write("Army name\n");
                /* Line only has one element after splitting on ',' */
                fw.write("Illegal argument !!");
            } catch (IOException e) {
                fail();
            }

            try {
                Army parsedArmy = ArmyFileHandler.parseArmy(filePath);
                fail();
            } catch (CorruptedArmyFileException e) {
                /* We expect this exceptions in particular to be thrown */
                assertTrue(e instanceof CorruptedArmyFileException);
            } catch (IOException | IllegalArgumentException e) {
                fail();
            }

            endTest();
        }

        @Test
        public void unitHealthThatIsNotAPositiveIntegerIsIgnored() {
            prepareTest();

            /*
             * Similarly to unknownUnitIsIgnored(), a unit with a health that can't be parsed as an Integer should
             * not be added as a Unit to the army. It should instead be ignored and normal program executions should
             * continue.
             */
            try (FileWriter fw = new FileWriter(filePath)){
                fw.write("Army name\n");
                /* Non-positive integer, should be ignored */
                fw.write("InfantryUnit,Name,-1\n");
                /* Not an integer, should be ignored */
                fw.write("InfantryUnit,Name Nameson,three\n");
                /* Not an integer, should be ignored */
                fw.write("InfantryUnit,Floaty,10.2");
            } catch (IOException e) {
                fail();
            }

            try {
                Army army = ArmyFileHandler.parseArmy(filePath);
                assertFalse(army.hasUnits());
            } catch (Exception e) {
                fail();
            }

            endTest();
        }

        @Test
        public void parsingFileThatDoesntExistThrowsError() {
            try {
                Army parsedArmy = ArmyFileHandler.parseArmy("/bin/i_do_not_exist.csv");
                fail("Parsed army that does not exist, when it should have throwed an error");
            } catch (Exception e) {
                assertTrue(e instanceof FileNotFoundException);
            }
        }
    }

    @Nested
    public class ParsingFileWithFourthValue {

        String filePath;

        private void prepareTest() {
            if (System.getProperty("os.name").equals("Linux"))
                filePath = "/tmp/test_army.csv";
            else
                filePath = "test_army.csv";
        }

        private void endTest() {
            /* Clean up file made from test */
            File file = new File(filePath);
            file.delete();
        }

        @Test
        public void armyWithFourthValueInLineCreatesExpectedAmountOfUnits() {
            /* Here we will use the feature that the fourth value in a line codes to the amount of units of that type to create */
            prepareTest();
            try (FileWriter fw = new FileWriter(filePath)) {
                /* Expect 30 commanderunits with health 10 to be created */
                fw.write("Name\n");
                fw.write("CommanderUnit,Name,10,30\n");
                /* Expect one infantry unit to be created */
                fw.write("InfantryUnit,Name,10,NaN");

            } catch (IOException e) {
                fail();
            }

            try {
                Army army = ArmyFileHandler.parseArmy(filePath);

                assertEquals(31, army.getUnits().size());
                assertEquals(30, army.getCommanderUnits().size());
                assertEquals(1, army.getInfantryUnits().size());
            } catch (Exception e) {
                fail();
            }
            endTest();
        }
    }
}
