package edu.ntnu.idatt2001.nicolahb.units;

import edu.ntnu.idatt2001.nicolahb.TerrainType;

/**
 * Specialized subclass InfantryUnit. Inherits from Unit.
 * @author Nicolai H. Brand
 * @version 13.04.2022
 */
public class InfantryUnit extends Unit {

    private final static int defaultAttack = 15;
    private final static int defaultArmor = 10;
    private final static int attackBonus = 2;
    private final static int resistBonus = 1;
    private final static int forestBonus = 3;

    /**
     * Constructor for InfantryUnit.
     * @param name, a short descriptive name of the type of unit.
     * @param health, a value that represents the health of the unit. Must be larger than zero.
     * @param attack, a value that represents the weapon of the unit.
     * @param armor, a defensive value that represents the protection under attack.
     */
    public InfantryUnit(String name, int health, int attack, int armor) {
        super(name, health, attack, armor);
    }

    /**
     * Constructor for InfantryUnit that uses the default values for attack and armor.
     * @param name, a short descriptive name of the type of unit.
     * @param health, a value that represents the health of the unit. Must be larger than zero.
     */
    public InfantryUnit(String name, int health) {
        super(name, health, defaultAttack, defaultArmor);
    }

    /**
     * Returns the attack bonus.
     * Extra bonus if terraintype is forest.
     * @param terrainType TerrainType, the type of terrain the attack is taking place at.
     * @return int, attack bonus.
     */
    @Override
    public int getAttackBonus(TerrainType terrainType) {
        return (terrainType == TerrainType.FOREST) ? attackBonus + forestBonus : attackBonus;
    }

    /**
     * Returns the resist bonus.
     * Extra bonus if terraintype is forest.
     * @param terrainType TerrainType, the type of terrain the attack is taking place at.
     * @return int, resist bonus.
     */
    @Override
    public int getResistBonus(TerrainType terrainType) {
        return (terrainType == TerrainType.FOREST) ? resistBonus + forestBonus : resistBonus;
    }
}