package edu.ntnu.idatt2001.nicolahb.client.models;

import edu.ntnu.idatt2001.nicolahb.Army;
import edu.ntnu.idatt2001.nicolahb.Battle;
import edu.ntnu.idatt2001.nicolahb.TerrainType;
import edu.ntnu.idatt2001.nicolahb.client.App;
import edu.ntnu.idatt2001.nicolahb.exceptions.CorruptedArmyFileException;
import edu.ntnu.idatt2001.nicolahb.filehandling.CSVFileHandler;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * The class Model as in MVC.
 * (The main.fxml can be seen as the view, while the MainController is the controller in this implementation of MVC.)
 * The class handles most 'business logic' of the project.
 * Due to the nature of the MVC design pattern, there is high coupling between this class, the controllers and the
 * rest of the classes in the models package. In addition, since it deals with the 'business logic', the class is
 * also tightly coupled with other important classes.
 * Most of the methods and fields in this class is static. The rationale behind this decision is so that
 * the controller can call methods from the Model without having a specific instance of the model.
 *
 * @author Nicolai H. Brand.
 * @version 01.05.2022
 */
public class Model {

    private static ArmyObservableWrapper armyOneWrapper = new ArmyObservableWrapper(null);
    private static ArmyObservableWrapper armyTwoWrapper = new ArmyObservableWrapper(null);
    private static Battle battle;
    private static Logger battleLog = new Logger(true);

    private final static StringProperty armyOnePath = new SimpleStringProperty("filename");
    private final static StringProperty armyTwoPath = new SimpleStringProperty("filename");
    private final static StringProperty winningArmy = new SimpleStringProperty("None");

    private static Alert alert;


    /**
     * Attempts to load and then parse an army.
     * Gives visual error if an army could not be loaded.
     *
     * @param n int, which of the two armies to be loaded
     */
    public static void loadArmy(int n) {
        if (n != 1 && n!= 2) {
            /* This error will never be due to the user of the program */
            giveError("Could not load army. An internal error occurred.");
            return;
        }

        String armyFileName;

        try {
            armyFileName = selectFile();
            if (n == 1)
                armyOneWrapper.setArmy(CSVFileHandler.parseArmy(armyFileName));
            else
                armyTwoWrapper.setArmy(CSVFileHandler.parseArmy(armyFileName));

            /*
             * If program execution ends here, then the army is properly loaded, and we can set the path in the GUI.
             * This could not be done earlier as it only now is guaranteed that the army is properly loaded.
             */
            if (n == 1)
                armyOnePath.setValue(armyFileName);
            else
                armyTwoPath.setValue(armyFileName);

        } catch (IOException | CorruptedArmyFileException e) {
            giveError("Could not load army: " + e.getMessage());
        }
    }

    /**
     * Reloads the files where the armies came from.
     * Called when resetting the battle in the GUI.
     */
    private static void reloadArmies() {
        try {
            armyOneWrapper.setArmy(CSVFileHandler.parseArmy(armyOnePath.getValue()));
            armyTwoWrapper.setArmy(CSVFileHandler.parseArmy(armyTwoPath.getValue()));
        } catch (IOException | CorruptedArmyFileException e) {
            giveError("Could not load army: " + e.getMessage());
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
        if (armyOneWrapper == null || armyTwoWrapper == null) {
            giveError("Make sure both armies need are loaded");
            return;
        }

        if (terrainType == null) {
            giveError("The terrain is not set. Make sure to set the terrain.");
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
        //TODO: handle when battle already has ran

        try {
            Army winner = battle.simulateStep(battleLog);
            while (winner == null) {
                //TODO: use concurrency

                winner = battle.simulateStep(battleLog);
                armyOneWrapper.updateObservableFields();
                armyTwoWrapper.updateObservableFields();
            }
            winningArmy.setValue(winner.getName());

        } catch (Exception e) {
            giveError("An internal error occurred: " + e.getMessage());
        }
    }

    /**
     * Resets a battle.
     * Called when pressing 'reset' in the GUI.
     */
    public static void resetBattle() {
        reloadArmies();
        battle = null;
        winningArmy.setValue("None");
        battleLog.clearLog();
    }

    /**
     * Opens a window for the user to select a file.
     *
     * @return selectedFile String, the absolute path of the file chosen
     * @throws FileNotFoundException, if file could not be found
     */
    public static String selectFile() throws FileNotFoundException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open army from file");
        File selectedFile = fileChooser.showOpenDialog(App.getStage());

        /* check if a file selected, and if selected file has read permissions */
        if (selectedFile != null && selectedFile.canRead())
            return selectedFile.getAbsolutePath();
        else
            throw new FileNotFoundException("Selected file could not be read, or does not have read permissions");
    }

    /**
     * Opens an alert window and displays an error message to the user.
     *
     * @param message String, the error message to be shown
     */
    public static void giveError(String message) {
        alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("An error occurred");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /* getters for StringProperties that are bound to fields in controllers */

    public static StringProperty getArmyOnePath() { return armyOnePath; }

    public static StringProperty getArmyTwoPath() { return armyTwoPath; }

    public static StringProperty getWinningArmy() { return winningArmy; }

    public static ArmyObservableWrapper getArmyOneWrapper() { return armyOneWrapper; }

    public static ArmyObservableWrapper getArmyTwoWrapper() { return armyTwoWrapper; }

    public static ObservableList<String> getBattleLog() { return battleLog.getLog(); }
}
