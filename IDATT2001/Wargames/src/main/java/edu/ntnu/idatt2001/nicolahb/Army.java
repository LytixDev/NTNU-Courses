package edu.ntnu.idatt2001.nicolahb;

import edu.ntnu.idatt2001.nicolahb.units.Unit;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

/**
 * Class Army
 * The army has a list of units.
 * Two armies fight each other.
 * @author Nicolai Brand
 */
public class Army {
    private final String name;
    private ArrayList<Unit> units;

    /**
     * Default constructor
     * Creates an empty list of Units.
     * @param name, String: can't be empty.
     * @throws IllegalArgumentException, if name is empty.
     */
    public Army(String name) throws IllegalArgumentException {
        if (name.isBlank()) throw new IllegalArgumentException("Name can't be empty");
        this.name = name;
        this.units = new ArrayList<>();
    }

    /**
     * Constructor that takes in a list of Units.
     * @param name, String: can't be empty.
     * @param units, ArrayList<Unit>: object that stores the units.
     * @throws IllegalArgumentException
     */
    public Army(String name, ArrayList<Unit> units) throws IllegalArgumentException {
        if (name.isBlank()) throw new IllegalArgumentException("Name can't be empty");
        this.name = name;
        this.units = units;
    }

    /**
     * Adds a Unit to the units list if the list does not already contain the unit.
     * @param unit, Unit: an object of type Unit.
     */
    public void addUnit(Unit unit) {
        if (!units.contains(unit))
            units.add(unit);
    }

    /**
     * Adds a list of Units to the unit list.
     * @param units, ArrayList<Unit>: A list containing objects of type Unit.
     */
    public void addAllUnits(ArrayList<Unit> units) {
        for (Unit toAdd : units)
            addUnit(toAdd);
    }
    /**
     * Remove a specified unit.
     * @param unit, Unit.
     */
    public void remove(Unit unit) {
        units.remove(unit);
    }

    /**
     * Checks if the list of units is not empty.
     * @return boolean, true if list not empty.
     */
    public boolean hasUnits() {
        return !units.isEmpty();
    }

    /**
     * Returns a random Unit from the list.
     * Returns null if there are is no Unit in units.
     * @return unit, Unit.
     */
    public Unit getRandom() {
        if (hasUnits())
            return units.get(new Random().nextInt(units.size()));
        return null;
    }

    /**
     * @return name, String.
     */
    public String getName() {
        return name;
    }

    /**
     * @return units, ArrayList<Unit>.
     */
    public ArrayList<Unit> getUnits() {
        return units;
    }

    /**
     * Generates a string representation of the class.
     * @return String, String representation of clas.
     */
    @Override
    public String toString() {
        return "Army{" +
                "name='" + name + '\'' +
                ", units=" + units +
                '}';
    }

    /**
     * Checks if two objects of Army are equal.
     * @param o Object, should be of type Army.
     * @return bool, true if objects are equal else false.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Army army = (Army) o;
        return Objects.equals(name, army.name) && Objects.equals(units, army.units);
    }

    /**
     * Generates a hash code for the object.
     * @return int, hash code.
     */
    @Override
    public int hashCode() {
        return Objects.hash(name, units);
    }
}
