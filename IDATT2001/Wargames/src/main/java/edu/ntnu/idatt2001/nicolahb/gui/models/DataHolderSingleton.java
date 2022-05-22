package edu.ntnu.idatt2001.nicolahb.gui.models;

import edu.ntnu.idatt2001.nicolahb.Army;
import edu.ntnu.idatt2001.nicolahb.Battle;
import edu.ntnu.idatt2001.nicolahb.TerrainType;
import edu.ntnu.idatt2001.nicolahb.gui.App;
import edu.ntnu.idatt2001.nicolahb.exceptions.CorruptedArmyFileException;
import edu.ntnu.idatt2001.nicolahb.filehandling.ArmyFileHandler;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

import java.io.IOException;


/**
 * The singleton class DataHolderSingleton.
 * The class holds the data in memory and in conjunction with the rest of the models package
 * handles most 'business logic' of the project to adhere to the MVC design pattern.
 * Due to the nature of the MVC design pattern, there is high coupling between this class, the controller(s) and the
 * rest of the classes in the models package. In addition, since it deals with the 'business logic', the class is
 * also tightly coupled with other important classes of the program.
 * This class is a Singleton which means methods and fields are static. The rationale behind this decision is so that
 * the controller can call methods from the Model without having a specific instance of the model. In addition,
 * if there were many controllers, they could all interactive with the same data without having their own instance
 * of the data.
 *
 * @author Nicolai H. Brand.
 * @version 21.05.2022
 */
public final class DataHolderSingleton {

    /* Time in milliseconds between each attack */
    private final int DEFAULT_BATTLE_DELAY = 100;
    private int battleStepDelay = DEFAULT_BATTLE_DELAY;
    private final ArmyObservableWrapper armyOneWrapper;
    private final ArmyObservableWrapper armyTwoWrapper;

    /* copy of armies used to reload once a battle is finished */
    private Army armyOneCopy;
    private Army armyTwoCopy;
    private Battle battle;
    private final Logger<String> battleLog;

    private final StringProperty armyOnePath;
    private final StringProperty armyTwoPath;
    private final StringProperty winningArmy;
    private final StringProperty armyOneScore;
    private final StringProperty armyTwoScore;
    /* Is set to true when a simulation is running */
    private final BooleanProperty isSimulating;

    /* Eagerly initialize singleton */
    private static final DataHolderSingleton dataHolderSingleton = new DataHolderSingleton();

    /* Private constructor */
    private DataHolderSingleton() {
        /* Set army wrappers to empty armies to begin with */
        armyOneWrapper = new ArmyObservableWrapper();
        armyTwoWrapper = new ArmyObservableWrapper();

        battleLog = new Logger<>(true);
        armyOnePath = new SimpleStringProperty("filename");
        armyTwoPath = new SimpleStringProperty("filename");
        winningArmy = new SimpleStringProperty("None");
        armyOneScore = new SimpleStringProperty("0");
        armyTwoScore = new SimpleStringProperty("0");
        isSimulating = new SimpleBooleanProperty(false);
    }

    /* Get only instance of singleton */
    public static DataHolderSingleton getDataHolderSingleton() {
        return dataHolderSingleton;
    }

    /**
     * Attempts to load and then parse an army.
     * Gives visual error if an army could not be loaded.
     *
     * @param armyToLoad int, which of the two armies to be loaded
     */
    public void loadArmy(int armyToLoad) {
        if (armyToLoad != 1 && armyToLoad!= 2) {
            /* This error will never be due to the user of the program */
            FloatingWindow.promptErrorMessage("Could not load army. An internal error occurred.");
            return;
        }

        String armyFileName;

        try {
            armyFileName = FloatingWindow.selectFile(App.getStage());
            if (armyToLoad == 1) {
                armyOneCopy = ArmyFileHandler.parseArmy(armyFileName);
                /* Use a deep copy of the army to be able to reload it later on without it having changed */
                armyOneWrapper.setArmy(armyOneCopy.deepCopy());
            } else {
                armyTwoCopy = ArmyFileHandler.parseArmy(armyFileName);
                /* Use a deep copy of the army to be able to reload it later on without it having changed */
                armyTwoWrapper.setArmy(armyTwoCopy.deepCopy());
            }

            /*
             * If program execution ends here, then the army is properly loaded, and we can set the path in the GUI.
             * This could not be done earlier as it only now is guaranteed that the army is properly loaded.
             */
            if (armyToLoad == 1)
                armyOnePath.setValue(armyFileName);
            else
                armyTwoPath.setValue(armyFileName);

        } catch (IOException | CorruptedArmyFileException e) {
            FloatingWindow.promptErrorMessage("Could not load army: " + e.getMessage());
        }
    }

