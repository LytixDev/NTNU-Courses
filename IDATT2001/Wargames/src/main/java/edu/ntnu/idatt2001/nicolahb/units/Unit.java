package edu.ntnu.idatt2001.nicolahb.units;


import java.util.Objects;

/**
 * Unit abstract class.
 * The unit class serves as an abstract for all subsequent units.
 * @author Nicolai H. Brand
 * @version 28.03.2022
 */
abstract public class Unit {
    private final String name;
    private int health;
    private int attack;
    private int armor;

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
     * @param opponent, enemy of any unit subclass.
     */
    public void attack(Unit opponent) {
        int attackDamage = this.getAttack() + this.getAttackBonus();
        int resistance = opponent.getArmor() + opponent.getResistBonus();
        if (attackDamage > resistance)
            opponent.setHealth(opponent.getHealth() - attackDamage + resistance);
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
        return "Unit{" +
                "name='" + name + '\'' +
                ", health=" + health +
                ", attack=" + attack +
                ", armor=" + armor +
                '}';
    }

    /**
     * Different units will be specialized in different things. Some units will have a high attack bonus,
     * others will not. Therefore, it is an abstract class.
     * @return int, representation of the attack bonus
     */
    public abstract int getAttackBonus();

    /**
     * Different units will be specialized in different things. Some units will have a high resist bonus,
     * others will not. Therefore, it is an abstract class.
     * @return int, representation of the resist bonus
     */
    public abstract int getResistBonus();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Unit unit = (Unit) o;
        return health == unit.health && attack == unit.attack && armor == unit.armor && Objects.equals(name, unit.name);
    }
}
