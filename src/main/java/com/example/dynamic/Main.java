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
    String des;

    public void start(Stage stage) throws Exception {

        BorderPane bp = new BorderPane();

        Image backgroundImage = new Image("bk.gif");
        BackgroundImage bgImage = new BackgroundImage(
                backgroundImage,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT,
                new BackgroundSize(100, 100, true, true, false, true)
        );
        bp.setBackground(new Background(bgImage));

        bp.setCenter(createStyledText("Minumam Cost Calculater"));

        Button fileBtn = createStyledButton("Upload Cities File");
        Button startBtn = createStyledButton("Start");

        // File display box
        HBox fileInfoBox = new HBox(10);
        fileInfoBox.setPadding(new Insets(10));
        fileInfoBox.setAlignment(Pos.CENTER_LEFT);
        fileInfoBox.setStyle("-fx-background-color: rgba(186,250,169,0.53); -fx-background-radius: 10px;");
        fileInfoBox.setVisible(false);

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

        VBox buttonsBox = new VBox(15);
        buttonsBox.getChildren().addAll(fileInfoBox, fileBtn, startBtn);
        buttonsBox.setAlignment(Pos.CENTER);
        buttonsBox.setPadding(new Insets(20));
        BorderPane.setAlignment(buttonsBox, Pos.CENTER);

        bp.setBottom(buttonsBox);

        Scene scene = new Scene(bp, 1000, 600);



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
            try {
                readCities();
            } catch (FileNotFoundException e2) {
            } catch (Exception e2) {
                createAlert(Alert.AlertType.ERROR,"Error","Invalid or corrupted file format.","Please upload a properly formatted file.");
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
                SolutionScr solutionScr = new SolutionScr(stage, scene, citiesList, source, des);
                Scene solutionScene = new Scene(solutionScr);
                stage.setScene(solutionScene);
                stage.setMaximized(true);
            } catch (Exception ex) {
                createAlert(Alert.AlertType.ERROR,"Error","Invalid or corrupted file format.","Please upload a properly formatted file.");
            }
        });

        stage.setScene(scene);
        stage.setTitle("Minimum Cost Calculator");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
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
        text.setStroke(Color.BLACK);
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
        try (Scanner sc = new Scanner(file)) {
            int numberOfCities = Integer.parseInt(sc.nextLine().trim());
            String[] tkz = sc.nextLine().split(",");
            source = tkz[0].trim();
            des = tkz[1].trim();
            citiesList = new City[numberOfCities];

            int number = 0;
            int totalLines = 1;
            int firstLine = 0;
            int stage = 1;
            City city = null;
            while (sc.hasNextLine()) {
                String[] lineInfo = sc.nextLine().split(",");
                if (number == 0) {
                    city = new City(lineInfo[0], 0, 0, new int[0]);
                    citiesList[number++] = city;
                }
                for (int i = 1; i < lineInfo.length; i++) {
                    lineInfo[i] = lineInfo[i].trim();
                    String[] info = lineInfo[i].substring(1, lineInfo[i].length() - 1).split(":");
                    if (firstLine == 0) {
                        int[] arr = new int[totalLines];
                        arr[0] = Integer.parseInt(info[1]);
                        city = new City(info[0], stage, Integer.parseInt(info[2]), arr);
                        citiesList[number] = city;
                        number++;
                    } else {
                        String cityName = info[0];
                        getCity(cityName, numberOfCities).getPetrolCost()[firstLine] = Integer.parseInt(info[1]);
                    }
                }
                if (firstLine == 0)
                    stage++;
                firstLine++;
                if (firstLine == totalLines) {
                    firstLine = 0;
                    totalLines = lineInfo.length - 1;
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