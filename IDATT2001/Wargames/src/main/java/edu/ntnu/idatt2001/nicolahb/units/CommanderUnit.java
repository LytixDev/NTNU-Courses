package edu.ntnu.idatt2001.nicolahb.units;

/**
 * Specialized subclass CommanderUnit. Inherits from Unit and CavalryUnit.
 * @author Nicolai H. Brand
 * @version 13.04.2022
 */
public class CommanderUnit extends CavalryUnit {

    private static final int defaultAttack = 25;
    private static final int defaultArmor = 15;

    /**
     * The Commander Unit inherits the attack and resist bonus from the CavalryUnit.
     * @param name, a short descriptive name of the type of unit.
     * @param health, a value that represents the health of the unit. Must be larger than zero.
     * @param attack, a value that represents the weapon of the unit.
     * @param armor a defensive value that represents the protection under attack.
     */
    public CommanderUnit(String name, int health, int attack, int armor) {
        super(name, health, attack, armor);
    }

    /**
     * The Commander Unit inherits the attack and resist bonus from the CavalryUnit.
     * It has a large default attack value and armor value.
     * @param name, a short descriptive name of the type of unit.
     * @param health, a value that represents the health of the unit. Must be larger than zero.
     */
    public CommanderUnit(String name, int health) {
        super(name, health, defaultAttack, defaultArmor);
    }
}
