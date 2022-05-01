package edu.ntnu.idatt2001.nicolahb;

import edu.ntnu.idatt2001.nicolahb.units.CommanderUnit;
import edu.ntnu.idatt2001.nicolahb.units.CavalryUnit;
import edu.ntnu.idatt2001.nicolahb.units.RangedUnit;
import edu.ntnu.idatt2001.nicolahb.units.Unit;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BattleBetweenTwoUnitsTest {
    /*
     * All units will start with the same initial health.
     * Therefore, the first unit who attacks between equal units will win.
     */
    private final static int startHealth = 50;

    /*
     * When two instances of the same unit with the same starting health attack, the first attacker should win.
     */
    @Nested
    public class FirstAttackerWinsBetweenEqualUnits {

        @Test
        public void firstAttackerBetweenEqualUnitsWin() {
            CommanderUnit first = new CommanderUnit("First", startHealth);
            CommanderUnit second = new CommanderUnit("Second", startHealth);

            while(first.getHealth() > 0 && second.getHealth() > 0) {
                first.attack(second, TerrainType.NEUTRAL);
                if (second.getHealth() < 0)
                    break;
                second.attack(first, TerrainType.NEUTRAL);
            }

            assertTrue(first.getHealth() > second.getHealth());
        }
    }

    /*
     * Some units do not have a static attack/resist bonus.
     * The attack and resist bonuses depends on how many times the unit has previously attacked / been attacked.
     */
    @Nested
    public class HealthAndBonusIsUpdated {

        @Test
        public void firstAttackBonusIsNotEqualToSecondAttackBonus() {
            CavalryUnit first = new CavalryUnit("First", startHealth);
            CavalryUnit second = new CavalryUnit("Second", startHealth);

            /* Since the first unit has attacked, we expect the attack bonus to have decremented */
            first.attack(second, TerrainType.NEUTRAL);
            assertNotEquals(first.getAttackBonus(TerrainType.NEUTRAL), second.getAttackBonus(TerrainType.NEUTRAL));
        }

        @Test
        public void firstResistBonusIsMoreThanSecondResistBonusAndThirdAndFourthIsEqual() {
            RangedUnit rangedUnit = new RangedUnit("First", startHealth);

            int initialResistBonus = rangedUnit.getResistBonus(TerrainType.NEUTRAL);
            int secondResistBonus = rangedUnit.getResistBonus(TerrainType.NEUTRAL);
            int thirdResistBonus = rangedUnit.getResistBonus(TerrainType.NEUTRAL);
            int fourthResistBonus = rangedUnit.getResistBonus(TerrainType.NEUTRAL);

            assertTrue(initialResistBonus > secondResistBonus);
            assertEquals(thirdResistBonus, fourthResistBonus);
        }

        @Test
        public void AttackingWithXDamageDecrementsOpponentsHealthByX() {
            Unit attacker = new Unit("test", 10, 10, 0) {

                @Override
                public int getAttackBonus(TerrainType ignored) {
                    return 0;
                }

                @Override
                public int getResistBonus(TerrainType ignored) {
                    return 0;
                }
            };

            Unit defender = new Unit("test", 10, 0, 0) {

                @Override
                public int getAttackBonus(TerrainType ignored) {
                    return 0;
                }

                @Override
                public int getResistBonus(TerrainType ignored) {
                    return 0;
                }
            };

            int initialHealth = defender.getHealth();
            attacker.attack(defender, TerrainType.NEUTRAL);
            int postAttackHealth = defender.getHealth();

            assertEquals(10, initialHealth - postAttackHealth);
        }
    }
}