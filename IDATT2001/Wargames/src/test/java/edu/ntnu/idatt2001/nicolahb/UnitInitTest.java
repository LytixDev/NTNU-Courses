package edu.ntnu.idatt2001.nicolahb;

import edu.ntnu.idatt2001.nicolahb.units.CavalryUnit;
import edu.ntnu.idatt2001.nicolahb.units.CommanderUnit;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class UnitInitTest {

    @Nested
    public class CreateLegalUnit {

        @Test
        public void unitNameCannotBeEmpty() {
            try {
                CavalryUnit cavalryUnit = new CavalryUnit("", 10);
                fail();
            } catch (IllegalArgumentException e) {
                assertEquals(e.getMessage(), "Name can't be empty");
            }
        }

        @Test
        public void attackDamageCannotBeNegative() {
            /* Health and armor fields are implemented exactly the same, and therefore I have decided only
             * to test one of these. If this test succeeds we can infer the other two work as well. */
            try {
                CommanderUnit commanderUnit = new CommanderUnit("Test", 10, -1, 10);
                fail();
            } catch (IllegalArgumentException e) {
                assertEquals(e.getMessage(), "Attack value has to be zero or larger");
            }
        }
    }
}
