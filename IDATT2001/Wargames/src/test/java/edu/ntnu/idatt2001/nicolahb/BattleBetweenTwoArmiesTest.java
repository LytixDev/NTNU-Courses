package edu.ntnu.idatt2001.nicolahb;

import edu.ntnu.idatt2001.nicolahb.units.InfantryUnit;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BattleBetweenTwoArmiesTest {

    @Nested
    class OnlyWinnerHasUnits {
        Army armyOne;
        Army armyTwo;

        /*
         * Creates army instances and fills them with some test units
         */
        void prepareTest() {
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
        void winnerHasUnits(){
            prepareTest();
            Battle battle = new Battle(armyOne, armyTwo);
            Army winner = battle.simulate();
            assertTrue(winner.hasUnits());
        }

        /*
         * Checks if the losing army has no units left.
         * Losing means you are out of units, so we expect the losing army to have no units.
         */
        @Test
        void loserDoesNotHaveUnits(){
            prepareTest();
            Battle battle = new Battle(armyOne, armyTwo);
            Army winner = battle.simulate();
            if (winner.equals(armyOne))
                assertFalse(armyTwo.hasUnits());
            else
                assertFalse(armyOne.hasUnits());
        }

    }

}
