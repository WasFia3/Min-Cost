package com.example.dynamic;

import javafx.animation.ScaleTransition;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
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
//
import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;

public class BuildTable {
    City[] cities; // Array of City objects
    String source; // Source city name
    String dest; // Destination city name
    NodeSolution[][] dpTable; // 2D array representing the dynamic programming table

    private int start, end; // Indices for source and destination cities within the array

    private Stage stage; // Stage for the application

    private int numberOfStages; // Number of stages in the journey

    public Scene createScene(Stage stage, City[] cities, String source, String dest, NodeSolution[][] dpTable, int start, int end) {

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
            header.setMinWidth(Region.USE_PREF_SIZE); // Show the full city name instead of "..."
            header.setMaxWidth(Double.MAX_VALUE);
            header.setStyle(
                    "-fx-text-fill: #465e43; " +
                            "-fx-font-family: 'Nunito ExtraBold'; " +
                            "-fx-font-size: 15px; " +
                            "-fx-padding: 0 0.3em;"
            );
            GridPane.setHalignment(header, HPos.CENTER);
            gridPane.add(header, i + 1, 0);
        }

        // Adding row headers
        for (int i = start; i <= end; i++) {
            Label rowHeader = new Label(cities[i].getName());
            rowHeader.setAlignment(Pos.CENTER);
            rowHeader.setMinWidth(Region.USE_PREF_SIZE); // Show the full city name instead of "..."
            rowHeader.setMaxWidth(Double.MAX_VALUE);
            rowHeader.setStyle(
                    "-fx-background-color: rgba(250,231,169,0.53); " +
                            "-fx-text-fill: #465e43; " +
                            "-fx-font-family: 'Nunito Bold'; " +
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
                    final int finalI = i;
                    final int finalJ = j;
                    String city_i = cities[finalI].getName();
                    String city_j = cities[finalJ].getName();
                    NodeSolution[] nodeSolutions = dpTable[finalI][finalJ].getCellInfos();

                    cell.setOnMouseClicked(event -> {
                        playSound("/clickSound.wav");
                        showDetailsStage(
                                "Optimal path from " + city_i + " to " + city_j + ":\n" + optimalPath.replace("->", "\u2192")
                                        + "\nCost: " + dpTable[finalI][finalJ].getOptimalCost(),
                                optimalPath,
                                nodeSolutions
                        );
                    });


                    cell.setAlignment(Pos.CENTER);
                    cell.setMinWidth(70);
                    cell.setMaxWidth(Double.MAX_VALUE);

                    String cellStyle =
                            "-fx-background-color: rgba(250,231,169,0.53); " +
                                    "-fx-text-fill: #465e43; " +
                                    "-fx-font-family: 'Nunito Bold'; " +
                                    "-fx-font-size: 16px; " +
                                    "-fx-padding: 0.5em; " +
                                    "-fx-background-radius: 10px; " +
                                    "-fx-border-color: #a9a26f; " +
                                    "-fx-border-width: 2px; " +
                                    "-fx-border-radius: 10px;";

                    // The final answer (start -> end) is highlighted
                    if (i == start && j == end)
                        cellStyle += "-fx-background-color: #fae7a9; -fx-border-color: #68af3d; -fx-font-family: 'Nunito ExtraBold';";

                    String normalStyle = cellStyle;
                    String hoverStyle = cellStyle + "-fx-background-color: #fff5d6; -fx-border-color: #68af3d;";

                    cell.setStyle(normalStyle);
                    cell.setCursor(Cursor.HAND); // Shows the cell can be clicked
                    cell.setOnMouseEntered(e -> cell.setStyle(hoverStyle));
                    cell.setOnMouseExited(e -> cell.setStyle(normalStyle));

                    gridPane.add(cell, j + 1, i + 1);
                }
            }
        }

        // Label above the table
        Label dynamicLabel = new Label("Dynamic Programming Table");
        dynamicLabel.setStyle(
                "-fx-text-fill: #3f7d24; " +
                        "-fx-font-family: 'Fredoka SemiBold'; " +
                        "-fx-font-size: 26px; " +
                        "-fx-padding: 2;"
        );

        // Small hint under the title
        Label hintLabel = new Label("Click any cell to see its path and alternatives");
        hintLabel.setStyle(
                "-fx-text-fill: #465e43; " +
                        "-fx-font-family: 'Nunito SemiBold'; " +
                        "-fx-font-size: 14px; " +
                        "-fx-opacity: 0.8;"
        );

        // Adding frog image at the start or end
        Image frogImage = new Image(getClass().getResourceAsStream("/dpTableIcon.png"));
        ImageView frogImageView = new ImageView(frogImage);
        frogImageView.setFitWidth(100);  // Set width
        frogImageView.setFitHeight(100); // Set height
        frogImageView.setPreserveRatio(true); // Maintain aspect ratio

        // Creating back button
        Button backButton = createStyledButton("Back", true);

        backButton.setOnAction(e -> {
            playSound("/clickSound.wav");
            CalculationScreen calculationScreen = new CalculationScreen();
            Scene scene = calculationScreen.createScene(stage, cities, source, dest); // Create the scene
            mainApp.switchToScene(scene);  // Switch to the new scene using Main's method
        });

        // Frog image next to the title and hint
        frogImageView.setFitWidth(80);
        frogImageView.setFitHeight(80);
        VBox titleBox = new VBox(2, dynamicLabel, hintLabel);
        titleBox.setAlignment(Pos.CENTER_LEFT);
        HBox headerBox = new HBox(15, frogImageView, titleBox);
        headerBox.setAlignment(Pos.CENTER);

        // ScrollPane with the table only, so the title and the back button are always visible
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(gridPane);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        // Size the scroll area to the table, leaving room for the scroll bar
        // (when the table is taller than the window, it scrolls inside the panel)
        scrollPane.prefViewportWidthProperty().bind(gridPane.widthProperty());
        scrollPane.prefViewportHeightProperty().bind(gridPane.heightProperty());
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-background-insets: 0; -fx-padding: 0;");
        gridPane.setPadding(new Insets(5));

        // Adding the back button at the bottom of VBox (below the table and frog image)
        VBox leftBox = new VBox(15, headerBox, scrollPane, backButton);
        leftBox.setAlignment(Pos.CENTER);
        leftBox.setPadding(new Insets(15, 25, 20, 25));

        // Adding green gradient to the background
        leftBox.setStyle(
                "-fx-border-color: #c9bc8e; " +
                        "-fx-border-width: 2px; " +
                        "-fx-border-radius: 10px;" +
                        "-fx-background-radius: 10px;" +
                        "-fx-background-color: linear-gradient(to bottom, #b0e57c, #8ccf5c);" // Green gradient
        );

        // Adding the panel to the center of the BorderPane (sized to the table, not the whole window)
        leftBox.setMaxWidth(Region.USE_PREF_SIZE);
        leftBox.setMaxHeight(Region.USE_PREF_SIZE);
        border.setCenter(leftBox);
        BorderPane.setAlignment(leftBox, Pos.CENTER);
        border.setPadding(new Insets(15));
        gridPane.setAlignment(Pos.CENTER);

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


    private void showDetailsStage(String message, String optimalPath, NodeSolution[] nodeSolutions) {
        // Create a new stage
        Stage detailStage = new Stage();
        detailStage.initOwner(stage); // Stays on top of the app and closes with it
        detailStage.getIcons().addAll(stage.getIcons());

        String boxStyle = "-fx-background-color: rgba(255,255,255,0.92); -fx-text-fill: #2f4a28; -fx-font-family: 'Nunito SemiBold'; -fx-font-size: 16px; -fx-padding: 0.6em 0.8em; -fx-background-radius: 10px; -fx-border-color: rgba(63,125,36,0.3); -fx-border-width: 1.5px; -fx-border-radius: 10px;";

        // Set the content of the stage
        Label messageLabel = new Label(message);
        messageLabel.setStyle(boxStyle + "-fx-font-family: 'Nunito ExtraBold';");

        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(Double.MAX_VALUE);
        VBox layout = new VBox(10);
        layout.setAlignment(Pos.TOP_LEFT);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: linear-gradient(to bottom, #b0e57c, #8ccf5c);"); // Same green as the table panel
        layout.getChildren().add(messageLabel);

        if (nodeSolutions != null) {
            Label alternativeLabel = new Label("Alternative Solutions");
            alternativeLabel.setStyle("-fx-text-fill: #465e43; " + "-fx-font-family: 'Fredoka SemiBold'; " + "-fx-padding: 8 0 0 2; " + "-fx-font-size: 16px;");
            alternativeLabel.setAlignment(Pos.CENTER);

            layout.getChildren().add(alternativeLabel);

            int alternativesCount = 0;
            for (int i = 0; i < nodeSolutions.length; i++)
                // Skip the optimal path itself, it's already shown above
                if (nodeSolutions[i] != null && !nodeSolutions[i].getOptimalPath().equals(optimalPath)) {
                    alternativesCount++;
                    Label alternative = new Label(nodeSolutions[i].getOptimalPath().replace("->", "\u2192")
                            + "\nCost: " + nodeSolutions[i].getOptimalCost());
                    alternative.setStyle(boxStyle);
                    alternative.setWrapText(true);
                    alternative.setMaxWidth(Double.MAX_VALUE);

                    layout.getChildren().add(alternative);

                }

            if (alternativesCount == 0)
                alternativeLabel.setText("No other paths between these two cities");
        }

        Scene scene = new Scene(layout, 480, Region.USE_COMPUTED_SIZE);
        detailStage.setScene(scene);
        detailStage.setTitle("Optimal Path Details");
        detailStage.show();  // VERY IMPORTANT


    }


    private void generateDPTable() {
        dpTable = new NodeSolution[cities.length][cities.length];

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
                NodeSolution nodeSolution = new NodeSolution(cities[i].getHotelCost() + petrolCost[j],
                        cities[j + count].getName() + " -> " + cities[i].getName());
                dpTable[j + count][i] = nodeSolution;

            }
        }

        for (int i = 0; i < dpTable.length; i++)
            for (int j = 0; j < dpTable.length; j++)
                if (cities[j].getStage() - cities[i].getStage() > 1)
                    dpTable[i][j] = setCost(i, j);

    }

    private Button createStyledButton(String labelText) {
        return createStyledButton(labelText, false);
    }

    // Primary buttons are solid green, secondary ones (like Back) are white with a green border
    private Button createStyledButton(String labelText, boolean secondary) {
        Button button = new Button(labelText);

        String baseStyle = "-fx-font-family: 'Nunito ExtraBold'; -fx-font-size: 17px; -fx-padding: 11px 24px; " +
                "-fx-background-radius: 12px; -fx-border-radius: 12px; -fx-border-width: 2px; ";
        String normalStyle = secondary
                ? "-fx-background-color: rgba(255,255,255,0.92); -fx-text-fill: #3f7d24; -fx-border-color: #5a9e35;"
                : "-fx-background-color: #5a9e35; -fx-text-fill: white; -fx-border-color: #5a9e35;";
        String hoverStyle = secondary
                ? "-fx-background-color: #eef7e8; -fx-text-fill: #3f7d24; -fx-border-color: #4c8a2c;"
                : "-fx-background-color: #4c8a2c; -fx-text-fill: white; -fx-border-color: #4c8a2c;";

        button.setStyle(baseStyle + normalStyle);
        button.setPrefWidth(220);
        button.setCursor(Cursor.HAND); // Hand cursor on hover

        // Soft shadow under the button
        DropShadow shadow = new DropShadow(8, 0, 3, Color.rgb(47, 93, 30, 0.3));
        button.setEffect(shadow);

        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(150), button);
        scaleTransition.setToX(1.04);
        scaleTransition.setToY(1.04);

        ScaleTransition resetScaleTransition = new ScaleTransition(Duration.millis(150), button);
        resetScaleTransition.setToX(1.0);
        resetScaleTransition.setToY(1.0);

        button.setOnMouseEntered(e -> {
            scaleTransition.playFromStart();
            button.setStyle(baseStyle + hoverStyle);
        });

        button.setOnMouseExited(e -> {
            resetScaleTransition.playFromStart();
            button.setStyle(baseStyle + normalStyle);
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
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException | IllegalArgumentException e) {
            // IllegalArgumentException: no audio device available, just skip the sound
            e.printStackTrace();
        }
    }

    private NodeSolution setCost(int i, int j) {
        int stage = cities[j].getStage() - 1;
        int min = Integer.MAX_VALUE;

        int optimal_i = 0, optimal_k = 0;

        NodeSolution[] nodeSolutions = new NodeSolution[3];
        byte cellInfoCounter = 0;
        for (int k = i + 1; k < j; k++) {
            if (dpTable[i][k] != null && dpTable[k][j] != null) {

                int cost = dpTable[i][k].getOptimalCost() + dpTable[k][j].getOptimalCost();

                if (cellInfoCounter < 3) {
                    NodeSolution nodeSolution = new NodeSolution(cost,
                            dpTable[i][k].getOptimalPath() + " -> " + cities[j].getName());
                    nodeSolutions[cellInfoCounter++] = nodeSolution;
                }

                if (cities[k].getStage() == stage && cost < min) {
                    min = cost;
                    optimal_i = i;
                    optimal_k = k;
                }
            }
        }

        NodeSolution nodeSolution = new NodeSolution(min,
                dpTable[optimal_i][optimal_k].getOptimalPath() + " -> " + cities[j].getName());

        nodeSolution.setCellInfos(nodeSolutions);
        return nodeSolution;

    }
}
