package edu.ntnu.idatt2001.nicolahb.units;

/**
 * Specialized subclass CavalryUnit. Inherits from Unit.
 * @author Nicolai H. Brand.
 * @version 28.03.2022
 */
public class CavalryUnit extends Unit {

    private static int defaultAttack = 20;
    private static int defaultArmor = 12;
    private final static int resistBonus = 2;
    private final static int firstAttackBonus = 4;
    private final static int defaultAttackBonus = 2;
    private int timesAttacked = 0;

    /**
     * Constructor for CavalryUnit.
     * The ranged unit has more high attack bonus and a slight resist bonus.
     * The attack bonus depends on the amount of times it has attacked.
     * @param name, a short descriptive name of the type of unit.
     * @param health, a value that represents the health of the unit. Must be larger than zero.
     * @param attack, a value that represents the weapon of the unit.
     * @param armor a defensive value that represents the protection under attack.
     */
    public CavalryUnit(String name, int health, int attack, int armor) {
        super(name, health, attack, armor);
    }

    /**
     * Constructor for CavalryUnit that takes in default values for attack and armor.
     * The ranged unit has more high attack bonus and a slight resist bonus.
     * The attack bonus depends on the amount of times it has attacked.
     * @param name, a short descriptive name of the type of unit.
     * @param health, a value that represents the health of the unit. Must be larger than zero.
     */
    public CavalryUnit(String name, int health) {
        super(name, health, defaultAttack, defaultArmor);
    }

    /**
     * The attack bonus depends on the amount of times it has attacked.
     * The first time it attacks it will return the firstAttackBonus.
     * Preceding attacks will return defaultAttackBonus.
     * @return int, attack bonus.
     */
    @Override
    public int getAttackBonus() {
        timesAttacked++;
        return timesAttacked == 1 ? firstAttackBonus : defaultAttackBonus;
    }

    /**
     * @return int, resist bonus.
     */
    @Override
    public int getResistBonus() {
        return resistBonus;
    }
}
