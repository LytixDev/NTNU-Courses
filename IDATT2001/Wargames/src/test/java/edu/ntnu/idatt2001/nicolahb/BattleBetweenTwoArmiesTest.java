package edu.ntnu.idatt2001.nicolahb;

import edu.ntnu.idatt2001.nicolahb.exceptions.SimulationAlreadyRanException;
import edu.ntnu.idatt2001.nicolahb.gui.models.Logger;
import edu.ntnu.idatt2001.nicolahb.units.InfantryUnit;
import edu.ntnu.idatt2001.nicolahb.units.Unit;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BattleBetweenTwoArmiesTest {

    @Nested
    public class OnlyWinnerHasUnits {
        Army armyOne;
        Army armyTwo;
        TerrainType terrainType = TerrainType.NEUTRAL;

        /*
         * Creates army instances and fills them with some test units
         */
        public void prepareTest() {
            armyOne = new Army("Army one");
            armyTwo = new Army("Army two");

            armyOne.addUnit(new InfantryUnit("Footman", 100));
            armyTwo.addUnit(new InfantryUnit("Footman", 100));
        }

        /*
         * Checks if the winning army has units left.
         * We expect the winning army to have units left.
         */
        @Test
        public void winnerHasUnits(){
            prepareTest();
            Battle battle = new Battle(armyOne, armyTwo, terrainType);
            try {
                Army winner = battle.simulate();
                assertTrue(winner.hasUnits());
            } catch (SimulationAlreadyRanException e) {
               fail();
            }
        }

        /*
         * Checks if the losing army has no units left.
         * Losing means you are out of units, so we expect the losing army to have no units.
         */
        @Test
        public void loserDoesNotHaveUnits() {
            prepareTest();
            Battle battle = new Battle(armyOne, armyTwo, terrainType);

            try {
                Army winner = battle.simulate();

                if (winner.equals(armyOne))
                    assertFalse(armyTwo.hasUnits());
                else
                    assertFalse(armyOne.hasUnits());

            } catch (SimulationAlreadyRanException e) {
                fail();
            }
        }
    }

    @Nested
    public class BattleSimulation {
        Army armyOne;
        Army armyTwo;

        /*
         * Creates army instances and fills them with some test units
         */
        public void prepareTest() {
            armyOne = new Army("Army one");
            armyTwo = new Army("Army two");

            armyOne.addUnit(new InfantryUnit("Footman", 100));
            armyTwo.addUnit(new InfantryUnit("Footman", 100));
        }

        @Test
        public void battleCanOnlyRunOnce() {
            prepareTest();
            Battle battle = new Battle(armyOne, armyTwo, TerrainType.NEUTRAL);

            try {
                battle.simulate();
            } catch (SimulationAlreadyRanException e) {
                fail();
            }

            try {
                battle.simulate();
                fail();
            } catch (SimulationAlreadyRanException e) {
                assertTrue(true);
            }
        }

        /* This method cannot be tested as it requires JAVAFX to run, but we can tell
           visually during program execution that the log works.

        @Test
        public void attackAddsInformationToLog() {
            prepareTest();
            Battle battle = new Battle(armyOne, armyTwo, TerrainType.NEUTRAL);
            Logger<String> log = new Logger<>();
            try {
                battle.simulateSingleStepGUI(log);
            } catch (Exception e) {
                fail();
            }

            assertFalse(log.getLog().isEmpty());
        }
         */
    }

    @Nested
    public class DeadUnitIsRemovedFromArmy {

        @Test
        public void unitWithZeroHealthIsRemovedFromArmy() {
            Army armyOne = new Army("Army one");
            Army armyTwo = new Army("Army two");

            Unit chadUnit = new InfantryUnit("Chad Footman", 10000);
            Unit weakUnit = new InfantryUnit("Footman", 1);

            /* weakUnit's health will fall below zero */
            chadUnit.attack(weakUnit, TerrainType.NEUTRAL);

            armyOne.addUnit(chadUnit);
            armyTwo.addUnit(weakUnit);

            Battle battle = new Battle(armyOne, armyTwo, TerrainType.NEUTRAL);

            try {
                battle.simulate();
                /*
                 * This is not a perfect way to test whether a unit with zero or below health is removed.
                 * However, due to how I've implemented the Battle.simulate() method this is the best I can do.
                 * An improvement could be to have a method in Battle that would check if an attacked Unit has it's health
                 * fallen to zero or below, for it then to be removed from it's respective army. Such a method could be
                 * called in this test.
                 */
                assertFalse(armyTwo.hasUnits());
            } catch (SimulationAlreadyRanException e) {
                fail();
            }
        }

    }

}