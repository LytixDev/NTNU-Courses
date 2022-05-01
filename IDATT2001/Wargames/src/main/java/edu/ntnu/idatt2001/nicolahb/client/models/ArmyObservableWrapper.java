package edu.ntnu.idatt2001.nicolahb.client.models;

import edu.ntnu.idatt2001.nicolahb.Army;
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
 * @author Nicolai H. Brand
 * @version 30.04.2022
 */
public class ArmyObservableWrapper {

    private Army army;
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty totalUnits = new SimpleStringProperty();
    private final StringProperty totalCavalryUnits = new SimpleStringProperty();
    private final StringProperty totalCommanderUnits = new SimpleStringProperty();
    private final StringProperty totalInfantryUnits = new SimpleStringProperty();
    private final StringProperty totalRangedUnits = new SimpleStringProperty();
    private ObservableList<?> units;


    /**
     * Instantiates a new Army observable wrapper.
     *
     * @param army, the army to be made observable
     */
    public ArmyObservableWrapper(Army army)  {
        this.army = army;

        if (this.army != null)
            updateObservableFields();
    }

    /**
     * Updates the observable fields
     */
    public void updateObservableFields() {
        name.setValue(army.getName());
        totalUnits.setValue(String.valueOf(army.getUnits().size()));
        totalCavalryUnits.setValue(String.valueOf(army.getCavalryUnits().size()));
        totalCommanderUnits.setValue(String.valueOf(army.getCommanderUnits().size()));
        totalInfantryUnits.setValue(String.valueOf(army.getInfantryUnits().size()));
        totalRangedUnits.setValue(String.valueOf(army.getRangedUnits().size()));
        units = FXCollections.observableArrayList(army.getUnits());
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

    public ObservableList<?> getUnits() {
        return units;
    }
}