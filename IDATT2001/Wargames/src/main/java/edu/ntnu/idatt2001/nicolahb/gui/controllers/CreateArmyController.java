package edu.ntnu.idatt2001.nicolahb.gui.controllers;

import edu.ntnu.idatt2001.nicolahb.Army;
import edu.ntnu.idatt2001.nicolahb.filehandling.CSVFileHandler;
import edu.ntnu.idatt2001.nicolahb.gui.App;
import edu.ntnu.idatt2001.nicolahb.gui.models.ArmyObservableWrapper;
import edu.ntnu.idatt2001.nicolahb.gui.models.DataHolderSingleton;
import edu.ntnu.idatt2001.nicolahb.gui.models.FloatingWindow;
import edu.ntnu.idatt2001.nicolahb.units.Unit;
import edu.ntnu.idatt2001.nicolahb.units.UnitFactory;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

import static edu.ntnu.idatt2001.nicolahb.gui.models.FloatingWindow.promptErrorMessage;
import static edu.ntnu.idatt2001.nicolahb.gui.models.FloatingWindow.saveFile;

/**
 * The Controller class corresponding to the create_army view found in ~/resources.
 * Attempts to load the newly created army into the DataHolderSingleton once the user
 * presses 'load to battle'. Gives visual error messages when fit.
 *
 * @author Nicolai H. Brand.
 * @version 19.05.2022.
 */
public class CreateArmyController implements Initializable {

    /* Temporary army that created units are put into and eventually loaded into memory/saved to file */
    private Army tmpArmy;

    private ArmyObservableWrapper tmpArmyObservable;

    @FXML
    private Spinner<Integer> unitAmountSpinner, unitHealthSpinner;

    SpinnerValueFactory<Integer> valueFactoryAmount, valueFactoryHealth;

    @FXML
    private Button saveBtn, loadBtn, addUnitBtn;

    @FXML
    private TextField armyNameField, unitNameField;

    @FXML
    private ComboBox<String> unitTypeBox;

    @FXML
    private ListView<Unit> viewOfArmy;

    /**
     * Attempts to add units to the temporary army given the input the user has typed in.
     * Gives a visual floating window error if the input is not valid, although this should
     * in theory never happen as the button that calls this function is only enabled when the
     * input is valid.
     */
    @FXML
    void addUnitsToArmy() {
        try {
            int amount = unitAmountSpinner.getValue();
            if (amount < 1) {
                promptErrorMessage("Need to add a minimum of one unit to the army, and not + " + amount);
            }

            /* Potential errors handled by UnitFactory.buildUnits() */
            int health = unitHealthSpinner.getValue();

            String name = unitNameField.getText();
            if (name.isBlank()) {
                promptErrorMessage("Unit name cannot be blank.");
                return;
            }

            if (name.contains(",")) {
                promptErrorMessage("Unit name cannot contain \"'\"");
                return;
            }

            if (unitTypeBox.getValue() == null) {
                promptErrorMessage("Unit has to have a type.");
                return;
            }

            tmpArmy.addAllUnits(UnitFactory.buildUnits(unitTypeBox.getValue(), name, health, amount));
            tmpArmyObservable.updateObservableFields();
            resetUnitFields();

        } catch (Exception e) {
            promptErrorMessage("An internal error occurred: " + e.getMessage());
        }
    }

    /**
     * Change scene back to the main scene.
     */
    @FXML
    void goToMain() {
        App.changeScene("main");
    }

    /**
     * Load newly created army into battle.
     * Prompts the user with a floating window where they can chose to load the army into slot 1 or 2.
     */
    @FXML
    void loadArmyIntoBattle() {
        tmpArmy.setName(armyNameField.getText());

        /* will return true if army one is chosen, else false */
        boolean rc = FloatingWindow.giveBinaryChoice("Choose which army to load into",
                "Choose which side to load the newly created army into", "Army One",
                "Army Two");

        if (rc)
            DataHolderSingleton.setArmyOne(tmpArmy.deepCopy());
        else
            DataHolderSingleton.setArmyTwo(tmpArmy.deepCopy());
    }

    /**
     * Clears the textFields in the GUI.
     */
    private void resetUnitFields() {
        unitNameField.clear();
        unitTypeBox.valueProperty().set(null);
        valueFactoryAmount.setValue(1);
        valueFactoryHealth.setValue(20);
    }

    /**
     * Reset every field in the GUI.
     */
    @FXML
    void resetArmyFields() {
        resetUnitFields();
        armyNameField.clear();
        tmpArmy.removeAll();
        tmpArmyObservable.updateObservableFields();
    }

    /**
     * Attempts to save the temporarily created army into a file.
     * Gives a visual error in the form of a floating window if the file could not be saved or name contains a comma.
     */
    @FXML
    void saveArmyToFile() {
        if (armyNameField.getText().contains(",")) {
            promptErrorMessage("Army name cannot contain \"'\"");
            return;
        }

        tmpArmy.setName(armyNameField.getText());
        try {
            String placeToSave = saveFile(App.getStage(), tmpArmy.getName() + ".csv");
            CSVFileHandler.writeArmyData(tmpArmy, placeToSave);
        } catch (Exception e) {
            promptErrorMessage("Could not save army to file. Error caused by " + e.getCause() +
                    ". Error message: " + e.getMessage());
        }
    }

    /**
     * Adds listeners to all fields in order to dynamically enable or disable buttons.
     * A button that has an undefined action for certain input will therefore not be able to be pressed.
     */
    private void disableButtons() {
        saveBtn.setDisable(true);
        loadBtn.setDisable(true);
        addUnitBtn.setDisable(true);

        /* When a unit has been added, show load and save buttons */
        addUnitBtn.setOnAction(actionEvent -> {
            loadBtn.setDisable(false);
            saveBtn.setDisable(false);
            addUnitsToArmy();
        });

        unitNameField.textProperty().addListener(((observable, ignored, newValue) -> addUnitBtn.setDisable(newValue.isBlank() || unitTypeBox.getValue() == null)));
        unitTypeBox.valueProperty().addListener(((observable, ignored, newValue) -> addUnitBtn.setDisable(newValue == null || unitNameField.getText().isBlank())));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        /* init items in terrainBox */
        unitTypeBox.getItems().add("CavalryUnit");
        unitTypeBox.getItems().add("CommanderUnit");
        unitTypeBox.getItems().add("InfantryUnit");
        unitTypeBox.getItems().add("RangedUnit");

        /* init spinners */
        valueFactoryAmount = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100000);
        valueFactoryHealth = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100000);
        valueFactoryAmount.setValue(1);
        valueFactoryHealth.setValue(20);
        unitAmountSpinner.setValueFactory(valueFactoryAmount);
        unitHealthSpinner.setValueFactory(valueFactoryHealth);

        /* init temporary army */
        tmpArmy = new Army("Temporary");
        tmpArmyObservable = new ArmyObservableWrapper(tmpArmy);
        viewOfArmy.setItems(tmpArmyObservable.getUnits());

        /* disable and grey out buttons that can't be pressed */
        disableButtons();
    }
}