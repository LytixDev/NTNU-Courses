package edu.ntnu.idatt2001.nicolahb.gui.controllers;

import edu.ntnu.idatt2001.nicolahb.TerrainType;
import edu.ntnu.idatt2001.nicolahb.gui.App;
import edu.ntnu.idatt2001.nicolahb.gui.models.DataHolderSingleton;
import edu.ntnu.idatt2001.nicolahb.gui.models.FloatingWindow;
import edu.ntnu.idatt2001.nicolahb.units.Unit;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * The Controller class corresponding to view main.fxml found in ~/resources.
 * This will be the first Controller to be loaded as the main.fxml view is loaded on startup.
 *
 * @author Nicolai H. Brand.
 * @version 20.05.2022.
 */
public class MainViewController implements Initializable {
    @FXML
    private Label health1, attack1, armor1, health2, attack2, armor2, armyOneScore, armyTwoScore, armyOneFileName,
                armyTwoFileName, armyOneName, armyTwoName, cavUnits1, cavUnits2, comUnits1, comUnits2, infUnits1,
                infUnits2, ranUnits1, ranUnits2, tUnits1, tUnits2, winningArmyName;

    @FXML
    private TitledPane titledPaneCollapse, titledPaneCollapse2;

    @FXML
    private Button startBtn, resetBtn, loadArmyOneBtn, loadArmyTwoBtn, createArmyBtn;

    @FXML
    private ListView<Unit> armyOneList, armyTwoList;

    @FXML
    private ListView<String> simulationLogList;

    @FXML
    private ComboBox<TerrainType> terrainBox;

    @FXML
    private Spinner<Double> speedSpinner;

    private SpinnerValueFactory<Double> valueFactorySpeed;

    /* Instance of the DataHolderSingleton */
    private final DataHolderSingleton data = DataHolderSingleton.getDataHolderSingleton();

    /**
     * Attempts to load an army as the first army.
     * Business logic handled by the DataHolderSingleton class.
     */
    @FXML
    void loadArmyOne() {
        data.loadArmy(1);

        /* Enable buttons if all is loaded correctly */
        if (armyTwoName.textProperty().getValue() != null) {
            resetBtn.setDisable(false);
            if (terrainBox.valueProperty().getValue() != null)
                startBtn.setDisable(false);
        }
    }

    /**
     * Attempts to load an army as the second army.
     * Business logic handled by the data class.
     */
    @FXML
    void loadArmyTwo() {
        data.loadArmy(2);

        /* Enable buttons if all is loaded correctly */
        if (armyOneName.textProperty().getValue() != null) {
            resetBtn.setDisable(false);
            if (terrainBox.valueProperty().getValue() != null)
                startBtn.setDisable(false);
        }
    }

    /**
     * Changes scene to the create_army.fxml view.
     */
    @FXML
    void createNewArmyBtn() {
        App.changeScene("create_army");
    }

    /**
     * Resets the simulation.
     * Reloads the previously loaded armies into memory.
     * Business logic handled by data's resetBattle() method.
     */
    @FXML
    void resetSimulatorBtn() {
        data.resetBattle();
        disableButtons();
    }

    /**
     * Starts the simulation between two loaded armies.
     * Business logic handled by data's startBattle() method.
     */
    @FXML
    void startSimulationBtn() {
        data.startBattle(terrainBox.getValue());
        startBtn.setDisable(true);
    }

    /**
     * Binds all the observer fields in the GUI to their corresponding observable fields in
     * the data.
     */
    public void bindObserverFields() {
        armyOneFileName.textProperty().bind(data.getArmyOnePath());
        armyTwoFileName.textProperty().bind(data.getArmyTwoPath());
        winningArmyName.textProperty().bind(data.getWinningArmy());

        /* bind dependant army fields */
        armyOneName.textProperty().bind(data.getArmyOne().getName());
        tUnits1.textProperty().bind(data.getArmyOne().getTotalUnits());
        cavUnits1.textProperty().bind(data.getArmyOne().getTotalCavalryUnits());
        comUnits1.textProperty().bind(data.getArmyOne().getTotalCommanderUnits());
        infUnits1.textProperty().bind(data.getArmyOne().getTotalInfantryUnits());
        ranUnits1.textProperty().bind(data.getArmyOne().getTotalRangedUnits());
        health1.textProperty().bind(data.getArmyOne().getAvgHealth());
        attack1.textProperty().bind(data.getArmyOne().getAvgAttack());
        armor1.textProperty().bind(data.getArmyOne().getAvgArmor());

        armyTwoName.textProperty().bind(data.getArmyTwo().getName());
        tUnits2.textProperty().bind(data.getArmyTwo().getTotalUnits());
        cavUnits2.textProperty().bind(data.getArmyTwo().getTotalCavalryUnits());
        comUnits2.textProperty().bind(data.getArmyTwo().getTotalCommanderUnits());
        infUnits2.textProperty().bind(data.getArmyTwo().getTotalInfantryUnits());
        ranUnits2.textProperty().bind(data.getArmyTwo().getTotalRangedUnits());
        health2.textProperty().bind(data.getArmyTwo().getAvgHealth());
        attack2.textProperty().bind(data.getArmyTwo().getAvgAttack());
        armor2.textProperty().bind(data.getArmyTwo().getAvgArmor());

        simulationLogList.setItems(data.getBattleLog());

        armyOneScore.textProperty().bind(data.getArmyOneScore());
        armyTwoScore.textProperty().bind(data.getArmyTwoScore());

        armyOneList.setItems(data.getArmyOne().getUnits());
        armyTwoList.setItems(data.getArmyTwo().getUnits());
    }

