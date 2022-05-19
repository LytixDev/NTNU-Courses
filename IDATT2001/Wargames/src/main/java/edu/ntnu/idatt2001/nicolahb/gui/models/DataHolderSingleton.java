package edu.ntnu.idatt2001.nicolahb.gui.models;

import edu.ntnu.idatt2001.nicolahb.Army;
import edu.ntnu.idatt2001.nicolahb.Battle;
import edu.ntnu.idatt2001.nicolahb.TerrainType;
import edu.ntnu.idatt2001.nicolahb.gui.App;
import edu.ntnu.idatt2001.nicolahb.exceptions.CorruptedArmyFileException;
import edu.ntnu.idatt2001.nicolahb.filehandling.CSVFileHandler;
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
 * @version 19.05.2022
 */
public final class DataHolderSingleton {

    /* Time in milliseconds between each attack */
    private static final int DEFAULT_BATTLE_DELAY = 100;
    private static int battleStepDelay = DEFAULT_BATTLE_DELAY;
    private static final ArmyObservableWrapper armyOneWrapper = new ArmyObservableWrapper();
    private static final ArmyObservableWrapper armyTwoWrapper = new ArmyObservableWrapper();

    /* copy of armies used to reload once a battle is finished */
    private static Army armyOneCopy;
    private static Army armyTwoCopy;
    private static Battle battle;
    private static Logger battleLog = new Logger(true);

    private final static StringProperty armyOnePath = new SimpleStringProperty("filename");
    private final static StringProperty armyTwoPath = new SimpleStringProperty("filename");
    private final static StringProperty winningArmy = new SimpleStringProperty("None");
    private final static StringProperty armyOneScore = new SimpleStringProperty("0");
    private final static StringProperty armyTwoScore = new SimpleStringProperty("0");
    /* Is set to true when a simultion is running */
    private final static BooleanProperty isSimulating = new SimpleBooleanProperty(false);

    /* Private constructor */
    private DataHolderSingleton() {}

    /**
     * Attempts to load and then parse an army.
     * Gives visual error if an army could not be loaded.
     *
     * @param armyToLoad int, which of the two armies to be loaded
     */
    public static void loadArmy(int armyToLoad) {
        if (armyToLoad != 1 && armyToLoad!= 2) {
            /* This error will never be due to the user of the program */
            FloatingWindow.promptErrorMessage("Could not load army. An internal error occurred.");
            return;
        }

        String armyFileName;

        try {
            armyFileName = FloatingWindow.selectFile(App.getStage());
            if (armyToLoad == 1) {
                armyOneCopy = CSVFileHandler.parseArmy(armyFileName);
                /* Use a deep copy of the army to be able to reload it later on without it having changed */
                armyOneWrapper.setArmy(armyOneCopy.deepCopy());
            } else {
                armyTwoCopy = CSVFileHandler.parseArmy(armyFileName);
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
    private static void reloadArmies() {
        try {
            armyOneWrapper.setArmy(CSVFileHandler.parseArmy(armyOnePath.getValue()));
            armyTwoWrapper.setArmy(CSVFileHandler.parseArmy(armyTwoPath.getValue()));
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
    public static void startBattle(TerrainType terrainType) {
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
    private static void simulateBattle() {
        Thread thread = new Thread(() -> {
            try {
                Army winner = battle.simulateStep(battleLog);
                isSimulating.setValue(true);

                while (winner == null) {
                    Thread.sleep(battleStepDelay);

                    winner = battle.simulateStep(battleLog);
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
    public static void resetBattle() {
        //reloadArmies();
        armyOneWrapper.setArmy(armyOneCopy.deepCopy());
        armyTwoWrapper.setArmy(armyTwoCopy.deepCopy());
        battle = null;
        winningArmy.setValue("None");
        battleLog.clearLog();
        isSimulating.setValue(false);
    }


    /* getters for Observable fields that are bound to fields in controllers */
    public static StringProperty getArmyOnePath() { return armyOnePath; }

    public static StringProperty getArmyTwoPath() { return armyTwoPath; }

    public static StringProperty getWinningArmy() { return winningArmy; }

    public static StringProperty getArmyOneScore() { return armyOneScore; }

    public static StringProperty getArmyTwoScore() { return armyTwoScore; }

    public static BooleanProperty getIsSimulating() { return isSimulating; }

    public static ArmyObservableWrapper getArmyOne() { return armyOneWrapper; }

    public static ArmyObservableWrapper getArmyTwo() { return armyTwoWrapper; }

    public static ObservableList<String> getBattleLog() { return battleLog.getLog(); }

    /* Setters */
    public static void setArmyOne(Army army) {
        armyOneCopy = army;
        armyOneWrapper.setArmy(armyOneCopy.deepCopy());
    }

    public static void setArmyTwo(Army army) {
        armyTwoCopy = army;
        armyTwoWrapper.setArmy(armyTwoCopy.deepCopy());
    }

    public static void setBattleDelay(Double scaleFactor) {
        battleStepDelay = (int) (DEFAULT_BATTLE_DELAY / scaleFactor);
    }
}