package com.example.dynamic;

import javafx.application.Application;

import javafx.scene.Scene;

import javafx.stage.Stage;

public class Main extends Application {
    Stage stage;

    public void start(Stage stage) throws Exception {

        this.stage = stage;
        this.stage.setUserData(this); // Make Main accessible globally

        StartScene startScene = new StartScene();
        Scene scene = startScene.createScene(stage);
        stage.setScene(scene);
        stage.setTitle("Minimum Cost Calculator");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    // Method to switch scenes
    public void switchToScene(Scene newScene) {

        stage.setScene(newScene);
    }
}

