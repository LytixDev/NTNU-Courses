package edu.ntnu.idatt2001.nicolahb;

import edu.ntnu.idatt2001.nicolahb.units.CommanderUnit;
import edu.ntnu.idatt2001.nicolahb.units.Unit;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class ArmyTest {

    @Nested
    class ArmyConstructorIsValid {
        Army army;

        @Test
        public void nameCannotBeEmpty() {
            try {
                army = new Army("  ");
                fail();
            } catch (IllegalArgumentException e) {
                assertTrue(true);
            }
        }

        @Test
        public void unitsAreAdded() {
            ArrayList<Unit> units = new ArrayList<>();
            units.add(new CommanderUnit("test", 10));
            army = new Army("test", units);
            assertTrue(army.hasUnits());
        }
    }

    @Nested
    class GetRandomUnitFromArmy {
        Army army;

        @Test
        public void getRandomUnitWhenArmyHasNoUnitsIsNull() {
            army = new Army("test");
            assertNull(army.getRandom());
        }

        @Test
        public void getRandomUnitIsUnit() {
            ArrayList<Unit> units = new ArrayList<>();
            units.add(new CommanderUnit("test", 10));
            army = new Army("test", units);
            /* Could also check to see if army.getRandom() does not return null */
            assertTrue(army.getRandom() instanceof Unit);
        }
    }
}
