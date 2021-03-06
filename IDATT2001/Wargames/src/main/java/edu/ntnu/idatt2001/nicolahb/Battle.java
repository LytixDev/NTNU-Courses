package edu.ntnu.idatt2001.nicolahb;

import edu.ntnu.idatt2001.nicolahb.gui.models.Logger;
import edu.ntnu.idatt2001.nicolahb.units.Unit;
import edu.ntnu.idatt2001.nicolahb.exceptions.SimulationAlreadyRanException;
import javafx.application.Platform;

import java.util.Random;

/**
 * Class Battle
 * Purpose of the class is to simulate a battle between two armies consisting of multiple units.
 * @author Nicolai Brand.
 * @version 20.05.2022
 */
public class Battle {
    private final Army armyOne;
    private final Army armyTwo;
    private final TerrainType terrainType;
    private boolean hasRun = false;

    /**
     * Constructor for battle.
     * Takes in two armies.
     * @param armyOne, Army.
     * @param armyTwo, Army.
     * @param terrainType, TerrainType, the lay of the land of the battle.
     */
    public Battle(Army armyOne, Army armyTwo, TerrainType terrainType) {
        this.armyOne = armyOne;
        this.armyTwo = armyTwo;
        this.terrainType = terrainType;
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
            simulateSingleAttack();
        }

        this.hasRun = true;
        return armyOne.hasUnits() ? armyOne : armyTwo;
    }

    /**
     * Specialized method to simulate one step of a battle.
     * Makes one attack of the simulation, and then ends.
     * Only meant to be used in a GUI application.
     * @param log Logger, the log to append information about the attack.
     * @return Army, the winning army. Null if there is no winning army.
     * @throws SimulationAlreadyRanException if the simulation has already ran.
     */
    public Army simulateSingleStepGUI(Logger<String> log) throws SimulationAlreadyRanException {
        if (hasRun) throw new SimulationAlreadyRanException("Simulation has already run");
        simulateSingleAttackGUI(log);

        if (!armyOne.hasUnits()) {
            this.hasRun = true;
            return armyTwo;
        }
        if (!armyTwo.hasUnits()) {
            this.hasRun = true;
            return armyOne;
        }

        /* No winner yet, so return null */
        return null;
    }

    /**
     * Simulates one attack between two units from the two armies in the battle.
     * If the defending unit has health zero or less, then this unit will become "dead"
     * and removed from its corresponding army.
     */
    private void simulateSingleAttack() {
        Unit unitOne = armyOne.getRandom();
        Unit unitTwo = armyTwo.getRandom();

        /* The variable r will be a random int from 0 to 1 */
        int r = new Random().nextInt(0, 2);
        if (r == 0) {
            unitOne.attack(unitTwo, terrainType);
            if (unitTwo.getHealth() <= 0)
                armyTwo.remove(unitTwo);

        } else {
            unitTwo.attack(unitOne, terrainType);
            if (unitOne.getHealth() <= 0)
                armyOne.remove(unitOne);
        }
    }
    /**
     * Specialized method meant to be called from a JAVAFX application.
     * Might not be called from the main application thread.
     * @param log Logger, the log to add information about the attack on.
     */
    private void simulateSingleAttackGUI(Logger<String> log) {
        Unit unitOne = armyOne.getRandom();
        Unit unitTwo = armyTwo.getRandom();

        /* The variable r will be a random int from 0 to 1 */
        int r = new Random().nextInt(0, 2);
        if (r == 0) {
            unitOne.attack(unitTwo, terrainType);
            /* Method might be called from a thread that is outside the main application thread,
            * and therefore needs to queue updates to the GUI.
            */
            Platform.runLater(() -> log.addLogItem(unitOne.getName() + " attacked " + unitTwo.getName() +
                    " for " + unitOne.calculateAttackDamage(unitTwo, terrainType) + " damage"));

            if (unitTwo.getHealth() <= 0) {
                Platform.runLater(() -> log.addLogItem(unitTwo.getName() + " died"));
                armyTwo.remove(unitTwo);
            }

        } else {
            unitTwo.attack(unitOne, terrainType);
            Platform.runLater(() -> log.addLogItem(unitTwo.getName() + " attacked " + unitOne.getName() +
                    " for " + unitTwo.calculateAttackDamage(unitOne, terrainType) + " damage"));

            if (unitOne.getHealth() <= 0) {
                Platform.runLater(() -> log.addLogItem(unitOne.getName() + " died"));
                armyOne.remove(unitOne);
            }
        }
    }
}
