package com.example.dynamic;

import java.io.IOException;
import java.net.URL;
import java.util.Random;

import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.sound.sampled.*;

/**
 * The SolutionScreen class extends BorderPane and is used to display a solution screen
 * for a dynamic programming problem in a graphical user interface. It visualizes the
 * dynamic programming table along with paths and costs.
 */
public class SolutionScreen {

    City[] cities; // Array of City objects
     String source; // Source city name
     String dest; // Destination city name
    CellInfo[][] dpTable; // 2D array representing the dynamic programming table

    private int start, end; // Indices for source and destination cities within the array

    private Stage stage; // Stage for the application
    private Scene scene; // Scene for navigation




    private final int prefWidth = 50; // Preferred width for cells and labels in the grid
    private final int prefHeight = 10; // Preferred height for cells and labels in the grid

    private int numberOfStages; // Number of stages in the journey



    /**
     * Constructor for the SolutionScreen class.
     * Initializes the UI components and dynamic programming structures based on the provided cities and path.
     * @param stage The primary stage of the application
     * @param cities Array of cities involved in the problem
     * @param source Name of the source city
     * @param dest Name of the destination city
     */
    public Scene createScene(Stage stage, City[] cities, String source, String dest) {

        Main mainApp = (Main) stage.getUserData();   //This allows you to access


        this.stage = stage;
        this.cities = cities;
        this.source = source;
        this.dest = dest;

        initializeSourceAndDest();

        generateDPTable();

        BorderPane border = new BorderPane();

        /* GridPane gridPane = new GridPane();
        gridPane.setHgap(10); // Horizontal gap between grid cells
       gridPane.setVgap(10); // Vertical gap between grid cells

        this.numberOfStages = cities[cities.length - 1].getStage();

        String[] colors = arrayOfColors(this.numberOfStages);

        // Adding column headers
        for (int i = start; i < end + 1; i++) {
            Label header = new Label(cities[i].getName());
            header.setStyle(
                    "-fx-background-color: #222831; -fx-text-fill: #FFD369; -fx-font-size: 14px; -fx-padding: 0.5em; -fx-background-radius: 10px; -fx-border-color: "
                            + colors[cities[i].getStage()] + "; -fx-border-width: 2px; -fx-border-radius: 10px;");
            header.setAlignment(Pos.CENTER);
            header.setPrefWidth(prefWidth);
            header.setPrefHeight(prefHeight);
          gridPane.add(header, i + 1, 0); // +1 because first column is for row headers
        }

        // Adding row headers
        for (int i = start; i < end + 1; i++) {
            Label rowHeader = new Label(cities[i].getName());
            rowHeader.setStyle(
                    "-fx-background-color: #222831; -fx-text-fill: #FFD369; -fx-font-size: 14px; -fx-padding: 0.5em; -fx-background-radius: 10px; -fx-border-color: "
                            + colors[cities[i].getStage()] + "; -fx-border-width: 2px; -fx-border-radius: 10px;");
            rowHeader.setAlignment(Pos.CENTER);
            rowHeader.setPrefWidth(prefWidth);
            rowHeader.setPrefHeight(prefHeight);
           gridPane.add(rowHeader, 0, i + 1);
        }

        // Adding data cells
        for (int i = start; i < end + 1; i++) {
            for (int j = start; j < end + 1; j++) {
                if (dpTable[i][j] != null) {
                    Label cell = new Label(String.valueOf(dpTable[i][j].getOptimalCost()));
                    cell.setCursor(Cursor.HAND);

                    String optimalPath = dpTable[i][j].getOptimalPath().toString();
                    String city_i = cities[i].getName(), city_j = cities[j].getName();
                    CellInfo[] cellInfos = dpTable[i][j].getCellInfos();
                    cell.setOnMouseClicked(event -> {
                        showDetailsStage(
                                "The Optimal Path Between City " + city_i + " and " + city_j + " is: " + optimalPath,
                                cellInfos);
                    });

                    if (i == 0 && j == dpTable[i].length - 1)
                        cell.setStyle(
                                "-fx-background-color: #FFF5E0; -fx-text-fill: #222831; -fx-font-size: 14px; -fx-padding: 0.5em; -fx-background-radius: 10px; -fx-border-color: #41B06E; -fx-border-width: 2px; -fx-border-radius: 10px;");
                    else
                        cell.setStyle(
                                "-fx-background-color: #FFF5E0; -fx-text-fill: #222831; -fx-font-size: 14px; -fx-padding: 0.5em; -fx-background-radius: 10px; -fx-border-color: #1679AB; -fx-border-width: 2px; -fx-border-radius: 10px;");

                    cell.setAlignment(Pos.CENTER);

                    cell.setPrefWidth(prefWidth);
                    cell.setPrefHeight(prefHeight);
                  //  gridPane.add(cell, j + 1, i + 1);

                }
            }
        }
*/
        Label dynamicLabel = new Label("Dynamic Programming Table");
        dynamicLabel.setStyle("-fx-text-fill: #FFF5E0; " + "-fx-padding: 2; " + "-fx-font-size: 22px;");

      /*  VBox leftBox = new VBox(10, dynamicLabel, gridPane);
        leftBox.setAlignment(Pos.CENTER);

        // Wrapping the VBox with a ScrollPane
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(leftBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        scrollPane.setStyle("-fx-background: black; " + "-fx-border-color: gold; " + "-fx-border-width: 2px; "
                + "-fx-border-radius: 10px;");

        scrollPane.setPadding(new Insets(10));
        setLeft(scrollPane);

        setPadding(new Insets(15));
        gridPane.setAlignment(Pos.CENTER);
        setAlignment(gridPane, Pos.CENTER);
*/



        // Text for min cost
        Text minTxt1 = new Text("    Result of calculating\n the minimum cost between");
        minTxt1.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 40));
        minTxt1.setFill(Color.web("#68af3d"));
        minTxt1.setStroke(Color.BLACK);
        minTxt1.setStrokeWidth(2);

        Text minTxt2 = new Text("your start and destination");
        minTxt2.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 40));
        minTxt2.setFill(Color.web("#68af3d"));
        minTxt2.setStroke(Color.BLACK);
        minTxt2.setStrokeWidth(2);


        Label optimalCostSolution = new Label("Optimal cost: "+String.valueOf(dpTable[start][end].getOptimalCost()));
        optimalCostSolution.setStyle("-fx-text-fill: #465e43; -fx-font-size: 16px; -fx-font-style: italic;");


        optimalCostSolution.setAlignment(Pos.CENTER);

        HBox optimalSolBox = new HBox(10);
        optimalSolBox.setPadding(new Insets(10));
        optimalSolBox.setAlignment(Pos.CENTER_LEFT);
        optimalSolBox.setStyle("-fx-background-color: rgba(250,231,169,0.53); -fx-background-radius: 10px;");

        ImageView optimalIcon = new ImageView(new Image(getClass().getResourceAsStream("/optimalIcon.png")));
        optimalIcon.setFitWidth(24);
        optimalIcon.setFitHeight(24);


        // Adjust the size to match the content
        optimalSolBox.setMaxWidth(Region.USE_PREF_SIZE);
        optimalSolBox.setMaxHeight(Region.USE_PREF_SIZE);


        optimalSolBox.getChildren().addAll(optimalIcon, optimalCostSolution);
        optimalSolBox.setAlignment(Pos.CENTER);





        Label optimalPathLabel = new Label("Optimal Path Between start city and end city: " +dpTable[start][end].getOptimalPath());
        optimalPathLabel.setFont(Font.font("Comic Sans MS"));
        optimalPathLabel.setStyle("-fx-text-fill: #465e43; " + "-fx-padding: 2; " + "-fx-font-size: 16px;");
        optimalPathLabel.setAlignment(Pos.CENTER);

        HBox optPathBox = new HBox(10);
        optPathBox.setPadding(new Insets(10));
        optPathBox.setAlignment(Pos.CENTER_LEFT);
        optPathBox.setStyle("-fx-background-color: rgba(250,231,169,0.53); -fx-background-radius: 10px;");

        ImageView optimalPathIcon = new ImageView(new Image(getClass().getResourceAsStream("/optPathIcon.png")));
        optimalPathIcon.setFitWidth(24);
        optimalPathIcon.setFitHeight(24);


        // Adjust the size to match the content
        optPathBox.setMaxWidth(Region.USE_PREF_SIZE);
        optPathBox.setMaxHeight(Region.USE_PREF_SIZE);


        optPathBox.getChildren().addAll(optimalPathIcon, optimalPathLabel);
        optPathBox.setAlignment(Pos.CENTER);




        CellInfo[] solutionInfo = dpTable[start][end].getCellInfos();


        // Buttons
        Button altBtn = createStyledButton("Load alternative paths");
        Button backBtn = createStyledButton("Back");
        Button tableBtn = createStyledButton("Show DP table");

        altBtn.setMinWidth(Region.USE_COMPUTED_SIZE);
        altBtn.setPrefWidth(Region.USE_COMPUTED_SIZE);
        altBtn.setMaxWidth(Region.USE_COMPUTED_SIZE);

        backBtn.setMinWidth(Region.USE_COMPUTED_SIZE);
        backBtn.setPrefWidth(Region.USE_COMPUTED_SIZE);
        backBtn.setMaxWidth(Region.USE_COMPUTED_SIZE);

        tableBtn.setMinWidth(Region.USE_COMPUTED_SIZE);
        tableBtn.setPrefWidth(Region.USE_COMPUTED_SIZE);
        tableBtn.setMaxWidth(Region.USE_COMPUTED_SIZE);

        altBtn.setPadding(new Insets(5, 10, 5, 10)); // top, right, bottom, left
        backBtn.setPadding(new Insets(5, 10, 5, 10)); // top, right, bottom, left
        tableBtn.setPadding(new Insets(5, 10, 5, 10)); // top, right, bottom, left

        VBox buttonsBox = new VBox(20);
        buttonsBox.getChildren().addAll(altBtn,tableBtn,backBtn);
        buttonsBox.setAlignment(Pos.CENTER);
        // Buttons Actions

        altBtn.setOnAction(e -> {
            showDetailsStage(solutionInfo);
        });


        backBtn.setOnAction(e -> {
        playSound("/clickSound.wav");
            StartScene startScene = new StartScene();
            Scene scene = startScene.createScene(stage); // Create the scene
            mainApp.switchToScene(scene);  // Switch to the new scene using Main's method
        });

        tableBtn.setOnAction(e ->{

        });



        VBox contentBox = new VBox(10, minTxt1,minTxt2, optimalSolBox, optPathBox,
               buttonsBox);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setPadding(new Insets(25));



        border.setCenter(contentBox);
        border.setMargin(contentBox, new Insets(0, 150, 0, 0));


        // Set background for the border
        Image backgroundImage = new Image("/solBk.gif");
        BackgroundImage bgImage = new BackgroundImage(
                backgroundImage,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT,
                new BackgroundSize(100, 100, true, true, false, true)
        );
        border.setBackground(new Background(bgImage));



        return new Scene(border, 800, 600);


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

    public Text createStyledText(String textContent) {
        Text text = new Text(textContent);
        text.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 40));
        text.setFill(Color.web("#68af3d"));
        text.setStroke(Color.BLACK);
        text.setStrokeWidth(2);
        return text;
    }

    /**
     * Initializes the source and destination indices based on the cities array.
     */
    private void initializeSourceAndDest() {
        for (int i = 0; i < cities.length; i++) {
            if (cities[i].getName().equals(source)) {
                start = i;
            }
        }

        for (int i = 0; i < cities.length; i++) {
            if (cities[i].getName().equals(dest)) {
                end = i;
            }
        }

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

    /**
     * Generates the dynamic programming table for the problem using the cities' information.
     */
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

    /**
     * Determines and sets the optimal cost and path for a particular cell in the dynamic programming table.
     * @param i Source city index
     * @param j Destination city index
     * @return CellInfo object with the minimal cost and path
     */
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

    /**
     * Creates and shows a new stage displaying the details for a specific cell, including alternative solutions.
     * @param message The message to display in the stage
     * @param cellInfos Array of alternative CellInfo objects for additional details
     */
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
        layout.getStylesheets().add("theme.css");
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

        Scene scene = new Scene(layout, 600, 500);

        // Configure and show the stage
        detailStage.setTitle("Path Details");
        detailStage.setScene(scene);
        detailStage.show();
    }

    /**
     * Similar to the above method but handles showing alternatives without a specific message.
     * @param cellInfos Array of CellInfo objects containing alternative solutions
     */
    private void showDetailsStage(CellInfo[] cellInfos) {
        // Create a new stage
        Stage detailStage = new Stage();

        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);
        layout.getStylesheets().add("theme.css");

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

        Scene scene = new Scene(layout, 600, 500);

        // Configure and show the stage
        detailStage.setTitle("Path Details");
        detailStage.setScene(scene);
        detailStage.show();
    }

    /**
     * Generates an array of random color codes.
     * @param size The number of color codes to generate
     * @return An array of hex color codes
     */
    private String[] arrayOfColors(int size) {
        Random rand = new Random();
        String[] colorCodes = new String[size + 1]; // Array to hold color codes

        for (int i = 0; i < colorCodes.length; i++) {
            // Generate random colors
            float r = rand.nextFloat();
            float g = rand.nextFloat();
            float b = rand.nextFloat();

            // Create a color object
            Color color = new Color(r, g, b, 1.0);

            // Convert color to hexadecimal (stripping alpha value)
            colorCodes[i] = String.format("#%02X%02X%02X", (int) (color.getRed() * 255), (int) (color.getGreen() * 255),
                    (int) (color.getBlue() * 255));
        }

        return colorCodes;
    }

}