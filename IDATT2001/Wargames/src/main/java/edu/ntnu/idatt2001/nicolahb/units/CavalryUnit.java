package edu.ntnu.idatt2001.nicolahb.units;

import edu.ntnu.idatt2001.nicolahb.TerrainType;

/**
 * Specialized subclass CavalryUnit. Inherits from Unit.
 * @author Nicolai H. Brand.
 * @version 13.04.2022
 */
public class CavalryUnit extends Unit {

    private static final int defaultAttack = 20;
    private static final int defaultArmor = 12;
    private final static int resistBonus = 2;
    private final static int firstAttackBonus = 4;
    private final static int defaultAttackBonus = 2;
    private final static int plainsBonus = 3;
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
     * @param terrainType TerrainType, the type of terrain the attack is taking place at.
     * @return int, attack bonus.
     */
    @Override
    public int getAttackBonus(TerrainType terrainType) {
        timesAttacked++;
        int bonus = timesAttacked == 1 ? firstAttackBonus : defaultAttackBonus;

        if (terrainType == TerrainType.PLAINS)
            bonus += plainsBonus;

        return bonus;
    }

    /**
     * If TerrainType is forest return 0.
     *
     * @param terrainType TerrainType, the type of terrain the attack is taking place at.
     * @return int, resist bonus.
     */
    @Override
    public int getResistBonus(TerrainType terrainType) {
        return (terrainType == TerrainType.FOREST) ? 0 : resistBonus;
    }
}
