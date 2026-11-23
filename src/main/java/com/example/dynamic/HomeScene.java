package com.example.dynamic;

import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;

// First screen of the app: title, Start and Quit
public class HomeScene {

    public Scene createScene(Stage stage) {

        Main mainApp = (Main) stage.getUserData();

        BorderPane border = new BorderPane();

        Image backgroundImage = new Image("bk.gif", true); // Loads in the background (the loading screen shows meanwhile)
        BackgroundImage bgImage = new BackgroundImage(
                backgroundImage,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT,
                new BackgroundSize(100, 100, true, true, false, true)
        );
        border.setBackground(new Background(bgImage));

        Text title = createStyledText("Minimum Cost Calculator");

        Label subtitle = new Label("Find the cheapest trip for the traveling frog");
        subtitle.setStyle("-fx-text-fill: #4b6b3f; -fx-font-family: 'Nunito SemiBold'; -fx-font-size: 18px;");
        VBox.setMargin(subtitle, new Insets(0, 0, 24, 0));

        Button startBtn = createStyledButton("Start");
        Button quitBtn = createStyledButton("Quit", true);

        VBox contentBox = new VBox(14, title, subtitle, startBtn, quitBtn);
        contentBox.setAlignment(Pos.CENTER);
        border.setCenter(contentBox);

        // Small footer at the bottom
        Label footer = new Label("Analysis of Algorithms  \u00b7  Birzeit University");
        footer.setStyle("-fx-text-fill: #4b6b3f; -fx-font-family: 'Nunito SemiBold'; -fx-font-size: 13px; -fx-opacity: 0.8;");
        BorderPane.setAlignment(footer, Pos.CENTER);
        BorderPane.setMargin(footer, new Insets(0, 0, 18, 0));
        border.setBottom(footer);

        // ACTIONS

        startBtn.setOnAction(e -> {
            playSound("/clickSound.wav");
            StartScene startScene = new StartScene();
            mainApp.switchToScene(startScene.createScene(stage));
        });

        quitBtn.setOnAction(e -> {
            playSound("/clickSound.wav");
            Platform.exit();
        });

        return new Scene(border, 1000, 600);
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

}
