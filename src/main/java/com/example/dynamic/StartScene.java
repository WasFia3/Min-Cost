package com.example.dynamic;

import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.sound.sampled.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Scanner;

public class StartScene {
    File file;
    City[] citiesList;
    String source;
    String dest;

    // Trip cards on this screen: Iraq, Palestine, your own file
    private VBox[] tripCards = new VBox[3];
    private Button calculateBtn;
    private Stage stage;

    private static final String CARD_STYLE =
            "-fx-background-color: rgba(255,255,255,0.9); -fx-background-radius: 16px; " +
            "-fx-border-color: rgba(63,125,36,0.25); -fx-border-radius: 16px; -fx-border-width: 2px;";
    private static final String CARD_HOVER_STYLE =
            "-fx-background-color: rgba(255,255,255,0.97); -fx-background-radius: 16px; " +
            "-fx-border-color: #8cc46c; -fx-border-radius: 16px; -fx-border-width: 2px;";
    private static final String CARD_SELECTED_STYLE =
            "-fx-background-color: #f1f9eb; -fx-background-radius: 16px; " +
            "-fx-border-color: #5a9e35; -fx-border-radius: 16px; -fx-border-width: 2px;";

    public Scene createScene(Stage stage) {

        // Ensure Main instance is properly set
        Main mainApp = (Main) stage.getUserData();
        this.stage = stage;

        BorderPane border = new BorderPane();

        // Calm cloud background so the cards are easy to read
        Image backgroundImage = new Image("/babyBlueBk.jpg");
        BackgroundImage bgImage = new BackgroundImage(
                backgroundImage,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT,
                new BackgroundSize(100, 100, true, true, false, true)
        );
        border.setBackground(new Background(bgImage));

        Text title = createStyledText("Choose a Trip");

        Label subtitle = new Label("Pick a sample trip, or upload your own cities file");
        subtitle.setStyle("-fx-text-fill: #4b6b3f; -fx-font-family: 'Nunito SemiBold'; -fx-font-size: 18px;");
        VBox.setMargin(subtitle, new Insets(0, 0, 18, 0));

        //////////////////////////////////////

        // Sample trips (files that come with the app)
        tripCards[0] = createTripCard("/iraqFrog.png", 100, "Iraq", readTripInfo(getClass().getResourceAsStream("/iraqTrip.txt")));
        tripCards[1] = createTripCard("/palestineFrog.png", 100, "Palestine", readTripInfo(getClass().getResourceAsStream("/palestineTrip.txt")));

        // Your own file
        tripCards[2] = createTripCard("/fileIcon.png", 64, "Your Own File", "Click to choose a .txt file");

        Label formatLink = new Label("File format  ?");
        formatLink.setStyle("-fx-text-fill: #3f7d24; -fx-font-family: 'Nunito ExtraBold'; -fx-font-size: 14px; -fx-underline: true;");
        formatLink.setCursor(Cursor.HAND);
        tripCards[2].getChildren().add(formatLink);

        HBox cardsBox = new HBox(24, tripCards[0], tripCards[1], tripCards[2]);
        cardsBox.setAlignment(Pos.CENTER);

        /////////////////////////////////////

        Button backBtn = createStyledButton("Back", true);
        calculateBtn = createStyledButton("Calculate");
        calculateBtn.setDisable(true); // Enabled after choosing a trip

        HBox buttonsBox = new HBox(16, backBtn, calculateBtn);
        buttonsBox.setAlignment(Pos.CENTER);
        VBox.setMargin(buttonsBox, new Insets(26, 0, 0, 0));

        VBox contentBox = new VBox(14, title, subtitle, cardsBox, buttonsBox);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setPadding(new Insets(20));
        border.setCenter(contentBox);

        Scene scene = new Scene(border, 1000, 600);


        // Set icon for the app (only once, this scene is created again on every Back)
        if (stage.getIcons().isEmpty())
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/appIcon.png")));


        // ACTIONS

        tripCards[0].setOnMouseClicked(e -> chooseSample("/iraqTrip.txt", 0));
        tripCards[1].setOnMouseClicked(e -> chooseSample("/palestineTrip.txt", 1));

        tripCards[2].setOnMouseClicked(e -> {
            // Play click sound
            playSound("/clickSound.wav");

            // Create file chooser
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Choose a Cities File");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text files", "*.txt"));

            File chosenFile = fileChooser.showOpenDialog(stage);
            if (chosenFile != null)
                useOwnFile(chosenFile);
        });

        formatLink.setOnMouseClicked(e -> {
            e.consume(); // Don't open the file chooser of the card
            playSound("/clickSound.wav");
            showFileFormatWindow(stage);
        });

        backBtn.setOnAction(e -> {
            playSound("/clickSound.wav");
            HomeScene homeScene = new HomeScene();
            mainApp.switchToScene(homeScene.createScene(stage));
        });

        // Calculate button action

        calculateBtn.setOnAction(e -> {
            playSound("/clickSound.wav");

            if (file == null || !file.exists()) {
                createAlert(Alert.AlertType.ERROR,"Error","No File Found","You should upload a valid file first.");
                return;
            }

            try {
                readCities();
                CalculationScreen calculationScreen = new CalculationScreen();
                mainApp.switchToScene(calculationScreen.createScene(stage,citiesList,source,dest));
            } catch (Exception ex) {
                createAlert(Alert.AlertType.ERROR,"Error","Invalid or corrupted file format.","Please upload a properly formatted file.");
            }
        });

        return scene;
    }

    // One card: icon, title and a short description
    private VBox createTripCard(String iconPath, int iconSize, String titleText, String infoText) {
        ImageView icon = new ImageView(new Image(getClass().getResourceAsStream(iconPath)));
        icon.setFitWidth(iconSize);
        icon.setFitHeight(iconSize);
        icon.setPreserveRatio(true);

        // Same height for every icon so the titles line up across the cards
        StackPane iconBox = new StackPane(icon);
        iconBox.setMinHeight(90);
        iconBox.setMaxHeight(90);

        Label cardTitle = new Label(titleText);
        cardTitle.setStyle("-fx-text-fill: #3f7d24; -fx-font-family: 'Fredoka SemiBold'; -fx-font-size: 24px;");

        Label info = new Label(infoText);
        info.setStyle("-fx-text-fill: #4b6b3f; -fx-font-family: 'Nunito SemiBold'; -fx-font-size: 15px;");
        info.setTextAlignment(TextAlignment.CENTER);
        info.setAlignment(Pos.CENTER);
        info.setWrapText(true);
        info.setMinHeight(Region.USE_PREF_SIZE); // Never cut the text with "..."

        VBox card = new VBox(6, iconBox, cardTitle, info);
        card.setAlignment(Pos.TOP_CENTER); // Titles line up across the cards
        card.setPadding(new Insets(16, 20, 20, 20));
        card.setPrefSize(240, 262);
        card.setMinSize(240, 262);
        card.setMaxSize(240, 262);
        card.setStyle(CARD_STYLE);
        card.setCursor(Cursor.HAND);
        card.setEffect(new DropShadow(10, 0, 3, Color.rgb(47, 93, 30, 0.15)));

        card.setOnMouseEntered(e -> {
            if (!card.getStyle().equals(CARD_SELECTED_STYLE))
                card.setStyle(CARD_HOVER_STYLE);
        });
        card.setOnMouseExited(e -> {
            if (!card.getStyle().equals(CARD_SELECTED_STYLE))
                card.setStyle(CARD_STYLE);
        });

        return card;
    }

    // Highlight the chosen card and enable the Calculate button
    private void selectCard(int index) {
        for (int i = 0; i < tripCards.length; i++)
            tripCards[i].setStyle(i == index ? CARD_SELECTED_STYLE : CARD_STYLE);
        calculateBtn.setDisable(false);
    }

    // Use a file chosen by the user: show its name and trip on the card, then select it
    private void useOwnFile(File chosenFile) {
        file = chosenFile;
        String info = "";
        try {
            info = readTripInfo(new FileInputStream(chosenFile));
        } catch (IOException ex) {
            // Keep only the file name
        }
        Label uploadInfo = (Label) tripCards[2].getChildren().get(2);
        uploadInfo.setText(chosenFile.getName() + (info.isEmpty() ? "" : "\n" + info));
        selectCard(2);
    }

    // Sample files are inside the app, so copy the chosen one to a temp file for readCities()
    private void chooseSample(String resourcePath, int index) {
        playSound("/clickSound.wav");
        try {
            File temp = File.createTempFile("trip", ".txt");
            temp.deleteOnExit();
            try (InputStream in = getClass().getResourceAsStream(resourcePath)) {
                Files.copy(in, temp.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
            file = temp;
            selectCard(index);
        } catch (IOException ex) {
            createAlert(Alert.AlertType.ERROR,"Error","Couldn't open the sample trip.","Please try again or upload a file.");
        }
    }

    // Reads the first two lines of a cities file, e.g. "Baghdad -> Basra" and "10 cities"
    private String readTripInfo(InputStream in) {
        try (Scanner scan = new Scanner(in)) {
            int numberOfCities = Integer.parseInt(scan.nextLine().trim());
            String[] first_line = scan.nextLine().split(",");
            return first_line[0].trim() + " \u2192 " + first_line[1].trim() + "\n" + numberOfCities + " cities";
        } catch (Exception e) {
            return "";
        }
    }

    // Small window that explains the file format with an example
    private void showFileFormatWindow(Stage stage) {
        Stage formatStage = new Stage();
        formatStage.initOwner(stage); // Stays on top of the app and closes with it
        formatStage.getIcons().addAll(stage.getIcons());
        formatStage.setTitle("Cities File Format");

        Label heading = new Label("Cities File Format");
        heading.setStyle("-fx-text-fill: #3f7d24; -fx-font-family: 'Fredoka SemiBold'; -fx-font-size: 26px;");

        String textStyle = "-fx-text-fill: #2f4a28; -fx-font-family: 'Nunito SemiBold'; -fx-font-size: 15px;";

        Label lines = new Label(
                "Line 1:  the total number of cities (including the start and the destination)\n" +
                "Line 2:  Start, Destination\n" +
                "Then one line for each city:  City, [NextCity,petrolCost,hotelCost], ...");
        lines.setStyle(textStyle);
        lines.setWrapText(true);

        // Example file
        String example = "";
        try (InputStream in = getClass().getResourceAsStream("/iraqTrip.txt")) {
            example = new String(in.readAllBytes()).trim();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Label exampleLabel = new Label(example);
        exampleLabel.setFont(Font.font("Monospaced", 13));
        exampleLabel.setStyle("-fx-text-fill: #2f4a28; -fx-background-color: #f1f8ec; -fx-background-radius: 10px; " +
                "-fx-border-color: #b9dba6; -fx-border-radius: 10px; -fx-padding: 12 14;");
        exampleLabel.setMaxWidth(Double.MAX_VALUE);

        Label exampleCaption = new Label("Example");
        exampleCaption.setStyle("-fx-text-fill: #6b8a5e; -fx-font-family: 'Nunito Bold'; -fx-font-size: 13px;");

        Label rules = new Label(
                "\u2022  Every city connects to all the cities of the next stage, in the same order\n" +
                "\u2022  The destination's hotel cost is 0\n" +
                "\u2022  No empty lines at the end of the file");
        rules.setStyle(textStyle);
        rules.setWrapText(true);

        Button saveBtn = createStyledButton("Save Example File", true);
        Button okBtn = createStyledButton("Got It");
        saveBtn.setPrefWidth(220);
        okBtn.setPrefWidth(140);
        HBox buttons = new HBox(12, saveBtn, okBtn);
        buttons.setAlignment(Pos.CENTER_RIGHT);
        VBox.setMargin(buttons, new Insets(8, 0, 0, 0));

        String exampleText = example;
        saveBtn.setOnAction(e -> {
            playSound("/clickSound.wav");
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Example File");
            fileChooser.setInitialFileName("cities.txt");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text files", "*.txt"));
            File saveFile = fileChooser.showSaveDialog(formatStage);
            if (saveFile != null) {
                try {
                    Files.writeString(saveFile.toPath(), exampleText);
                } catch (IOException ex) {
                    createAlert(Alert.AlertType.ERROR,"Error","Couldn't save the file.","Please choose another folder.");
                }
            }
        });
        okBtn.setOnAction(e -> {
            playSound("/clickSound.wav");
            formatStage.close();
        });

        VBox layout = new VBox(12, heading, lines, exampleCaption, exampleLabel, rules, buttons);
        layout.setPadding(new Insets(24, 28, 24, 28));
        layout.setStyle("-fx-background-color: white;");
        VBox.setMargin(exampleCaption, new Insets(6, 0, -6, 2));

        formatStage.setScene(new Scene(layout, 640, Region.USE_COMPUTED_SIZE));
        formatStage.show();
    }

    public void createAlert(Alert.AlertType type, String title, String header, String content) {
        playSound("/errorSound.wav");
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        // Same fonts and colors as the app, centered on the app window
        if (stage != null)
            alert.initOwner(stage);
        alert.getDialogPane().setStyle("-fx-font-family: 'Nunito SemiBold'; -fx-font-size: 15px; -fx-background-color: white;");
        alert.getDialogPane().lookupAll(".header-panel").forEach(node ->
                node.setStyle("-fx-background-color: #f1f8ec; -fx-font-family: 'Nunito ExtraBold';"));
        alert.getDialogPane().lookupAll(".header-panel .label").forEach(node ->
                node.setStyle("-fx-text-fill: #2f4a28; -fx-font-family: 'Nunito ExtraBold'; -fx-font-size: 17px;"));
        for (ButtonType buttonType : alert.getButtonTypes()) {
            Node button = alert.getDialogPane().lookupButton(buttonType);
            button.setStyle("-fx-background-color: #5a9e35; -fx-text-fill: white; -fx-font-family: 'Nunito ExtraBold'; " +
                    "-fx-background-radius: 10px; -fx-padding: 6 22;");
            button.setCursor(Cursor.HAND);
        }

        alert.showAndWait();
    }


    public Text createStyledText(String textContent) {
        Text text = new Text(textContent);
        text.setFont(Font.font("Fredoka SemiBold", 48));
        text.setFill(Color.web("#3f7d24"));

        // Soft white glow so the title stays readable over the animated background
        DropShadow glow = new DropShadow(14, Color.rgb(255, 255, 255, 0.9));
        glow.setSpread(0.4);
        text.setEffect(glow);
        return text;
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
        button.setPrefWidth(260);
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

            scan.close(); // Release the file so it can be edited or deleted while the app is open

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




