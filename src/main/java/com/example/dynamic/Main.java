package com.example.dynamic;

import javafx.animation.PauseTransition;
import javafx.application.Application;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;

import javafx.stage.Stage;
import javafx.util.Duration;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

public class Main extends Application {
    Stage stage;
    Parent loadingRoot; // Loading screen, created once and reused between screens

    // Background music (keeps playing between screens)
    Clip musicClip;
    boolean musicOn = true;
    Button musicButton; // Small speaker button in the top right corner of every screen

    // Speaker icons (Material Icons, Apache 2.0)
    private static final String SOUND_ON_ICON = "M3 9v6h4l5 5V4L7 9H3zm13.5 3c0-1.77-1.02-3.29-2.5-4.03v8.05c1.48-.73 2.5-2.25 2.5-4.02zM14 3.23v2.06c2.89.86 5 3.54 5 6.71s-2.11 5.85-5 6.71v2.06c4.01-.91 7-4.49 7-8.77s-2.99-7.86-7-8.77z";
    private static final String SOUND_OFF_ICON = "M16.5 12c0-1.77-1.02-3.29-2.5-4.03v2.21l2.45 2.45c.03-.2.05-.41.05-.63zm2.5 0c0 .94-.2 1.82-.54 2.64l1.51 1.51C20.63 14.91 21 13.5 21 12c0-4.28-2.99-7.86-7-8.77v2.06c2.89.86 5 3.54 5 6.71zM4.27 3L3 4.27 7.73 9H3v6h4l5 5v-6.73l4.25 4.25c-.67.52-1.42.93-2.25 1.18v2.06c1.38-.31 2.63-.95 3.69-1.81L19.73 21 21 19.73l-9-9L4.27 3zM12 4L9.91 6.09 12 8.18V4z";

    public void start(Stage stage) throws Exception {

        this.stage = stage;
        this.stage.setUserData(this); // Make Main accessible globally

        loadFonts();
        musicButton = createMusicButton();
        startMusic();

        // Show the loading screen first, then the home screen once its background is ready
        // (this Scene stays for the whole app, only its content changes)
        LoadingScene loadingScene = new LoadingScene();
        Scene scene = loadingScene.createScene();
        loadingRoot = scene.getRoot();
        stage.setScene(scene);
        stage.setTitle("Minimum Cost Calculator");
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/appIcon.png")));
        stage.setMaximized(true); // Open maximized so every screen has the same size
        stage.show();

        HomeScene homeScene = new HomeScene();
        switchToScene(homeScene.createScene(stage));
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void stop() {
        // Release the audio line when the app closes
        if (musicClip != null)
            musicClip.close();
    }

    // Calm music-box loop, played quietly so the click sounds stay clear
    private void startMusic() {
        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(getClass().getResource("/backgroundMusic.wav"));
            musicClip = AudioSystem.getClip();
            musicClip.open(audioStream);

            if (musicClip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                FloatControl volume = (FloatControl) musicClip.getControl(FloatControl.Type.MASTER_GAIN);
                volume.setValue(-6f); // Lower volume (in decibels)
            }

            musicClip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception e) {
            // No audio device: the app works without music
            musicClip = null;
            e.printStackTrace();
        }
    }

    private Button createMusicButton() {
        SVGPath icon = new SVGPath();
        icon.setContent(SOUND_ON_ICON);
        icon.setFill(Color.web("#3f7d24"));

        Button button = new Button();
        button.setGraphic(icon);
        button.setStyle("-fx-background-color: rgba(255,255,255,0.9); -fx-background-radius: 22px; " +
                "-fx-border-color: #5a9e35; -fx-border-radius: 22px; -fx-border-width: 2px; -fx-padding: 0;");
        button.setMinSize(44, 44);
        button.setPrefSize(44, 44);
        button.setMaxSize(44, 44);
        button.setCursor(Cursor.HAND);
        button.setFocusTraversable(false);
        button.setTooltip(new Tooltip("Music on / off"));

        button.setOnAction(e -> {
            musicOn = !musicOn;
            icon.setContent(musicOn ? SOUND_ON_ICON : SOUND_OFF_ICON);
            if (musicClip != null) {
                if (musicOn)
                    musicClip.loop(Clip.LOOP_CONTINUOUSLY);
                else
                    musicClip.stop();
            }
        });

        StackPane.setAlignment(button, Pos.TOP_RIGHT);
        StackPane.setMargin(button, new Insets(16));
        return button;
    }

    // Puts the screen and the music button on top of each other
    private Parent withMusicButton(Parent root) {
        return new StackPane(root, musicButton);
    }

    // Fonts used by all screens (Nunito for text, Fredoka for titles)
    public static void loadFonts() {
        String[] fonts = {"/Nunito-Medium.ttf", "/Nunito-SemiBold.ttf", "/Nunito-Bold.ttf",
                "/Nunito-ExtraBold.ttf", "/Fredoka-SemiBold.ttf"};
        for (String font : fonts)
            Font.loadFont(Main.class.getResourceAsStream(font), 14);
    }

    // Method to switch scenes
    public void switchToScene(Scene newScene) {

        // Keep the same Scene and only swap its content,
        // so the window doesn't resize (or un-maximize) on every screen change
        Parent root = newScene.getRoot();
        newScene.setRoot(new Pane()); // Detach the root from the temporary scene

        // The animated backgrounds are big, so they load in the background.
        // If it's not ready yet, show the loading screen until it is.
        Image background = getBackgroundImage(root);
        if (background == null || background.getProgress() >= 1) {
            stage.getScene().setRoot(withMusicButton(root));
            return;
        }

        stage.getScene().setRoot(loadingRoot);
        long shownAt = System.currentTimeMillis();
        boolean[] done = {false};

        Runnable showScreen = () -> {
            if (done[0])
                return;
            done[0] = true;

            // Keep the loading screen at least half a second so it doesn't just flash
            long remaining = Math.max(0, 500 - (System.currentTimeMillis() - shownAt));
            PauseTransition pause = new PauseTransition(Duration.millis(remaining));
            pause.setOnFinished(e -> stage.getScene().setRoot(withMusicButton(root)));
            pause.play();
        };

        background.progressProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue.doubleValue() >= 1)
                showScreen.run();
        });
        background.errorProperty().addListener((obs, oldValue, newValue) -> showScreen.run());

        // In case it finished loading before the listeners were added
        if (background.getProgress() >= 1 || background.isError())
            showScreen.run();
    }

    private Image getBackgroundImage(Parent root) {
        if (root instanceof Region) {
            Region region = (Region) root;
            if (region.getBackground() != null && !region.getBackground().getImages().isEmpty())
                return region.getBackground().getImages().get(0).getImage();
        }
        return null;
    }
}
