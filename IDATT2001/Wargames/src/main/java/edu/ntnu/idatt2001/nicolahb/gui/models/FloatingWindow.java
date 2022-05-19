package edu.ntnu.idatt2001.nicolahb.gui.models;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Optional;

/**
 * The class ShowFloatingWindow.
 * Has static methods that open floating windows/dialogs for the user to interactive with.
 *
 * @author Nicolai H. Brand.
 * @version 19.05.2022.
 */
public class FloatingWindow {

    /**
     * Opens an alert window and displays an error message to the user.
     * @param message String, the error message to be shown
     */
    public static void promptErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("An error occurred");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Open a new floating window with a message to the user.
     *
     * @param message String, the message to be shown
     */
    public static void promptInformation(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Shows a floating window with two buttons for the user to press.
     *
     * @param message String, the message to be shown.
     * @param buttonOneText String, the text on the first button.
     * @param buttonTwoText String, the text on the second button.
     * @return boolean, true if the first button was pressed else false.
     */
    public static boolean giveBinaryChoice(String title, String message, String buttonOneText, String buttonTwoText) {
        ButtonType firstBtn = new ButtonType(buttonOneText, ButtonBar.ButtonData.APPLY);
        ButtonType secondBtn = new ButtonType(buttonTwoText, ButtonBar.ButtonData.APPLY);
        Alert alert = new Alert(Alert.AlertType.NONE, message, firstBtn, secondBtn);
        alert.setTitle(title);
        Optional<ButtonType> result = alert.showAndWait();

        return result.get().equals(firstBtn);
    }

    /**
     * Opens a window for the user to select a file.
     * @param stage Stage, the stage to open the dialog from
     *
     * @return selectedFile String, the absolute path of the file chosen
     * @throws FileNotFoundException, if file could not be found
     */
    public static String selectFile(Stage stage) throws FileNotFoundException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open army from file");
        File selectedFile = fileChooser.showOpenDialog(stage);

        /* check if a file selected, and if selected file has read permissions */
        if (selectedFile != null && selectedFile.canRead())
            return selectedFile.getAbsolutePath();
        else
            throw new FileNotFoundException("Selected file could not be read, or does not have read permissions");
    }

    /**
     * Opens a window for the user to select a place to save a file.
     * @param stage Stage, the stage to open the dialog from
     *
     * @return selectedPlace, the absolute path of the place to save the file
     * @throws FileNotFoundException, if placement is not selected or does not have write permissions
     */
    public static String saveFile(Stage stage, String initialName) throws FileNotFoundException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save army to file");
        fileChooser.setInitialFileName(initialName);
        File selectedPlace = fileChooser.showSaveDialog(stage);

        /* check if a user selected a placement, and if selected place has write permissions */
        if (selectedPlace != null)// && selectedPlace.canWrite())
            return selectedPlace.getAbsolutePath();
        else
            throw new FileNotFoundException("Could not save to selected place");
    }
}
