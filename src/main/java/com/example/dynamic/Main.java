package com.example.dynamic;

import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.sound.sampled.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

public class Main extends Application {

    File file;
    City[] citiesList;
    String source;
    String dest;
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

