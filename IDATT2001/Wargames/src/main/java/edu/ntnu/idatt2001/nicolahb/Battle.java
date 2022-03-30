package edu.ntnu.idatt2001.nicolahb;

import edu.ntnu.idatt2001.nicolahb.units.Unit;
import edu.ntnu.idatt2001.nicolahb.exceptions.SimulationAlreadyRanException;

import java.util.Random;

/**
 * Class Battle
 * Purpose of the class is to simulate a battle between two armies consisting of multiple units.
 * @author Nicolai Brand.
 * @version 28.03.2022
 */
public class Battle {
    private Army armyOne;
    private Army armyTwo;
    private boolean hasRun = false;

    /**
     * Constructor for battle.
     * Takes in two armies.
     * @param armyOne, Army.
     * @param armyTwo, Army.
     */
    public Battle(Army armyOne, Army armyTwo) {
        this.armyOne = armyOne;
        this.armyTwo = armyTwo;
    }

    /**
     * Simulation of a battle between two armies.
     * Every turn, a random army attacks the other with a random unit.
     * If a unit has health 0 it is removed.
     * This goes on as long as both armies have units.
     * @throws SimulationAlreadyRanException, if the simulation has already run
     * @return Army, the army that still has a unit(s) left.
     */
    public Army simulate() throws SimulationAlreadyRanException {
        if (hasRun) throw new SimulationAlreadyRanException("Simulation has already run");
        while (armyOne.hasUnits() && armyTwo.hasUnits()) {
            Unit unitOne = armyOne.getRandom();
            Unit unitTwo = armyTwo.getRandom();

            /* The variable r will be a random int from 0 to 1 */
            int r = new Random().nextInt(0, 2);
            if (r == 0) {
                unitOne.attack(unitTwo);
                if (unitTwo.getHealth() <= 0)
                    armyTwo.remove(unitTwo);
            } else {
                unitTwo.attack(unitOne);
                if (unitOne.getHealth() <= 0)
                    armyOne.remove(unitOne);
            }
        }

        this.hasRun = true;
        return armyOne.hasUnits() ? armyOne : armyTwo;
    }
}
