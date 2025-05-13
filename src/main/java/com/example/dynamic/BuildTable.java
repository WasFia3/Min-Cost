package com.example.dynamic;

import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;

public class BuildTable {
    City[] cities; // Array of City objects
    String source; // Source city name
    String dest; // Destination city name
    CellInfo[][] dpTable; // 2D array representing the dynamic programming table

    private int start, end; // Indices for source and destination cities within the array

    private Stage stage; // Stage for the application

    private int numberOfStages; // Number of stages in the journey

    public Scene createScene(Stage stage, City[] cities, String source, String dest, CellInfo[][] dpTable, int start, int end) {

        Main mainApp = (Main) stage.getUserData();

        BorderPane border = new BorderPane();

        this.stage = stage;
        this.cities = cities;
        this.source = source;
        this.dest = dest;
        this.dpTable = dpTable;
        this.start = start;
        this.end = end;

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        this.numberOfStages = cities[cities.length - 1].getStage();

        generateDPTable(); // Generates data

        // Adding column headers
        for (int i = start; i <= end; i++) {
            Label header = new Label(cities[i].getName());
            header.setAlignment(Pos.CENTER);
            header.setPrefWidth(40);
            header.setPrefHeight(5);
            gridPane.add(header, i + 1, 0);
        }

        // Adding row headers
        for (int i = start; i <= end; i++) {
            Label rowHeader = new Label(cities[i].getName());
            rowHeader.setAlignment(Pos.CENTER);
            rowHeader.setPrefWidth(40 * 1.5);  // Increase width
            rowHeader.setPrefHeight(5 * 1.5);  // Increase height
            rowHeader.setStyle(
                    "-fx-background-color: rgba(250,231,169,0.53); " +
                            "-fx-text-fill: #465e43; " +
                            "-fx-font-family: 'Comic Sans MS'; " +
                            "-fx-font-size: 16px; " +
                            "-fx-padding: 0.5em; " +
                            "-fx-background-radius: 10px; " +
                            "-fx-border-color: #c9bc8e; " +
                            "-fx-border-width: 2px; " +
                            "-fx-border-radius: 10px;"
            );
            gridPane.add(rowHeader, 0, i + 1);
        }

        // Adding data cells
        for (int i = start; i <= end; i++) {
            for (int j = start; j <= end; j++) {
                if (dpTable[i][j] != null) {
                    Label cell = new Label(String.valueOf(dpTable[i][j].getOptimalCost()));
                    String optimalPath = dpTable[i][j].getOptimalPath().toString();
                    String city_i = cities[i].getName(), city_j = cities[j].getName();
                    CellInfo[] cellInfos = dpTable[i][j].getCellInfos();

                    cell.setOnMouseClicked(event -> {
                        showDetailsStage(
                                "The Optimal Path Between City " + city_i + " and " + city_j + " is: " + optimalPath,
                                cellInfos
                        );
                    });

                    cell.setAlignment(Pos.CENTER);
                    cell.setPrefWidth(50 * 1.5);  // Increase width
                    cell.setPrefHeight(5 * 1.5);  // Increase height
                    cell.setStyle(
                            "-fx-background-color: rgba(250,231,169,0.53); " +
                                    "-fx-text-fill: #465e43; " +
                                    "-fx-font-family: 'Comic Sans MS'; " +
                                    "-fx-font-size: 16px; " +
                                    "-fx-padding: 0.5em; " +
                                    "-fx-background-radius: 10px; " +
                                    "-fx-border-color: #a9a26f; " +
                                    "-fx-border-width: 2px; " +
                                    "-fx-border-radius: 10px;"
                    );

                    gridPane.add(cell, j + 1, i + 1);
                }
            }
        }

        // Label above the table
        Label dynamicLabel = new Label("Dynamic Programming Table");
        dynamicLabel.setStyle(
                "-fx-text-fill: #465e43; " +
                        "-fx-font-family: 'Comic Sans MS'; " +
                        "-fx-font-size: 22px; " +
                        "-fx-padding: 2;"
        );

        // Adding frog image at the start or end
        Image frogImage = new Image(getClass().getResourceAsStream("/dpTableIcon.png"));
        ImageView frogImageView = new ImageView(frogImage);
        frogImageView.setFitWidth(100);  // Set width
        frogImageView.setFitHeight(100); // Set height
        frogImageView.setPreserveRatio(true); // Maintain aspect ratio

        // Creating back button
        Button backButton = createStyledButton("Back");

        backButton.setOnAction(e -> {
            playSound("/clickSound.wav");
            CalculationScreen calculationScreen = new CalculationScreen();
            Scene scene = calculationScreen.createScene(stage, cities, source, dest); // Create the scene
            mainApp.switchToScene(scene);  // Switch to the new scene using Main's method
        });

        // Adding the back button at the bottom of VBox (below the table and frog image)
        VBox leftBox = new VBox(10, frogImageView, dynamicLabel, gridPane, backButton);
        leftBox.setAlignment(Pos.CENTER);

        // ScrollPane with new content (VBox)
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(leftBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        // Adding green gradient to the background
        scrollPane.setStyle(
                "-fx-background: transparent; " +
                        "-fx-border-color: #c9bc8e; " +
                        "-fx-border-width: 2px; " +
                        "-fx-border-radius: 10px;" +
                        "-fx-background-color: linear-gradient(to bottom, #b0e57c, #8ccf5c);" // Green gradient
        );
        scrollPane.setPadding(new Insets(10));

        // Adding ScrollPane to the BorderPane
        border.setLeft(scrollPane);
        border.setPadding(new Insets(15));
        gridPane.setAlignment(Pos.CENTER);
        border.setAlignment(gridPane, Pos.CENTER);

        // Set background for the border
        Image backgroundImage = new Image("/babyBlueBk.jpg");
        BackgroundImage bgImage = new BackgroundImage(
                backgroundImage,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT,
                new BackgroundSize(100, 100, true, true, false, true)
        );
        border.setBackground(new Background(bgImage));

        Scene scene = new Scene(border);
        return scene;
    }


    private void showDetailsStage(String message, CellInfo[] cellInfos) {
        // Create a new stage
        Stage detailStage = new Stage();

        // Set the content of the stage
        Label messageLabel = new Label(message);
        messageLabel.setStyle(
                "-fx-background-color: #FFF5E0; -fx-text-fill: #222831; -fx-font-size: 16px; -fx-padding: 0.5em; -fx-background-radius: 10px; -fx-border-color: #41B06E; -fx-border-width: 2px; -fx-border-radius: 10px;");

        messageLabel.setWrapText(true);
        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().add(messageLabel);

        if (cellInfos != null) {
            Label alternativeLabel = new Label("Alternative Solutions");
            alternativeLabel.setStyle("-fx-text-fill: #FFF5E0; " + "-fx-padding: 2; " + "-fx-font-size: 16px;");
            alternativeLabel.setAlignment(Pos.CENTER);

            layout.getChildren().add(alternativeLabel);

            for (int i = 0; i < cellInfos.length; i++)
                if (cellInfos[i] != null) {
                    Label alternative = new Label("Alternative Solution : " + cellInfos[i].getOptimalPath()
                            + " With Cost " + cellInfos[i].getOptimalCost());
                    alternative.setStyle(
                            "-fx-background-color: #FFF5E0; -fx-text-fill: #222831; -fx-font-size: 16px; -fx-padding: 0.5em; -fx-background-radius: 10px; -fx-border-color: #41B06E; -fx-border-width: 2px; -fx-border-radius: 10px;");

                    alternative.setAlignment(Pos.CENTER);

                    layout.getChildren().add(alternative);

                }
        }

    }


    private void generateDPTable() {
        dpTable = new CellInfo[cities.length][cities.length];

        for (int i = 0; i < cities.length; i++) {
            int[] petrolCost = cities[i].getPetrolCost();
            int stage = cities[i].getStage();

            int count = 0;
            for (int j = 0; j < cities.length; j++)
                if (cities[j].getStage() == stage - 1) {
                    count = j;
                    break;
                }

            for (int j = 0; j < petrolCost.length; j++) {
                CellInfo cellInfo = new CellInfo(cities[i].getHotelCost() + petrolCost[j],
                        cities[j + count].getName() + " -> " + cities[i].getName());
                dpTable[j + count][i] = cellInfo;

            }
        }

        for (int i = 0; i < dpTable.length; i++)
            for (int j = 0; j < dpTable.length; j++)
                if (cities[j].getStage() - cities[i].getStage() > 1)
                    dpTable[i][j] = setCost(i, j);

    }

    private Button createStyledButton(String labelText) {
        Button button = new Button(labelText);
        button.setStyle("-fx-background-color: #68af3d; -fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold; -fx-background-radius: 15px; -fx-padding: 10px 20px;");
        button.setPrefWidth(200);

        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), button);
        scaleTransition.setToX(1.2);
        scaleTransition.setToY(1.2);

