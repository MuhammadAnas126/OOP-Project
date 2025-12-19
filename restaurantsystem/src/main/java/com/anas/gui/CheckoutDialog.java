package com.anas.gui;

import java.time.LocalDate;
import java.util.function.Consumer;

import com.anas.logic.OrderCart;
import com.anas.logic.OrderManager;
import com.anas.models.Order;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class CheckoutDialog {

    public static void display(OrderCart cart, double subtotal, Consumer<String> onSuccess) {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Checkout");
        window.setMinWidth(450); 

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(30));
        layout.setStyle("-fx-background-color: #2D3447;");

        Label header = new Label("Delivery Details");
        header.setStyle("-fx-text-fill: white; -fx-font-size: 22px; -fx-font-weight: bold; -fx-font-family: 'Segoe UI';");

        // 1. BEAUTIFUL INPUT FIELDS
        TextField nameField = new TextField(); 
        nameField.setPromptText("Full Name"); 
        styleField(nameField);

        TextField phoneField = new TextField(); 
        phoneField.setPromptText("Phone Number"); 
        styleField(phoneField);
        
        TextArea addressField = new TextArea();
        addressField.setPromptText("Delivery Address (Street, House No, Area)");
        addressField.setPrefHeight(80);
        addressField.setWrapText(true);
        styleArea(addressField); // Specialized style for TextArea

        Label payLabel = new Label("Payment Method");
        payLabel.setStyle("-fx-text-fill: #a0a0a0; -fx-font-size: 14px; -fx-padding: 10 0 0 0;");
        
        ToggleGroup group = new ToggleGroup();
        RadioButton rbCash = new RadioButton("Cash on Delivery (15% Tax)");
        rbCash.setStyle("-fx-text-fill: white; -fx-cursor: hand; -fx-font-size: 13px;");
        rbCash.setToggleGroup(group);
        rbCash.setSelected(true);

        RadioButton rbCard = new RadioButton("Credit/Debit Card (5% Tax)");
        rbCard.setStyle("-fx-text-fill: white; -fx-cursor: hand; -fx-font-size: 13px;");
        rbCard.setToggleGroup(group);

        // --- CARD DETAILS BOX (Hidden by default) ---
        VBox cardBox = new VBox(12);
        cardBox.setVisible(false); cardBox.setManaged(false); 
        cardBox.setStyle("-fx-background-color: #222831; -fx-padding: 20; -fx-background-radius: 8; -fx-border-color: #393E46; -fx-border-radius: 8;");
        
        TextField cardNum = new TextField(); 
        cardNum.setPromptText("Card Number (16 digits)"); 
        styleField(cardNum);
        applyNumericLimit(cardNum, 16);

        TextField cardCvc = new TextField(); 
        cardCvc.setPromptText("CVC (3 digits)"); 
        styleField(cardCvc);
        applyNumericLimit(cardCvc, 3);

        Label expLabel = new Label("Expiry Date");
        expLabel.setStyle("-fx-text-fill: #a0a0a0; -fx-font-size: 12px;");
        
        DatePicker datePicker = new DatePicker();
        datePicker.setPromptText("Select Date");
        datePicker.setEditable(false); 
        // Style the DatePicker specifically
        datePicker.getEditor().setStyle("-fx-background-color: #393E46; -fx-text-fill: white;");
        datePicker.setStyle("-fx-control-inner-background: #393E46; -fx-background-color: #393E46; -fx-border-color: #555; -fx-border-radius: 5;");
        
        datePicker.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.compareTo(LocalDate.now()) < 0);
            }
        });

        cardBox.getChildren().addAll(new Label("Secure Payment"), cardNum, expLabel, datePicker, cardCvc);
        cardBox.getChildren().get(0).setStyle("-fx-text-fill: #00ADB5; -fx-font-weight: bold; -fx-font-size: 14px;");

        // RESIZE ANIMATION
        group.selectedToggleProperty().addListener((obs, old, newVal) -> {
            boolean isCard = newVal == rbCard;
            cardBox.setVisible(isCard); cardBox.setManaged(isCard);
            window.sizeToScene(); 
            window.centerOnScreen();
        });

        Button btnPlace = new Button("Confirm & Place Order");
        btnPlace.setMaxWidth(Double.MAX_VALUE);
        btnPlace.setPrefHeight(45);
        btnPlace.setStyle("-fx-background-color: #00ADB5; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-background-radius: 8; -fx-cursor: hand;");
        
        // Button Animation
        Animations.animateButton(btnPlace);

        btnPlace.setOnAction(e -> {
            if (nameField.getText().isEmpty()) { Animations.shake(nameField); return; }
            if (addressField.getText().isEmpty()) { Animations.shake(addressField); return; }

            if (rbCard.isSelected()) {
                if (cardNum.getText().length() != 16 || cardCvc.getText().length() != 3 || datePicker.getValue() == null) {
                     new Alert(Alert.AlertType.ERROR, "Invalid Card Details!").show();
                     return;
                }
            }
            
            boolean isCard = rbCard.isSelected();
            double taxRate = isCard ? 0.05 : 0.15;
            double taxAmount = subtotal * taxRate;
            double finalTotal = subtotal + taxAmount;
            String method = isCard ? "Card" : "Cash";
            
            Order newOrder = new Order(nameField.getText(), addressField.getText(), method, finalTotal, cart.getList());
            OrderManager.placeOrder(newOrder);

            window.close(); 
            onSuccess.accept(method); 
        });

        layout.getChildren().addAll(header, nameField, phoneField, addressField, payLabel, rbCash, rbCard, cardBox, btnPlace);
        
        Animations.popIn(layout);
        window.setScene(new Scene(layout));
        window.showAndWait();
    }

    
    private static void styleField(TextField tf) {
        String defaultStyle = "-fx-background-color: #393E46; -fx-text-fill: white; -fx-prompt-text-fill: #a0a0a0; -fx-background-radius: 5; -fx-border-color: #555; -fx-border-radius: 5; -fx-padding: 10;";
        String focusStyle = "-fx-background-color: #393E46; -fx-text-fill: white; -fx-prompt-text-fill: #a0a0a0; -fx-background-radius: 5; -fx-border-color: #00ADB5; -fx-border-radius: 5; -fx-padding: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,173,181,0.3), 5, 0, 0, 0);";
        
        tf.setStyle(defaultStyle);
        
        // Change Border Color when user clicks inside
        tf.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) tf.setStyle(focusStyle);
            else tf.setStyle(defaultStyle);
        });
    }

    //  TEXT AREA STYLING
    private static void styleArea(TextArea ta) {
        String baseStyle = "-fx-control-inner-background: #393E46; -fx-background-color: #393E46; -fx-text-fill: white; -fx-prompt-text-fill: #a0a0a0; -fx-background-radius: 5; -fx-border-radius: 5; -fx-padding: 5;";
        
        // CHANGE 1: Make default border transparent or very dark
        String defaultBorder = "-fx-border-color: transparent;"; 
        
        // CHANGE 2: Keep the Teal Glow when clicked
        String focusBorder = "-fx-border-color: #00ADB5; -fx-effect: dropshadow(three-pass-box, rgba(0,173,181,0.3), 5, 0, 0, 0);";

        ta.setStyle(baseStyle + defaultBorder);
        
        ta.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) ta.setStyle(baseStyle + focusBorder);
            else ta.setStyle(baseStyle + defaultBorder);
        });
    }

    private static void applyNumericLimit(TextField field, int maxLength) {
        field.textProperty().addListener((obs, old, newVal) -> {
            if (!newVal.matches("\\d*")) field.setText(newVal.replaceAll("[^\\d]", ""));
            if (field.getText().length() > maxLength) field.setText(field.getText().substring(0, maxLength));
        });
    }
}