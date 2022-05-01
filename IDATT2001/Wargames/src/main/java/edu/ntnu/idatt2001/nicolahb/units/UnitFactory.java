package edu.ntnu.idatt2001.nicolahb.units;

import java.util.ArrayList;
import java.util.List;

/**
 * The class UnitFactory.
 * Creates new units based on type given as a String.
 * @author Nicolai H. Brand
 * @version 13.04.2022
 */
public class UnitFactory {

    /*
     * List of legal units.
     * Used to give a more descriptive error message.
     */
    public static String legalUnits = "CavalryUnit, CommanderUnit, InfantryUnit, RangedUnit";

    /**
     * Returns a new unit based on type, name and health.
     * Converts the given type (String) to lowercase to make sure
     * badly capitalized letters gives desired result.
     * Throws exception if type given does not exist.
     *
     * @param rawUnitType String, the name of the type of unit to be build
     * @param unitName    String, the assigned name of the unit
     * @param unitHealth  int, the health of the unit
     * @return            Unit, newly built unit
     * @throws IllegalArgumentException if rawUnitType is not a legal unit
     */
    public static Unit buildUnit(String rawUnitType, String unitName, int unitHealth) throws IllegalArgumentException {
        String unitType = rawUnitType.trim().toLowerCase();

        return switch (unitType) {
            case "cavalryunit" -> new CavalryUnit(unitName, unitHealth);
            case "commanderunit" -> new CommanderUnit(unitName, unitHealth);
            case "infantryunit" -> new InfantryUnit(unitName, unitHealth);
            case "rangedunit" -> new RangedUnit(unitName, unitHealth);
            default -> throw new IllegalArgumentException("Unit type " + rawUnitType + " does not exist. Legal units are: "
                    + legalUnits + ".");
        };
    }

    /**
     * Returns a new list of units based on type name and health.
     * Calls buildUnit() method when building a new unit.
     *
     * @param rawUnitType String, the name of the type of unit to be build
     * @param unitName    String, the assigned name of the unit
     * @param unitHealth  int, the health of the unit
     * @param totalUnits  int, the total amount of new units to be built
     * @return            List, a list of the newly built units given parameters
     * @throws IllegalArgumentException if totalUnits is less or equal to zero
     *                                  Also passes on exception from buildUnit()
     */
    public static List<Unit> buildUnits(String rawUnitType, String unitName, int unitHealth, int totalUnits)
                             throws IllegalArgumentException {
        if (totalUnits <= 0)
            throw new IllegalArgumentException("Can not create less than one units");

        /* units.add(buildUnits(...) i for i in range(totalUnits) */
        ArrayList<Unit> units = new ArrayList<>();
        for (int i = 0; i < totalUnits; i++)
            units.add(buildUnit(rawUnitType, unitName, unitHealth));

        return units;
    }
}