        ScaleTransition resetScaleTransition = new ScaleTransition(Duration.millis(200), button);
        resetScaleTransition.setToX(1.0);
        resetScaleTransition.setToY(1.0);

        DropShadow hoverGlow = new DropShadow();
        hoverGlow.setColor(Color.LIGHTBLUE);
        hoverGlow.setRadius(15);
        hoverGlow.setSpread(0.5);

        button.setOnMouseEntered(e -> {
            scaleTransition.playFromStart();
            button.setEffect(hoverGlow);
        });

        button.setOnMouseExited(e -> {
            resetScaleTransition.playFromStart();
            button.setEffect(null);
        });

        return button;
    }

    public void playSound(String fileName) {
        try {
            URL soundURL = getClass().getResource(fileName);
            if (soundURL == null) {
                System.out.println("no such sound file exists " + fileName);
                return;
            }
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundURL);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    private CellInfo setCost(int i, int j) {
        int stage = cities[j].getStage() - 1;
        int min = Integer.MAX_VALUE;

        int optimal_i = 0, optimal_k = 0;

        CellInfo[] cellInfos = new CellInfo[3];
        byte cellInfoCounter = 0;
        for (int k = i + 1; k < j; k++) {
            if (dpTable[i][k] != null && dpTable[k][j] != null) {

                int cost = dpTable[i][k].getOptimalCost() + dpTable[k][j].getOptimalCost();

                if (cellInfoCounter < 3) {
                    CellInfo cellInfo = new CellInfo(cost,
                            dpTable[i][k].getOptimalPath() + " -> " + cities[j].getName());
                    cellInfos[cellInfoCounter++] = cellInfo;
                }

                if (cities[k].getStage() == stage && cost < min) {
                    min = cost;
                    optimal_i = i;
                    optimal_k = k;
                }
            }
        }

        CellInfo cellInfo = new CellInfo(min,
                dpTable[optimal_i][optimal_k].getOptimalPath() + " -> " + cities[j].getName());

        cellInfo.setCellInfos(cellInfos);
        return cellInfo;

    }
}