    /**
     * DEPRECATED as new system stores a copy of the armies.
     * Reloads the files where the armies came from.
     * Called when resetting the battle in the GUI.
     */
    @Deprecated
    private void reloadArmies() {
        try {
            armyOneWrapper.setArmy(ArmyFileHandler.parseArmy(armyOnePath.getValue()));
            armyTwoWrapper.setArmy(ArmyFileHandler.parseArmy(armyTwoPath.getValue()));
        } catch (IOException | CorruptedArmyFileException e) {
            /*
             * default name for files are filename, and so if this is a substring of the error it means the files
             * were never loaded and so can't be reset.
             */
            if (e.getMessage().contains("filename"))
                FloatingWindow.promptErrorMessage("Can not reload armies, because armies were never loaded");
            else
                FloatingWindow.promptErrorMessage("Could not load army: " + e.getMessage());
        }
    }

    /**
     * Starts a battle from the Battle class.
     * Uses the armies already loaded in the Model class.
     * Gives visual error if the armies or terrain are null.
     *
     * @param terrainType TerrainType, the type of terrain the battle is taken place at
     */
    public void startBattle(TerrainType terrainType) {
        /* The start button should not be clickable if both armies are null, but keep these checks for safety */
        if (armyOneWrapper.getArmy() == null || armyTwoWrapper.getArmy() == null) {
            FloatingWindow.promptErrorMessage("Make sure both armies are loaded.");
            return;
        }

        if (terrainType == null) {
            FloatingWindow.promptErrorMessage("The terrain is not set. Make sure to set the terrain.");
            return;
        }

        battle = new Battle(armyOneWrapper.getArmy(), armyTwoWrapper.getArmy(), terrainType);
        simulateBattle();
    }

    /**
     * Simulates a battle, step for step.
     * Called from the startBattle method.
     * Simulates a new battle step until there is a winner.
     * Updates the observable fields given the winner.
     *
     */
    private void simulateBattle() {
        Thread thread = new Thread(() -> {
            try {
                Army winner = battle.simulateSingleStepGUI(battleLog);
                isSimulating.setValue(true);

                while (winner == null) {
                    Thread.sleep(battleStepDelay);

                    winner = battle.simulateSingleStepGUI(battleLog);
                    /* GUI is on a different thread, so queue changes */
                    Platform.runLater(() -> {
                        armyOneWrapper.updateObservableFields();
                        armyTwoWrapper.updateObservableFields();
                    });
                }

                /* GUI is on a different thread, so queue changes.
                 * Need to copy army to make sure lambda expression has a final value, as the runLater
                 * is undetermined when it will run.
                 */
                Army finalWinner = winner;
                Platform.runLater(() -> {
                    /* Increment winning armies' score label */
                    winningArmy.setValue(finalWinner.getName());
                    if (finalWinner.equals(armyOneWrapper.getArmy()))
                        armyOneScore.setValue(String.valueOf(Integer.parseInt(armyOneScore.getValue()) + 1));
                    else
                        armyTwoScore.setValue(String.valueOf(Integer.parseInt(armyTwoScore.getValue()) + 1));
                });

                isSimulating.setValue(false);

            } catch (Exception e) {
                FloatingWindow.promptErrorMessage("An internal error occurred: " + e.getMessage());
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Resets a battle.
     * Called when pressing 'reload' in the GUI.
     */
    public void resetBattle() {
        //reloadArmies();
        armyOneWrapper.setArmy(armyOneCopy.deepCopy());
        armyTwoWrapper.setArmy(armyTwoCopy.deepCopy());
        battle = null;
        winningArmy.setValue("None");
        battleLog.clearLog();
        isSimulating.setValue(false);
    }


    /* getters for Observable fields that are bound to fields in controllers */
    public StringProperty getArmyOnePath() { return armyOnePath; }

    public StringProperty getArmyTwoPath() { return armyTwoPath; }

    public StringProperty getWinningArmy() { return winningArmy; }

    public StringProperty getArmyOneScore() { return armyOneScore; }

    public StringProperty getArmyTwoScore() { return armyTwoScore; }

    public BooleanProperty getIsSimulating() { return isSimulating; }

    public ArmyObservableWrapper getArmyOne() { return armyOneWrapper; }

    public ArmyObservableWrapper getArmyTwo() { return armyTwoWrapper; }

    public ObservableList<String> getBattleLog() { return battleLog.getLog(); }

    /* Setters */
    public void setArmyOne(Army army) {
        armyOneCopy = army;
        armyOneWrapper.setArmy(armyOneCopy.deepCopy());
    }

    public void setArmyTwo(Army army) {
        armyTwoCopy = army;
        armyTwoWrapper.setArmy(armyTwoCopy.deepCopy());
    }

    public void setBattleDelay(Double scaleFactor) {
        battleStepDelay = (int) (DEFAULT_BATTLE_DELAY / scaleFactor);
    }
}