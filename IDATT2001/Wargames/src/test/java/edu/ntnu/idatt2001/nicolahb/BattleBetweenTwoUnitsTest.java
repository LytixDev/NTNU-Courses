package edu.ntnu.idatt2001.nicolahb;

import edu.ntnu.idatt2001.nicolahb.units.CommanderUnit;
import edu.ntnu.idatt2001.nicolahb.units.CavalryUnit;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BattleBetweenTwoUnitsTest {
    /*
     * All units will start with the same initial health.
     * Therefore, the first unit who attacks between equal units will win.
     */
    private final static int startHealth = 30;

    /*
     * When two instances of the same unit with the same starting health attack, the first attacker should win.
     */
    @Nested
    class FirstAttackerWinsBetweenEqualUnits {

        @Test
        public void firstAttackerBetweenEqualUnitsWin() {
            CommanderUnit first = new CommanderUnit("First", startHealth);
            CommanderUnit second = new CommanderUnit("Second", startHealth);

            while(first.getHealth() > 0 && second.getHealth() > 0) {
                first.attack(second);
                if (second.getHealth() < 0)
                    break;
                second.attack(first);
            }

            assertTrue(first.getHealth() > second.getHealth());
        }
    }

    /*
     * Some units do not have a static attack bonus.
     * The attack bonus depends on how many times the unit has previously attacked.
     * I have decided not to test if the resist bonus works. This is because it has been implemented the same way \
     * as the attack bonus hence it seems unlikely it would give a different result.
     */
    @Nested
    class AttackBonusIsDecremented {

        @Test
        public void firstAttackBonusIsNotEqualToSecondAttackBonus() {
            CavalryUnit first = new CavalryUnit("First", startHealth);
            CavalryUnit second = new CavalryUnit("Second", startHealth);

            /* Since the first unit has attacked, we expect the attack bonus to have decremented */
            first.attack(second);
            assertNotEquals(first.getAttackBonus(), second.getAttackBonus());
        }

    }
}
