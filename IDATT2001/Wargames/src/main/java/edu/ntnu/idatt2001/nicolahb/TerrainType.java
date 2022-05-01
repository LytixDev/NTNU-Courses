package edu.ntnu.idatt2001.nicolahb;

/**
 * Enumeration class that represents the type of terrain for a given battle.
 * Used in bonus calculations for the various units as the lay of the land favours
 * some units and disfavour others.
 * TODO: The type NEUTRAL is only used in testing. Remove this when done. Having a neutral type that is sure to
 * not interfere with calculations of bonuses removes one variable and makes testing each component easier.
 *
 * @author Nicolai H. Brand
 * @version 13.04.2022
 */
public enum TerrainType {
    HILL,
    PLAINS,
    FOREST,
    NEUTRAL;
}
