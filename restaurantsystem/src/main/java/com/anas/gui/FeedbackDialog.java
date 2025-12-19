package com.anas.gui;

import com.anas.logic.FeedbackManager;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class FeedbackDialog {

    // Variable to store the rating (1-5)
    private static int selectedRating = 5; 

    public static void display() {
        Stage window = new Stage();
        
        // Block interaction with other windows until this one is closed
        window.initModality(Modality.APPLICATION_MODAL); 
        window.setTitle("We value your feedback!");
        window.setMinWidth(400);

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(30));
        layout.setAlignment(Pos.CENTER);
        // Dark Theme Background
        layout.setStyle("-fx-background-color: #2D3447;"); 

        // 1. Header
        Label lblHeader = new Label("How was your meal?");
        lblHeader.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");

        // 2. Name Input
        TextField nameInput = new TextField();
        nameInput.setPromptText("Your Name");
        styleField(nameInput);

        // 3. Rating System (Buttons 1-5)
        Label lblRating = new Label("Rate Us:");
        lblRating.setStyle("-fx-text-fill: #a0a0a0;");
        
        HBox ratingBox = new HBox(10);
        ratingBox.setAlignment(Pos.CENTER);
        
        // Create 5 buttons for 1-5 stars
        for (int i = 1; i <= 5; i++) {
            Button btnStar = new Button(String.valueOf(i));
            btnStar.setPrefSize(40, 40);
            int ratingValue = i; // Capture variable for lambda
            
            // Default Style
            btnStar.setStyle("-fx-background-color: transparent; -fx-border-color: #E58E26; -fx-text-fill: white; -fx-border-radius: 50; -fx-background-radius: 50;");
            
            btnStar.setOnAction(e -> {
                selectedRating = ratingValue;
                // Visual Highlight: Turn selected button Orange
                resetButtons(ratingBox); 
                btnStar.setStyle("-fx-background-color: #E58E26; -fx-text-fill: white; -fx-background-radius: 50;");
            });
            
            ratingBox.getChildren().add(btnStar);
        }

        // 4. Comment Box
        TextArea commentInput = new TextArea();
        commentInput.setPromptText("Any suggestions?");
        commentInput.setPrefHeight(100);
        commentInput.setStyle("-fx-control-inner-background: #393E46; -fx-text-fill: white; -fx-font-family: 'Arial';");

        // 5. Submit Button
        Button btnSubmit = new Button("Submit Feedback");
        btnSubmit.setPrefWidth(200);
        btnSubmit.setStyle("-fx-background-color: #00ADB5; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        
        btnSubmit.setOnAction(e -> {
            if (nameInput.getText().isEmpty()) {
                showAlert("Name Required", "Please enter your name!");
            } else {
                // Prepare the message string
                String fullMessage = "[" + selectedRating + "/5 Stars] " + commentInput.getText();
                
                // SAVE TO FILE (Using Mehak's Logic)
                FeedbackManager.saveFeedback(nameInput.getText(), fullMessage);
                
                // Close window
                window.close();
                showThankYouAlert();
            }
        });

        // Add everything to layout
        layout.getChildren().addAll(lblHeader, nameInput, lblRating, ratingBox, commentInput, btnSubmit);
        Animations.popIn(layout);
        
        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait(); // Wait for user to close it
    }

    // --- Helper to style text fields ---
    private static void styleField(TextField field) {
        field.setStyle("-fx-background-color: transparent; -fx-border-color: #a0a0a0; -fx-border-width: 0 0 2 0; -fx-text-fill: white;");
        field.setOnMouseClicked(e -> field.setStyle("-fx-background-color: transparent; -fx-border-color: #E58E26; -fx-border-width: 0 0 2 0; -fx-text-fill: white;"));
    }

    // --- Helper to reset star buttons ---
    private static void resetButtons(HBox container) {
        for (javafx.scene.Node node : container.getChildren()) {
            if (node instanceof Button) {
                ((Button) node).setStyle("-fx-background-color: transparent; -fx-border-color: #E58E26; -fx-text-fill: white; -fx-border-radius: 50; -fx-background-radius: 50;");
            }
        }
    }

    private static void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
    
    private static void showThankYouAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText("Thank you! Your feedback has been saved.");
        alert.showAndWait();
    }
}