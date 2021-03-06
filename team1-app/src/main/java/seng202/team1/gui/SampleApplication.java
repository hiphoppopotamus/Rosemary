package seng202.team1.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * starts the JavaFX application for Rosemary
 */
public class SampleApplication extends Application {


    /**
     * Runs automatically to start primary state
     * @param primaryStage is the stage it will call
     * @throws IOException exception for any IO errors
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("foodItemStorage.fxml"));
        // Parent root = FXMLLoader.load(getClass().getResource("order.fxml"));
        primaryStage.setTitle("ROSEMARY | Food Item Storage");
        Scene start = new Scene(root);
        start.getStylesheets().add("stylesheets/default.css");
        primaryStage.setScene(start);
        //primaryStage.setMaximized(true);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
