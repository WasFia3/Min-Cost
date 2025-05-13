package com.example.dynamic;

import javafx.animation.ScaleTransition;
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
import java.util.Arrays;
import java.util.Scanner;

public class StartScene {
    File file;
    City[] citiesList;
    String source;
    String dest;

    public Scene createScene(Stage stage) {

        // Ensure Main instance is properly set
        Main mainApp = (Main) stage.getUserData();

        BorderPane border = new BorderPane();

        Image backgroundImage = new Image("bk.gif");
        BackgroundImage bgImage = new BackgroundImage(
                backgroundImage,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT,
                new BackgroundSize(100, 100, true, true, false, true)
        );
        border.setBackground(new Background(bgImage));

        border.setCenter(createStyledText("Minumam Cost Calculater"));

        Button fileBtn = createStyledButton("Upload Cities File");
        Button startBtn = createStyledButton("Start");

        //////////////////////////////////////

        // File display box
        HBox fileInfoBox = new HBox(10);
        fileInfoBox.setPadding(new Insets(10));
        fileInfoBox.setAlignment(Pos.CENTER_LEFT);
        fileInfoBox.setStyle("-fx-background-color: rgba(186,250,169,0.53); -fx-background-radius: 10px;");

        ImageView fileIcon = new ImageView(new Image(getClass().getResourceAsStream("/fileIcon.png")));
        fileIcon.setFitWidth(24);
        fileIcon.setFitHeight(24);

        Label fileNameLabel = new Label("No file selected");
        fileNameLabel.setStyle("-fx-text-fill: #465e43; -fx-font-size: 16px; -fx-font-style: italic;");

        // Adjust the size to match the content
        fileInfoBox.setMaxWidth(Region.USE_PREF_SIZE);
        fileInfoBox.setMaxHeight(Region.USE_PREF_SIZE);


        fileInfoBox.getChildren().addAll(fileIcon, fileNameLabel);
        fileInfoBox.setAlignment(Pos.CENTER);

        /////////////////////////////////////

        VBox buttonsBox = new VBox(15);
        buttonsBox.getChildren().addAll(fileInfoBox, fileBtn, startBtn);
        buttonsBox.setAlignment(Pos.CENTER);
        buttonsBox.setPadding(new Insets(20));
        BorderPane.setAlignment(buttonsBox, Pos.CENTER);

        border.setBottom(buttonsBox);

        Scene scene = new Scene(border, 1000, 600);



        // Set icon for the app
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/appIcon.png")));


        // ACTIONS

        fileBtn.setOnAction(e -> {
            // Play click sound
            playSound("/clickSound.wav");

            // Create file chooser
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("File Chooser");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text files", "*txt"));

            file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                fileNameLabel.setText(file.getName());
                fileInfoBox.setVisible(true);
            }

        });


        // Start button action

        startBtn.setOnAction(e -> {
            playSound("/clickSound.wav");

            if (file == null || !file.exists()) {
                createAlert(Alert.AlertType.ERROR,"Error","No File Found","You should upload a valid file first.");
                return;
            }

            try {
                readCities();
                CalculationScreen calculationScreen = new CalculationScreen();
                stage.setScene(calculationScreen.createScene(stage,citiesList,source,dest));
                stage.setMaximized(true);
            } catch (Exception ex) {
                createAlert(Alert.AlertType.ERROR,"Error","Invalid or corrupted file format.","Please upload a properly formatted file.");
            }
        });

        stage.setScene(scene);
        return scene;
    }

    public void createAlert(Alert.AlertType type, String title, String header, String content) {
        playSound("/errorSound.wav");
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }


    public Text createStyledText(String textContent) {
        Text text = new Text(textContent);
        text.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 40));
        text.setFill(Color.web("#68af3d"));
        text.setStroke(Color.FORESTGREEN);
        text.setStrokeWidth(2);
        return text;
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

    private void readCities() throws IOException {
        if (file == null)
            throw new FileNotFoundException();
        try{
            Scanner scan = new Scanner(file);

            // Get number of cities
            int numberOfCities = Integer.parseInt(scan.nextLine().trim());
            System.out.println("the number of cities:"+ numberOfCities);


            String[] first_line = scan.nextLine().split(",");
            source = first_line[0].trim();
            dest = first_line[1].trim();

            // Assign the list of Cities size with the number of cities
            citiesList = new City[numberOfCities];

            // Print the first line
            System.out.println(Arrays.toString(first_line));


            int number = 0;
            int totalLines = 1;
            int firstLine = 0;
            int stage = 1;
            City city = null;


            while (scan.hasNextLine()) {
                // Read and trim the current line from the file
                String line = scan.nextLine().trim();

                // Split the city name from the rest of the line using the first comma only
                int firstComma = line.indexOf(',');
                String cityName = line.substring(0, firstComma).trim();
                String rest = line.substring(firstComma + 1).trim();

                // Debug: Print the current city and its connections line
                System.out.println("--------------------------------------------------");
                System.out.println("Reading line: " + line);
                System.out.println("Parsed city name: " + cityName);
                System.out.println("Raw connections string: " + rest);

                // Split the rest of the line into connection blocks like [A,22,70]
                String[] connections = rest.split("(?<=\\]),\\s*");

                // If it's the first city, create it manually with no connections yet
                if (number == 0) {
                    city = new City(cityName, 0, 0, new int[0]);
                    citiesList[number++] = city;
                    System.out.println("Created starting city: " + cityName);
                }

                // Loop through all connections for this city
                for (String conn : connections) {
                    conn = conn.trim();
                    conn = conn.substring(1, conn.length() - 1); // Remove brackets [ and ]
                    String[] data = conn.split(",");
                    for (int j = 0; j < data.length; j++) data[j] = data[j].trim(); // Remove extra whitespace

                    // Debug: Print parsed connection data
                    System.out.println("Parsed connection data: " + Arrays.toString(data));

                    if (firstLine == 0) {
                        // First row: Create new city objects
                        int[] petrolCosts = new int[totalLines];
                        petrolCosts[0] = Integer.parseInt(data[1]); // Petrol cost from start city
                        city = new City(data[0], stage, Integer.parseInt(data[2]), petrolCosts);
                        citiesList[number++] = city;
                        System.out.println("Added new city: " + data[0] + " with petrol cost: " + data[1] + " and stage cost: " + data[2]);
                    } else {
                        // Update petrol cost for existing cities
                        String connCity = data[0];
                        int cost = Integer.parseInt(data[1]);
                        getCity(connCity, numberOfCities).getPetrolCost()[firstLine] = cost;
                        System.out.println("Updated petrol cost for city " + connCity + " at line " + firstLine + ": " + cost);
                    }
                }

                // Update stage and counters
                if (firstLine == 0)
                    stage++;
                firstLine++;

                // If all lines of connections for current row are processed, reset counters
                if (firstLine == totalLines) {
                    firstLine = 0;
                    totalLines = connections.length;
                    System.out.println("Finished reading a full layer. totalLines set to: " + totalLines);
                }
            }


        } catch (IOException e) {
            throw new IOException();
        }
    }

    private City getCity(String cityName, int numberOfCities) {
        for (int i = 0; i < numberOfCities; i++)
            if (citiesList[i].getName().equals(cityName))
                return citiesList[i];
        return null;
    }
}




