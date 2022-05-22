package edu.ntnu.idatt2001.nicolahb;

import edu.ntnu.idatt2001.nicolahb.units.CavalryUnit;
import edu.ntnu.idatt2001.nicolahb.units.RangedUnit;
import edu.ntnu.idatt2001.nicolahb.units.Unit;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TerrainBonusTest {

    @Nested
    public class TerrainBonusIsCorrectlyApplied {

        /*
         * I have made unit tests to check if the terrain bonus is applied only to some cases.
         * All the cases not checked for are implemented exactly the same, and so I argue it's superfluous to check
         * every single unit's bonuses, as there is incredibly unlikely that the implementation is correct for only
         * some units since it's been implemented exactly the same.
         */

        @Test
        public void cavalryUnitGetsZeroResistBonusOnForest() {
            Unit cavalryUnit = new CavalryUnit("Name", 10);
            assertEquals(0, cavalryUnit.getResistBonus(TerrainType.FOREST));
        }

        @Test
        public void cavalryUnitGetsAttackBonusOnPlains() {
            Unit cavalryUnitOnPlains = new CavalryUnit("First", 1);
            Unit cavalryUnitOnHills = new CavalryUnit("Second", 1);

            /* All else is the same, so the extra bonus on plains should result in a higher attack bonus */
            assertTrue(cavalryUnitOnPlains.getAttackBonus(TerrainType.PLAINS) >
                    cavalryUnitOnHills.getAttackBonus(TerrainType.HILL));
        }

        @Test
        public void rangedUnitGetsHillBonus() {
            Unit rangedUnitOnHills = new RangedUnit("First", 1);
            Unit rangedUnitOnNeutral = new RangedUnit("Second", 1);

            assertTrue(rangedUnitOnHills.getAttackBonus(TerrainType.HILL) >
                    rangedUnitOnNeutral.getAttackBonus(TerrainType.NEUTRAL));
        }

        @Test
        public void rangedUnitGetsForestPenalty() {
            Unit rangedUnitOnForest = new RangedUnit("First", 1);
            Unit rangedUnitOnNeutral = new RangedUnit("Second", 1);

            assertNotEquals(rangedUnitOnForest.getAttackBonus(TerrainType.FOREST), rangedUnitOnNeutral.getAttackBonus(TerrainType.NEUTRAL));
        }
    }
}
