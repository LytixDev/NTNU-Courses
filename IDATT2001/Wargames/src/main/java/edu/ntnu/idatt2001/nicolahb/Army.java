package edu.ntnu.idatt2001.nicolahb;

import edu.ntnu.idatt2001.nicolahb.units.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Class Army
 * The army has a list of units.
 * Two armies fight each other.
 *
 * @author Nicolai Brand
 * @version 16.05.2022
 */
public class Army {
    private String name;
    private ArrayList<Unit> units;

    /**
     * Default constructor
     * Creates an empty list of Units.
     *
     * @param name the name
     * @throws IllegalArgumentException ,if name is empty.
     */
    public Army(String name) throws IllegalArgumentException {
        if (name.isBlank()) throw new IllegalArgumentException("Name can't be empty");
        this.name = name;
        this.units = new ArrayList<>();
    }

    /**
     * Constructor that takes in a list of Units.
     *
     * @param name  the name
     * @param units the units
     * @throws IllegalArgumentException the illegal argument exception
     */
    public Army(String name, ArrayList<Unit> units) throws IllegalArgumentException {
        if (name.isBlank()) throw new IllegalArgumentException("Name can't be empty");
        this.name = name;
        this.units = units;
    }

    /**
     * @return a deep copy of itself.
     */
    public Army deepCopy() {
        Army copy = new Army(this.name);
        for (Unit unit : getUnits())
            /*
             * There is an exception here which is not caught as unit.getClass().getSimpleName() will always give
             * valid input.
             */
            copy.addUnit(UnitFactory.buildUnit(unit.getClass().getSimpleName(), unit.getName(), unit.getHealth()));

        return copy;
    }

    /**
     * Adds a Unit to the units list if the list does not already contain the unit.
     *
     * @param unit the unit
     */
    public void addUnit(Unit unit) {
        if (!units.contains(unit))
            units.add(unit);
    }

    /**
     * Adds a list of Units to the unit list.
     *
     * @param units the units
     */
    public void addAllUnits(List<Unit> units) {
        for (Unit toAdd : units)
            addUnit(toAdd);
    }

    /**
     * Remove a specified unit.
     *
     * @param unit the unit
     */
    public void remove(Unit unit) {
        units.remove(unit);
    }

    /**
     * Removes all units.
     *
     */
    public void removeAll() {
        units.clear();
    }

    /**
     * Checks if the list of units is not empty.
     *
     * @return boolean, true if list not empty.
     */
    public boolean hasUnits() {
        return !units.isEmpty();
    }


    /**
     * Gets all the infantry units stored in class field units.
     *
     * @return the infantry units as an ArrayList
     */
    public List<Unit> getInfantryUnits() {
        return units.stream()
                .filter(unit -> unit instanceof InfantryUnit)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Gets all the cavalry units stored in class field units.
     * Since the CommanderUnit inherits from CavalryUnit, we need to filter these out.
     * @return the cavalry units as an ArrayList
     */
    public List<Unit> getCavalryUnits() {
        return units.stream()
                .filter(unit -> unit instanceof CavalryUnit && !(unit instanceof CommanderUnit))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Gets all the ranged units stored in class field units.
     *
     * @return the ranged units as an ArrayList
     */
    public List<Unit> getRangedUnits() {
        return units.stream()
                .filter(unit -> unit instanceof RangedUnit)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Gets all the commander units stored in class field units.
     *
     * @return the commander units as an ArrayList
     */
    public List<Unit> getCommanderUnits() {
        return units.stream()
                .filter(unit -> unit instanceof CommanderUnit)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Returns a random Unit from the list.
     * Returns null if there are is no Unit in units.
     *
     * @return unit, Unit.
     */
    public Unit getRandom() {
        if (hasUnits())
            return units.get(new Random().nextInt(units.size()));
        return null;
    }

    /**
     * Gets name.
     *
     * @return name, String.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets units.
     *
     * @return units, ArrayList<Unit>.
     */
    public ArrayList<Unit> getUnits() {
        return units;
    }


    /**
     * Creates a String representation of the instance of the class that is comma separated.
     * @return String, String representation of the class in a csv format.
     */
    public String csvRepresentation() {
        /* Strings in Java are supposed to be immutable, so StringBuilder is used */
        StringBuilder out = new StringBuilder();
        out.append(name).append("\n");
        units.forEach(unit -> out.append(unit.csvRepresentation()));
        return out.toString();
    }

    /**
     * Renames the army
     *
     * @param newName String, new name of the army.
     */
    public void setName(String newName) {
        this.name = newName;
    }


    /**
     * Generates a string representation of the class.
     * @return String, String representation of class.
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