    /**
     * Adds listeners to all fields in order to dynamically enable or disable buttons.
     * A button that has an undefined action for certain input will therefore be disabled.
     */
    private void disableButtons() {
        /* Only disable reset button if armies are not loaded. */
        resetBtn.setDisable(armyOneName.textProperty().getValue() == null ||
                armyOneName.textProperty().getValue().isBlank() ||
                armyTwoName.textProperty().getValue() == null ||
                armyTwoName.textProperty().getValue().isBlank());

        startBtn.setDisable(resetBtn.isDisable() || terrainBox.valueProperty().getValue() == null);
    }

    /**
     * Adds listeners to certain values in the GUI that determine whether a button should be clickable.
     * Need to add listeners to all fields as the user might load things in various orders, and so
     * only listening to the change on one of the fields would not catch a change in all the states.
     * The textProperties switch between being null, being blank and having a proper value.
     * Only when they have a proper value the button should be disabled.
     */
    private void dynamicallyDisableButtons() {
        terrainBox.valueProperty().addListener((observable, ignored, newValue) ->
                startBtn.setDisable(armyOneName.textProperty().getValue() == null ||
                        armyOneName.textProperty().getValue().isBlank() ||
                        armyTwoName.textProperty().getValue() == null ||
                        armyTwoName.textProperty().getValue().isBlank()));

        armyOneName.textProperty().addListener((observable, ignored, newValue) -> {
            resetBtn.setDisable(armyTwoName.textProperty().getValue() == null ||
                    armyTwoName.textProperty().getValue().isBlank());
            startBtn.setDisable(resetBtn.isDisable() || terrainBox.valueProperty().getValue() == null);
        });

        armyTwoName.textProperty().addListener((observable, ignored, newValue) -> {
            resetBtn.setDisable(armyOneName.textProperty().getValue() == null ||
                    armyOneName.textProperty().getValue().isBlank());
            startBtn.setDisable(resetBtn.isDisable() || terrainBox.valueProperty().getValue() == null);
        });

        /* Disable all buttons apart from reload when simulation is already running */
        data.getIsSimulating().addListener((observable, ignored, newValue) -> {
            loadArmyOneBtn.setDisable(newValue);
            loadArmyTwoBtn.setDisable(newValue);
            createArmyBtn.setDisable(newValue);
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        /* init items in terrainBox */
        terrainBox.getItems().add(TerrainType.HILL);
        terrainBox.getItems().add(TerrainType.PLAINS);
        terrainBox.getItems().add(TerrainType.FOREST);

        /* init spinner */
        valueFactorySpeed = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.25, 10.0, 1.0, 0.25);
        speedSpinner.setValueFactory(valueFactorySpeed);
        speedSpinner.valueProperty().addListener((observable, ignored, newValue) -> data.setBattleDelay(newValue));

        /* collapse titled-panes with collapse ID */
        titledPaneCollapse.setExpanded(false);
        titledPaneCollapse2.setExpanded(false);

        /* add listener when a unit is pressed */
        armyOneList.getSelectionModel().selectedItemProperty().addListener((a, b, unit) ->
                FloatingWindow.promptInformation(unit.guiRepresentation()));
        armyTwoList.getSelectionModel().selectedItemProperty().addListener((a, b, unit) ->
                FloatingWindow.promptInformation(unit.guiRepresentation()));

        bindObserverFields();
        disableButtons();
        dynamicallyDisableButtons();
    }
}