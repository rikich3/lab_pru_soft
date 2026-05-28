package com.tracker;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/main.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);

            // ── Cargar stylesheet explícitamente en la Scene ──────────────
            // Esto garantiza que TODOS los nodos (incluyendo controles nativos
            // como Spinner, DatePicker, CheckBox) hereden el tema correcto.
            // Sin esta línea, JavaFX aplica Modena por defecto y los styleClass
            // del FXML se resuelven en vacío para los sub-nodos de controles.
            String css = getClass().getResource("/views/styles.css").toExternalForm();
            scene.getStylesheets().add(css);

            primaryStage.setTitle("Habitflow - Seguimiento de Hábitos");
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
