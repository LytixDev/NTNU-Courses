package edu.ntnu.idatt2001.nicolahb.units;

import edu.ntnu.idatt2001.nicolahb.TerrainType;

import java.util.Objects;


/**
 * Unit abstract class.
 * The unit class serves as an abstract for all subsequent units.
 * @author Nicolai H. Brand
 * @version 01.05.2022
 */
abstract public class Unit {
    private final String name;
    private int health;
    private final int attack;
    private final int armor;

    /**
     * Creates an instance of the Unit class.
     * @param name, a short descriptive name of the type of unit.
     * @param health, a value that represents the health of the unit. Must be larger than zero.
     * @param attack, a value that represents the weapon of the unit.
     * @param armor a defensive value that represents the protection under attack.
     * @throws IllegalArgumentException, if name is empty (including whitespace).
     * @throws IllegalArgumentException, if health is not larger than zero.
     * @throws IllegalArgumentException, if either attack or armor is not zero or larger.
     */
    public Unit(String name, int health, int attack, int armor) throws IllegalArgumentException {
        if (name.isBlank())
            throw new IllegalArgumentException("Name can't be empty");
        if (health <= 0)
            throw new IllegalArgumentException("Initial health has to be larger than zero");
        if (attack < 0)
            throw new IllegalArgumentException("Attack value has to be zero or larger");
        if (armor < 0)
            throw new IllegalArgumentException("Armor value has to be zero or larger");
        this.name = name;
        this.health = health;
        this.attack = attack;
        this.armor = armor;
    }

    /**
     * Calculates the damage done on another unit and subsequently modifies its health.
     * Does apply the attack if the resistance is higher than the attack damage.
     * @param opponent    Unit, unit to be attacked.
     * @param terrainType TerrainType, the type of terrain the attack is taking place at.
     */
    public void attack(Unit opponent, TerrainType terrainType) {
        int attackDamage = this.getAttack() + this.getAttackBonus(terrainType);
        int resistance = opponent.getArmor() + opponent.getResistBonus(terrainType);
        if (attackDamage > resistance)
            opponent.setHealth(opponent.getHealth() - attackDamage + resistance);
    }

    /**
     * Calculates the damage that would be dealt on another unit.
     * Only used for logging.
     *
     * @param opponent    Unit, unit to be attacked.
     * @param terrainType TerrainType, the type of terrain the attack is taking place at.
     */
    public String calculateAttackDamage(Unit opponent, TerrainType terrainType) {
        int attackDamage = this.getAttack() + this.getAttackBonus(terrainType);
        int resistance = opponent.getArmor() + opponent.getResistBonus(terrainType);

        if (attackDamage > resistance)
            return String.valueOf(attackDamage - resistance);
        return "0";
    }

    /**
     * @return String, returns the name of the unit
     */
    public String getName() {
        return name;
    }

    /**
     * @return int, health of the unit
     */
    public int getHealth() {
        return health;
    }

    /**
     * @return int, value that represents the weapon of the unit
     */
    public int getAttack() {
        return attack;
    }

    /**
     * @return int, value that represents a defensive protection under health
     */
    public int getArmor() {
        return armor;
    }

    /**
     * @param health int, set the health of the unit
     */
    public void setHealth(int health) {
        this.health = health;
    }

    /**
     * Formats the data of the specific instance of Unit class on the format:
     * "unit type,unit name,unit health"
     * This is used when writing to file
     * @return String, String representation of the object
     */
    public String csvRepresentation() {
        return this.getClass().getSimpleName() +
                "," +
                name +
                "," +
                health +
                "\n";
    }

    /**
     * @return String, string representation of the class
     */
    @Override
    public String toString() {
        return  name + " - " + getClass().getSimpleName() +
                " hp=" + health +
                " attack=" + attack +
                " armor=" + armor;
    }

    /**
     * Different units will be specialized in different things. Some units will have a high attack bonus,
     * others will not. Therefore, it is an abstract class.
     * @param terrainType TerrainType, the type of terrain the attack is taking place at.
     * @return int, representation of the attack bonus
     */
    public abstract int getAttackBonus(TerrainType terrainType);

    /**
     * Different units will be specialized in different things. Some units will have a high resist bonus,
     * others will not. Therefore, it is an abstract class.
     * @param terrainType TerrainType, the type of terrain the attack is taking place at.
     * @return int, representation of the resist bonus
     */
    public abstract int getResistBonus(TerrainType terrainType);

    /**
     * Checks if between two units are the same as well as the same class.
     * This is used instead of the traditional built-in equals() method to check for technical equality.
     * This is done, as to have the ability to have multiple of the same unit in one army.
     * This method is mainly used for testing.
     * @param other Unit, unit to be compared with.
     * @return boolean
     */
    public boolean equalFields(Unit other) {
        return this.getClass() == other.getClass() && Objects.equals(this.name, other.name) && this.health == other.health;
    }

}
