package edu.ntnu.idatt2001.nicolahb.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * The class App that extends the javafx abstract class Application.
 * Is used for starting and configuring javafx.
 *
 * @author Nicolai H. Brand
 * @version 18.04.2022
 */
public class App extends Application {
    private static final int DEFAULT_WIDTH = 1280;
    private static final int DEFAULT_HEIGHT = 720;
    private static final int MIN_WIDTH = 640;
    private static final int MIN_HEIGHT = 640;
    /* The name of first fxml scene to be loaded on startup */
    private static String startingScene = "main.fxml";
    private static Stage stage;

    /**
     * Called from the launch() method available from the superclass.
     * Configures the initial stage and attempts to load the initial scene.
     * If the initial scene could not be loaded, normal program excecution is stopped and
     * an error is printed to the screen.
     *
     * @param startStage Stage, the initial stage given by javafx.
     */
    @Override
    public void start(Stage startStage) {
        stage = startStage;

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
            System.out.println(e);
            /*
             * Close the JVM as the program could not be started properly.
             * I use the exit code 1 to emphasize that an error occurred.
             * */
            System.exit(1);
        }
    }

    /**
     * The entry point of application.
     *
     */
    public static void main() {
        launch();
    }

    public static Stage getStage() {
        return stage;
    }
}
