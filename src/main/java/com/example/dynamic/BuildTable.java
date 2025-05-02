package com.example.dynamic;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class BuildTable {

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



}
