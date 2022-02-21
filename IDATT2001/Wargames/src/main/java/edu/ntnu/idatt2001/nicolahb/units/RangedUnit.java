package edu.ntnu.idatt2001.nicolahb.units;

/**
 * Specialized subclass RangedUnit. Inherits from Unit.
 * @author Nicolai H. Brand
 */
public class RangedUnit extends Unit {

    private final static int defaultAttack = 15;
    private final static int defaultArmor = 8;
    private final static int fullResistBonus = 6;
    private final static int minResistBonus = 2;
    private final static int attackBonus = 3;
    private int timesResisted = 0;

    /**
     * Constructor for RangedUnit.
     * The ranged unit has more high resist bonus and a slight attack bonus.
     * The resist bonus depends on the amount of times resisted.
     * @param name, a short descriptive name of the type of unit.
     * @param health, a value that represents the health of the unit. Must be larger than zero.
     * @param attack, a value that represents the weapon of the unit.
     * @param armor a defensive value that represents the protection under attack.
     */
    public RangedUnit(String name, int health, int attack, int armor) {
        super(name, health, attack, armor);
    }


    /**
     * Constructor for RangedUnit that takes in default values for attack and armor.
     * The ranged unit has more high resist bonus and a slight attack bonus.
     * The resist bonus depends on the amount of times resisted.
     * @param name, a short descriptive name of the type of unit.
     * @param health, a value that represents the health of the unit. Must be larger than zero.
     */
    public RangedUnit(String name, int health) {
        super(name, health, defaultAttack, defaultArmor);
    }


    /**
     * @return int, attack bonus.
     */
    @Override
    public int getAttackBonus() {
        return attackBonus;
    }

    /**
     * The resist bonus depends on how mine times the unit has already resisted.
     * The first time it will return the fullResistBonus.
     * The second time it will return fullResistBonus - 2.
     * Afterwards it will return minResistBonus.
     * @return int, resist bonus
     */
    @Override
    public int getResistBonus() {
        timesResisted++;
        if (timesResisted == 1)
            return fullResistBonus;
        if (timesResisted == 2)
            return fullResistBonus - 2;

        return minResistBonus;
    }
}
