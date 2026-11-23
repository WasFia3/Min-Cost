package com.example.dynamic;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeLineCap;
import javafx.util.Duration;

public class LoadingScene {

    public Scene createScene() {

        BorderPane border = new BorderPane();

        // Same light cloud background as the DP table screen (small image, loads instantly)
        Image backgroundImage = new Image("/babyBlueBk.jpg");
        BackgroundImage bgImage = new BackgroundImage(
                backgroundImage,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT,
                new BackgroundSize(100, 100, true, true, false, true)
        );
        border.setBackground(new Background(bgImage));

        // The traveling frog from the app icon
        ImageView frogImageView = new ImageView(new Image(getClass().getResourceAsStream("/appIcon.png")));
        frogImageView.setFitWidth(170);
        frogImageView.setFitHeight(170);
        frogImageView.setPreserveRatio(true);

        // Small hop up and down while loading
        TranslateTransition hop = new TranslateTransition(Duration.millis(450), frogImageView);
        hop.setByY(-12);
        hop.setAutoReverse(true);
        hop.setCycleCount(Animation.INDEFINITE);
        hop.play();

        // Spinning loading circle (light ring + green arc that rotates)
        Circle track = new Circle(27, 27, 22);
        track.setFill(Color.TRANSPARENT);
        track.setStroke(Color.rgb(90, 158, 53, 0.2));
        track.setStrokeWidth(5);

        Arc arc = new Arc(27, 27, 22, 22, 90, 100);
        arc.setType(ArcType.OPEN);
        arc.setFill(Color.TRANSPARENT);
        arc.setStroke(Color.web("#5a9e35"));
        arc.setStrokeWidth(5);
        arc.setStrokeLineCap(StrokeLineCap.ROUND);

        // Fixed size box so it rotates around the circle's center
        Pane spinner = new Pane(track, arc);
        spinner.setMinSize(54, 54);
        spinner.setMaxSize(54, 54);

        RotateTransition spin = new RotateTransition(Duration.millis(900), spinner);
        spin.setByAngle(360);
        spin.setInterpolator(Interpolator.LINEAR);
        spin.setCycleCount(Animation.INDEFINITE);
        spin.play();

        Label loadingLabel = new Label("Loading...");
        loadingLabel.setStyle("-fx-text-fill: #3f7d24; -fx-font-family: 'Nunito Bold'; -fx-font-size: 18px;");

        VBox contentBox = new VBox(18, frogImageView, spinner, loadingLabel);
        contentBox.setAlignment(Pos.CENTER);
        border.setCenter(contentBox);

        return new Scene(border, 1000, 600);
    }
}
