package com.example.dynamic;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ScreenManager {
    private Stage stage;

    public ScreenManager(Stage stage) {
        this.stage = stage;
    }

    public void switchTo(Scene scene) {
        stage.setScene(scene);
    }

    public void switchTo(String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
