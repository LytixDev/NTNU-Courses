package edu.ntnu.idatt2001.nicolahb;

import edu.ntnu.idatt2001.nicolahb.units.*;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UnitFactoryTest {
    /*
     * I indirectly also test that each of the legal unit types are created by varying the type.
     * UnitFactory class is also indirectly tested in FilehandlingTest.
     */

    @Nested
    public class LegalUnitsAreBuilt {

        @Test
        public void legalUnitIsBuilt() {
            /* Tests whether the correct unit type is created */
            try {
                Unit builtUnit = UnitFactory.buildUnit("CavalryUnit", "name", 10);
                Unit manualUnit = new CavalryUnit("name", 10);
                assertTrue(builtUnit.equalFields(manualUnit));
            } catch (IllegalArgumentException ignored) {
                fail();
            }
        }

        @Test
        public void legalUnitButBadCapitalizationIsStillBuilt() {
            /* Test whether the raw unit type can handle wrong capitalization */
            try {
                Unit unit = UnitFactory.buildUnit("CAVALRYunit", "name", 10);
            } catch (IllegalArgumentException ignored) {
                fail("Failed to build unit with correct semantic unit type, but wrong syntax (wrong capitalization)");
            }
        }

        @Test
        public void legalUnitsAreBuilt() {
            /* Tests whether the correct unit type is correct AND that there has been created the expected amount */
            try {
                List<Unit> units = UnitFactory.buildUnits("RangedUnit", "name", 10, 100);
                assertEquals(100, units.size());
                /* Make sure every unit created is of the specified type */
                assertEquals(100, units.stream().filter(unit -> unit instanceof RangedUnit).count());
                assertEquals(0, units.stream().filter(unit -> unit instanceof InfantryUnit).count());
            } catch (IllegalArgumentException ignored) {
                fail();
            }
        }
    }

    @Nested
    public class UndefinedBehaviourIsDealtWith {

        @Test
        public void undefinedUnitTypeThrowsException() {
            String undefinedType = "BadUnit";
            String expectedErrorMsg = "Unit type BadUnit does not exist. Legal units are: CavalryUnit, CommanderUnit, InfantryUnit, RangedUnit.";

            try {
                Unit unit = UnitFactory.buildUnit(undefinedType, "name", 10);
                fail("Unit with undefined type was created, but should not have been.");
            } catch (IllegalArgumentException e) {
                assertEquals(expectedErrorMsg, e.getMessage());
            }
        }

        @Test
        public void buildingZeroUnitsRaisesError() {
            String expectedErrorMsg = "Can not create less than one units";

            try {
                List<Unit> units = UnitFactory.buildUnits("InfantryUnits", "name", 10, 0);
                fail();
            } catch (IllegalArgumentException e) {
                assertEquals(expectedErrorMsg, e.getMessage());
            }
        }
    }
}
