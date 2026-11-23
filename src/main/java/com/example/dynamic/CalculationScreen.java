package com.example.dynamic;

import java.io.IOException;
import java.net.URL;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.sound.sampled.*;

public class CalculationScreen {

    City[] cities; // Array of City objects
     String source; // Source city name
     String dest; // Destination city name
    NodeSolution[][] dpTable; // 2D array representing the dynamic programming table

    private int start, end; // Indices for source and destination cities within the array

    private Stage stage; // Stage for the application


    public Scene createScene(Stage stage, City[] cities, String source, String dest) {

        Main mainApp = (Main) stage.getUserData();   //This allows you to access


        this.stage = stage;
        this.cities = cities;
        this.source = source;
        this.dest = dest;

        initializeSourceAndDest();

        generateDPTable();

        BorderPane border = new BorderPane();


        // Text for min cost
        Text minTxt = new Text("Your Cheapest Trip");
        minTxt.setFont(Font.font("Fredoka SemiBold", 46));
        minTxt.setFill(Color.web("#3f7d24"));
        minTxt.setTextAlignment(TextAlignment.CENTER);

        // Soft white glow so the title stays readable over the animated background
        DropShadow glow = new DropShadow(14, Color.rgb(255, 255, 255, 0.9));
        glow.setSpread(0.4);
        minTxt.setEffect(glow);

        // Trip summary under the title
        Label tripLabel = new Label(source + "  \u2192  " + dest);
        tripLabel.setStyle("-fx-text-fill: #4b6b3f; -fx-font-family: 'Nunito Bold'; -fx-font-size: 20px;");
        VBox.setMargin(tripLabel, new Insets(0, 0, 12, 0));

        String cardStyle = "-fx-background-color: rgba(255,255,255,0.85); -fx-background-radius: 12px; " +
                "-fx-border-color: rgba(63,125,36,0.25); -fx-border-radius: 12px;";
        String captionStyle = "-fx-text-fill: #6b8a5e; -fx-font-family: 'Nunito Bold'; -fx-font-size: 13px;";


        // Optimal cost: small caption + big number
        Label costCaption = new Label("OPTIMAL COST");
        costCaption.setStyle(captionStyle);

        Label optimalCostSolution = new Label(String.valueOf(dpTable[start][end].getOptimalCost()));
        optimalCostSolution.setFont(Font.font("Fredoka SemiBold", 34));
        optimalCostSolution.setStyle("-fx-text-fill: #3f7d24;");

        VBox costText = new VBox(0, costCaption, optimalCostSolution);
        costText.setAlignment(Pos.CENTER_LEFT);

        HBox optimalSolBox = new HBox(12);
        optimalSolBox.setPadding(new Insets(10, 22, 10, 16));
        optimalSolBox.setStyle(cardStyle);

        ImageView optimalIcon = new ImageView(new Image(getClass().getResourceAsStream("/optimalIcon.png")));
        optimalIcon.setFitWidth(36);
        optimalIcon.setFitHeight(36);


        // Adjust the size to match the content
        optimalSolBox.setMaxWidth(Region.USE_PREF_SIZE);
        optimalSolBox.setMaxHeight(Region.USE_PREF_SIZE);


        optimalSolBox.getChildren().addAll(optimalIcon, costText);
        optimalSolBox.setAlignment(Pos.CENTER);





        // Optimal path: small caption + the path
        Label pathCaption = new Label("OPTIMAL PATH");
        pathCaption.setStyle(captionStyle);

        Label optimalPathLabel = new Label(dpTable[start][end].getOptimalPath().replace("->", "\u2192"));
        optimalPathLabel.setStyle("-fx-text-fill: #2f4a28; -fx-font-family: 'Nunito Bold'; -fx-font-size: 17px;");
        optimalPathLabel.setWrapText(true); // Long paths go to a new line instead of being cut with "..."
        optimalPathLabel.setMaxWidth(650);

        VBox pathText = new VBox(2, pathCaption, optimalPathLabel);
        pathText.setAlignment(Pos.CENTER_LEFT);

        HBox optPathBox = new HBox(12);
        optPathBox.setPadding(new Insets(10, 22, 10, 16));
        optPathBox.setStyle(cardStyle);

        ImageView optimalPathIcon = new ImageView(new Image(getClass().getResourceAsStream("/optPathIcon.png")));
        optimalPathIcon.setFitWidth(36);
        optimalPathIcon.setFitHeight(36);


        // Adjust the size to match the content
        optPathBox.setMaxWidth(Region.USE_PREF_SIZE);
        optPathBox.setMaxHeight(Region.USE_PREF_SIZE);


        optPathBox.getChildren().addAll(optimalPathIcon, pathText);
        optPathBox.setAlignment(Pos.CENTER);




        NodeSolution[] solutionInfo = dpTable[start][end].getCellInfos();


        // Buttons
        Button altBtn = createStyledButton("Load alternative paths");
        Button backBtn = createStyledButton("Back", true);
        Button tableBtn = createStyledButton("Show DP table");

        VBox buttonsBox = new VBox(15);
        buttonsBox.getChildren().addAll(altBtn,tableBtn,backBtn);
        buttonsBox.setAlignment(Pos.CENTER);
        buttonsBox.setPadding(new Insets(15, 0, 0, 0));
        // Buttons Actions

        altBtn.setOnAction(e -> {
            playSound("/clickSound.wav");
            showAlternativeStage(solutionInfo);
        });


        backBtn.setOnAction(e -> {
        playSound("/clickSound.wav");
            StartScene startScene = new StartScene();
            Scene scene = startScene.createScene(stage); // Create the scene
            mainApp.switchToScene(scene);  // Switch to the new scene using Main's method
        });

        tableBtn.setOnAction(e ->{
            playSound("/clickSound.wav");
            BuildTable buildTable = new BuildTable();
            Scene scene = buildTable.createScene(stage,cities,source,dest,dpTable,start,end); // Create the scene
            mainApp.switchToScene(scene);  // Switch to the new scene using Main's method
        });



        VBox contentBox = new VBox(10, minTxt, tripLabel, optimalSolBox, optPathBox,
               buttonsBox);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setPadding(new Insets(25));



        border.setCenter(contentBox);


        // Set background for the border
        Image backgroundImage = new Image("/solBk.gif", true); // Loads in the background (the loading screen shows meanwhile)
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
        button.setPrefWidth(280);
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
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException | IllegalArgumentException e) {
            // IllegalArgumentException: no audio device available, just skip the sound
            e.printStackTrace();
        }
    }

    /**
     * Generates the dynamic programming table for the problem using the cities' information.
     */
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

    /**
     * Determines and sets the optimal cost and path for a particular cell in the dynamic programming table.
     * @param i Source city index
     * @param j Destination city index
     * @return NodeSolution object with the minimal cost and path
     */
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

    private void showAlternativeStage(NodeSolution[] nodeSolutions) {
        Stage detailStage = new Stage();

        VBox contentBox = new VBox(15);
        contentBox.setAlignment(Pos.CENTER_LEFT);
        contentBox.setPadding(new Insets(20));
        contentBox.setStyle("-fx-background-color: rgba(255,255,255,0.88); -fx-background-radius: 14px; " +
                "-fx-border-color: rgba(63,125,36,0.25); -fx-border-radius: 14px;");
        contentBox.setMaxWidth(Region.USE_PREF_SIZE);
        contentBox.setMaxHeight(Region.USE_PREF_SIZE);

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(30, 30, 30, 300)); // Extra space on the left for the frog in the background

        // Background image setup
        Image backgroundImage = new Image("altBk.gif", true); // Loads in the background so the window opens right away
        BackgroundImage bgImage = new BackgroundImage(
                backgroundImage,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(100, 100, true, true, false, true)
        );
        root.setBackground(new Background(bgImage));

        if (nodeSolutions != null && dpTable[start][end] != null) {
            // Title
            Label titleCaption = new Label("OPTIMAL PATH FROM " + source.toUpperCase() + " TO " + dest.toUpperCase());
            titleCaption.setStyle("-fx-text-fill: #6b8a5e; -fx-font-family: 'Nunito Bold'; -fx-font-size: 13px;");

            Label alternativeLabel = new Label(dpTable[start][end].getOptimalPath().replace("->", "\u2192")
                    + "   (" + dpTable[start][end].getOptimalCost() + ")");
            alternativeLabel.setStyle("-fx-text-fill: #2f4a28; -fx-font-family: 'Nunito ExtraBold'; -fx-font-size: 19px;");
            alternativeLabel.setWrapText(true);

            VBox titleText = new VBox(2, titleCaption, alternativeLabel);

            // Icon
            ImageView optimalPathIcon = new ImageView(new Image(getClass().getResourceAsStream("/optPathIcon.png")));
            optimalPathIcon.setFitWidth(32);
            optimalPathIcon.setFitHeight(32);

            HBox titleBox = new HBox(12, optimalPathIcon, titleText);
            titleBox.setAlignment(Pos.CENTER_LEFT);

            Label otherPathsLabel = new Label("Other paths");
            otherPathsLabel.setStyle("-fx-text-fill: #3f7d24; -fx-font-family: 'Fredoka SemiBold'; -fx-font-size: 18px; -fx-padding: 6 0 0 2;");

            contentBox.getChildren().addAll(titleBox, otherPathsLabel);

            // Alternatives (skip the optimal path itself, it's already shown above)
            int alternativesCount = 0;
            for (NodeSolution nodeSolution : nodeSolutions) {
                if (nodeSolution != null && !nodeSolution.getOptimalPath().equals(dpTable[start][end].getOptimalPath())) {
                    alternativesCount++;
                    VBox altBox = new VBox(5);
                    altBox.setPadding(new Insets(12));
                    altBox.setSpacing(4);
                    altBox.setStyle(
                            "-fx-background-color: #f1f8ec;" +
                                    "-fx-background-radius: 12px;" +
                                    "-fx-border-color: #b9dba6;" +
                                    "-fx-border-radius: 12px;" +
                                    "-fx-border-width: 1.5px;"
                    );

                    Label pathLabel = new Label(nodeSolution.getOptimalPath().replace("->", "\u2192"));
                    pathLabel.setStyle("-fx-text-fill: #2f4a28; -fx-font-family: 'Nunito Bold'; -fx-font-size: 16px;");
                    pathLabel.setWrapText(true);

                    // Cost label + icon in a horizontal box
                    ImageView moneyIcon = new ImageView(new Image(getClass().getResourceAsStream("/moneyIcon.png")));
                    moneyIcon.setFitWidth(18);
                    moneyIcon.setFitHeight(18);

                    Label costLabel = new Label("Cost: " + nodeSolution.getOptimalCost()
                            + "  (+" + (nodeSolution.getOptimalCost() - dpTable[start][end].getOptimalCost()) + ")");
                    costLabel.setStyle("-fx-text-fill: #4b6b3f; -fx-font-family: 'Nunito SemiBold'; -fx-font-size: 15px;");

                    HBox costBox = new HBox(6, moneyIcon, costLabel);
                    costBox.setAlignment(Pos.CENTER_LEFT);

                    altBox.getChildren().addAll(pathLabel, costBox);
                    contentBox.getChildren().add(altBox);
                }
            }

            if (alternativesCount == 0) {
                Label noAlternatives = new Label("There are no other paths for this trip.");
                noAlternatives.setStyle("-fx-text-fill: #4b6b3f; -fx-font-family: 'Nunito SemiBold'; -fx-font-size: 15px;");
                contentBox.getChildren().add(noAlternatives);
            }


            root.setCenter(contentBox);
        }

        Scene scene = new Scene(root, 1000, 600);
        detailStage.initOwner(stage); // Stays on top of the app and closes with it
        detailStage.getIcons().addAll(stage.getIcons());
        detailStage.setTitle("Alternative Solutions");
        detailStage.setScene(scene);
        detailStage.show();
    }




}