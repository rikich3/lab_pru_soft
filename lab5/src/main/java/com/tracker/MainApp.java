package com.tracker;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/main.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            primaryStage.setTitle("Habitflow - Seguimiento de Hábitos Premium");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(1000);
            primaryStage.setMinHeight(700);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error cargando la vista principal: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
