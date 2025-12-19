package com.anas.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ReceiptDialog {

    public static void display(String receiptText) {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Order Receipt");
        window.setMinWidth(450);
        window.setResizable(false);

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #FFFFFF;"); // Clean white background

        // --- DISPLAY THE TEXT ---
        TextArea textArea = new TextArea(receiptText);
        textArea.setEditable(false);
        textArea.setWrapText(false); // Important for alignment
        textArea.setPrefHeight(500); 
        textArea.setPrefWidth(410);
        
        // Use Monospaced font so columns align perfectly
        textArea.setFont(Font.font("Monospaced", 13)); 
        // Style: White paper, Black text
        textArea.setStyle("-fx-control-inner-background: #ffffff; -fx-text-fill: #000000; -fx-font-family: 'Monospaced';");

        // --- SCROLL PANE ---
        ScrollPane scroll = new ScrollPane(textArea);
        scroll.setFitToWidth(true);
        scroll.setFitToHeight(true);
        scroll.setStyle("-fx-background-color:transparent; -fx-background: transparent;");

        // --- CLOSE BUTTON ---
        Button btnClose = new Button("Continue to Feedback");
        btnClose.setStyle("-fx-background-color: #00ADB5; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 10 20;");
        btnClose.setOnAction(e -> window.close());

        layout.getChildren().addAll(scroll, btnClose);
        
        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait(); 
    }
}