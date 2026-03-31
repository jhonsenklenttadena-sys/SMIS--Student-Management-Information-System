package com.smis;

import com.smis.database.DBInitializer;
import com.smis.database.DatabaseConnection;
import com.smis.views.common.LoginView;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

/**
 * SMIS — Student Management Information System
 * Uses SQLite — no external database installation required.
 * Database file: smis.db (auto-created on first run)
 * Default login: admin / admin123
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {

        // Initialize database and create tables if first run
        if (!DatabaseConnection.testConnection()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("Cannot create SQLite database.");
            alert.setContentText("Make sure the app has write permission in its folder.");
            alert.showAndWait();
            Platform.exit();
            return;
        }

        // Create all tables + default admin on first run
        DBInitializer.initialize();

        primaryStage.setTitle("SMIS — Student Management Information System");
        primaryStage.setResizable(true);
        primaryStage.setOnCloseRequest(e -> {
            DatabaseConnection.closeConnection();
            Platform.exit();
        });

        primaryStage.setScene(new LoginView(primaryStage).getScene());
        primaryStage.setWidth(900);
        primaryStage.setHeight(600);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
