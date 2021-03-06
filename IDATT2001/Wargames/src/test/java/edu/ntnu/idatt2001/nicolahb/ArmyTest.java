package edu.ntnu.idatt2001.nicolahb;

import edu.ntnu.idatt2001.nicolahb.units.*;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class ArmyTest {

    @Nested
    public class ArmyConstructorIsValid {
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
        public void unitsAreAddedAndRemoved() {
            ArrayList<Unit> units = new ArrayList<>();
            units.add(new CommanderUnit("test", 10));
            army = new Army("test", units);
            assertTrue(army.hasUnits());

            Army army2 = new Army("Name");
            army2.addAllUnits(units);
            assertTrue(army2.hasUnits());
            assertEquals(units, army2.getUnits());

            /* Remove all units from army2 */
            army2.removeAll();
            assertFalse(army2.hasUnits());
        }
    }

    @Nested
    public class GetRandomUnitFromArmy {
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
            assertTrue(army.getRandom() != null);
        }
    }

    @Nested
    public class getXUnits {

        @Test
        public void getAllCavalryUnits() {
            /*
            * A CommanderUnit inherits from CavalryUnit.
            * My implementations make sure CommanderUnit's are filtered out when calling getCavalryUnits().
            * All other getXUnits() method are implemented pretty much exactly the same, and therefore I see
            * no need to test them additionally.
            */
            CavalryUnit cavalryUnit = new CavalryUnit("Placeholder", 10);
            CommanderUnit commanderUnit = new CommanderUnit("Placeholder", 10);

            ArrayList<Unit> unitList = new ArrayList<>();
            unitList.add(cavalryUnit);
            unitList.add(commanderUnit);

            Army army = new Army("Army", unitList);

            /* We expect to only get the cavalryUnit back */
            assertEquals(1, army.getCavalryUnits().size());

            /* we expect the cavalryUnit that is returned to be equal to the one created here */
            assertEquals(cavalryUnit, army.getCavalryUnits().get(0));
        }

        @Test
        public void getAllRangedUnits() {
            Army army = new Army("Army");

            Unit rangedUnit1 = new RangedUnit("First", 10);
            Unit rangedUnit2 = new RangedUnit("Second", 10);
            Unit someThingElse = new InfantryUnit("Thirs", 10);

            army.addUnit(rangedUnit1);
            army.addUnit(rangedUnit2);
            army.addUnit(someThingElse);

            assertEquals(2, army.getRangedUnits().size());
            assertTrue(army.getRangedUnits().contains(rangedUnit1));
        }
    }

    @Nested
    public class Composition {

        @Test
        public void deepCopyCreatesDeepCopy() {
            Army army = new Army("Name");
            army.addUnit(new CavalryUnit("Placeholder", 10));
            Army armyDeepCopy = army.deepCopy();
            army.setName("New name");

            assertNotEquals(army, armyDeepCopy);
            assertNotEquals(army.getName(), armyDeepCopy.getName());
        }
    }

}
