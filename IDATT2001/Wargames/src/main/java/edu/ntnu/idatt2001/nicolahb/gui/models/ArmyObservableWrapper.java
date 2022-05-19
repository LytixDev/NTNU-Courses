package edu.ntnu.idatt2001.nicolahb.gui.models;

import edu.ntnu.idatt2001.nicolahb.Army;
import edu.ntnu.idatt2001.nicolahb.units.Unit;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * The class Army observable wrapper.
 * Holds an instance of the Army class, but makes the interesting fields
 * for the GUI into observable fields that can be observed by GUI components
 * from a controller. This class attempts to follow the observer design
 * pattern, and so there will naturally be some unavoidable coupling here.
 *
 * @author Nicolai H. Brand.
 * @version 19.05.2022.
 */
public class ArmyObservableWrapper {

    private Army army;
    private final StringProperty name = new SimpleStringProperty("None");
    private final StringProperty totalUnits = new SimpleStringProperty("None");
    private final StringProperty totalCavalryUnits = new SimpleStringProperty("None");
    private final StringProperty totalCommanderUnits = new SimpleStringProperty("None");
    private final StringProperty totalInfantryUnits = new SimpleStringProperty("None");
    private final StringProperty totalRangedUnits = new SimpleStringProperty("None");
    private final StringProperty avgHealth = new SimpleStringProperty("None");
    private final StringProperty avgAttack = new SimpleStringProperty("None");
    private final StringProperty avgArmor = new SimpleStringProperty("None");
    private ObservableList<Unit> units = FXCollections.observableArrayList();


    /**
     * Instantiates a new Army observable wrapper.
     *
     * @param army, the army to be made observable.
     */
    public ArmyObservableWrapper(Army army)  {
        this.army = army;
        updateObservableFields();
    }

    /**
     * Instantiates a new Army observable wrapper and sets the army to null.
     */
    public ArmyObservableWrapper() {
        this.army = null;
    }

    /**
     * Updates the observable fields.
     */
    public void updateObservableFields() {
        name.setValue(army.getName());
        totalUnits.setValue(String.valueOf(army.getUnits().size()));
        totalCavalryUnits.setValue(String.valueOf(army.getCavalryUnits().size()));
        totalCommanderUnits.setValue(String.valueOf(army.getCommanderUnits().size()));
        totalInfantryUnits.setValue(String.valueOf(army.getInfantryUnits().size()));
        totalRangedUnits.setValue(String.valueOf(army.getRangedUnits().size()));
        units.setAll(army.getUnits());

        /* Avoids division by zero error */
        if (army.getUnits().size() > 0) {
            avgHealth.setValue(String.valueOf(army.getUnits()
                    .stream()
                    .map(Unit::getHealth)
                    .reduce(0, Integer::sum) / army.getUnits().size()));

            avgAttack.setValue(String.valueOf(army.getUnits()
                    .stream()
                    .map(Unit::getAttack)
                    .reduce(0, Integer::sum) / army.getUnits().size()));

            avgArmor.setValue(String.valueOf(army.getUnits()
                    .stream()
                    .map(Unit::getArmor)
                    .reduce(0, Integer::sum) / army.getUnits().size()));

        } else {
            avgHealth.setValue("None");
            avgAttack.setValue("None");
            avgArmor.setValue("None");
        }
    }

    /* setters */
    public void setArmy(Army army) {
        this.army = army;
        updateObservableFields();
    }

    /* getters */
    public Army getArmy() {
        return army;
    }

    public StringProperty getName() {
        return name;
    }

    public StringProperty getTotalUnits() {
        return totalUnits;
    }

    public StringProperty getTotalCavalryUnits() {
        return totalCavalryUnits;
    }

    public StringProperty getTotalCommanderUnits() {
        return totalCommanderUnits;
    }

    public StringProperty getTotalInfantryUnits() {
        return totalInfantryUnits;
    }

    public StringProperty getTotalRangedUnits() {
        return totalRangedUnits;
    }

    public StringProperty getAvgHealth() {
        return avgHealth;
    }

    public StringProperty getAvgAttack() {
        return avgAttack;
    }

    public StringProperty getAvgArmor() {
        return avgArmor;
    }

    public ObservableList<Unit> getUnits() {
        return units;
    }
}