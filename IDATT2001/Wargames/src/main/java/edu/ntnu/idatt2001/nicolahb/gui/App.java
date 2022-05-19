package edu.ntnu.idatt2001.nicolahb.gui;

import edu.ntnu.idatt2001.nicolahb.gui.controllers.MainViewController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import static edu.ntnu.idatt2001.nicolahb.gui.models.FloatingWindow.promptErrorMessage;

/**
 * The class App that extends the javafx abstract class Application.
 * Is used for starting and configuring javafx.
 * Is automatically called when starting the program by mvn javafx:run, or called by the Main class
 * if started by calling the main method.
 *
 * @author Nicolai H. Brand.
 * @version 19.05.2022.
 */
public class App extends Application {
    private static final int DEFAULT_WIDTH = 1280;
    private static final int DEFAULT_HEIGHT = 720;
    private static final int MIN_WIDTH = 640;
    private static final int MIN_HEIGHT = 640;
    private static Stage stage;

    /**
     * Called from the launch() method available from the superclass.
     * Configures the initial stage and attempts to load the initial scene.
     * If the initial scene could not be loaded, normal program execution is stopped and
     * an error is printed to the screen.
     *
     * @param startStage Stage, the initial stage given by javafx.
     */
    @Override
    public void start(Stage startStage) {
        stage = startStage;

        /* The name of first fxml scene to be loaded on startup */
        final String startingScene = "main.fxml";
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getClassLoader().getResource(startingScene));
            Scene scene = new Scene(fxmlLoader.load(), DEFAULT_WIDTH, DEFAULT_HEIGHT);
            scene.getStylesheets().add("style.css");

            stage.setTitle("javafx");
            stage.setMinWidth(MIN_WIDTH);
            stage.setMinHeight(MIN_HEIGHT);

            stage.setScene(scene);
            stage.show();

        } catch (IOException | IllegalStateException e) {
            System.out.println("Fatal error occured!\nCould not load initial scene '" + startingScene + "'.");
            System.out.println("Error message: " + e.getMessage() + "\nCaused by " + e.getCause());
            /*
             * Close the JVM as the program could not be started properly.
             * I use the exit code 1 to emphasize that an error occurred.
             * */
            System.exit(1);
        }
    }

    /**
     * Attempts to load a new FXML file as a scene.
     * If the file could not be loaded in, it trows an error in the form of a floating window message
     * to the user.
     *
     * @param fileName String, name of the FXML scene to be loaded without the .fxml file extension.
     */
    public static void changeScene(String fileName) {
        FXMLLoader fxmlLoader = new FXMLLoader(MainViewController.class.getClassLoader().getResource(fileName + ".fxml"));
        try {
            Scene scene = new Scene(fxmlLoader.load(), DEFAULT_WIDTH, DEFAULT_HEIGHT);
            scene.getStylesheets().add("style.css");
            stage.setScene(scene);
        } catch (IOException e) {
            promptErrorMessage("Something went wrong when trying to load " + fileName + ". " + e.getMessage());
        }
    }

    /**
     * The entry point of the application when ran from mvn javafx:run.
     */
    public static void main() {
        launch();
    }

    /* Get method for Stage */
    public static Stage getStage() {
        return stage;
    }
}