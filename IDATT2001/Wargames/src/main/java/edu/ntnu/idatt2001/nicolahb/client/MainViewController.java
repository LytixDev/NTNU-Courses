package edu.ntnu.idatt2001.nicolahb.client;

import edu.ntnu.idatt2001.nicolahb.TerrainType;
import edu.ntnu.idatt2001.nicolahb.client.models.Model;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.net.URL;
import java.util.ResourceBundle;

public class MainViewController implements Initializable {

    @FXML
    private Label armyOneFileName;

    @FXML
    private ListView<String> armyOneList;

    @FXML
    private Label armyOneName;

    @FXML
    private Label armyTwoFileName;

    @FXML
    private ListView<String> armyTwoList;

    @FXML
    private Label armyTwoName;

    @FXML
    private Label cavUnits1;

    @FXML
    private Label cavUnits2;

    @FXML
    private Label comUnits1;

    @FXML
    private Label comUnits2;

    @FXML
    private Label infUnits1;

    @FXML
    private Label infUnits2;

    @FXML
    private Label ranUnits1;

    @FXML
    private Label ranUnits2;

    @FXML
    private ListView<String> simulationLogList;

    @FXML
    private Label tUnits1;

    @FXML
    private Label tUnits2;

    @FXML
    private ComboBox<TerrainType> terrainBox;

    @FXML
    private Label winningArmyName;

    @FXML
    void loadArmyOneBtn(ActionEvent event) {
        Model.loadArmy(1);
        armyOneList.setItems((ObservableList<String>) Model.getArmyOneWrapper().getUnits());
    }

    @FXML
    void loadArmyTwoBtn(ActionEvent event) {
        Model.loadArmy(2);
        armyTwoList.setItems((ObservableList<String>) Model.getArmyTwoWrapper().getUnits());
    }

    @FXML
    void pauseSimulationBtn(ActionEvent event) {

    }

    @FXML
    void resetSimulatorBtn(ActionEvent event) {
        Model.resetBattle();
    }

    @FXML
    void startSimulationBtn() {
        Model.startBattle(terrainBox.getValue());
    }

    public void bindObserverFields() {
        armyOneFileName.textProperty().bind(Model.getArmyOnePath());
        armyTwoFileName.textProperty().bind(Model.getArmyTwoPath());
        winningArmyName.textProperty().bind(Model.getWinningArmy());

        /* bind dependant army fields */
        armyOneName.textProperty().bind(Model.getArmyOneWrapper().getName());
        tUnits1.textProperty().bind(Model.getArmyOneWrapper().getTotalUnits());
        cavUnits1.textProperty().bind(Model.getArmyOneWrapper().getTotalCavalryUnits());
        comUnits1.textProperty().bind(Model.getArmyOneWrapper().getTotalCommanderUnits());
        infUnits1.textProperty().bind(Model.getArmyOneWrapper().getTotalInfantryUnits());
        ranUnits1.textProperty().bind(Model.getArmyOneWrapper().getTotalRangedUnits());

        armyTwoName.textProperty().bind(Model.getArmyTwoWrapper().getName());
        tUnits2.textProperty().bind(Model.getArmyTwoWrapper().getTotalUnits());
        cavUnits2.textProperty().bind(Model.getArmyTwoWrapper().getTotalCavalryUnits());
        comUnits2.textProperty().bind(Model.getArmyTwoWrapper().getTotalCommanderUnits());
        infUnits2.textProperty().bind(Model.getArmyTwoWrapper().getTotalInfantryUnits());
        ranUnits2.textProperty().bind(Model.getArmyTwoWrapper().getTotalRangedUnits());

        simulationLogList.setItems(Model.getBattleLog());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        /* init items in terrainBox */
        terrainBox.getItems().add(TerrainType.HILL);
        terrainBox.getItems().add(TerrainType.PLAINS);
        terrainBox.getItems().add(TerrainType.FOREST);

        bindObserverFields();
    }
}
