package com.anas.gui;

import java.util.function.Consumer;

import com.anas.models.Drink;
import com.anas.models.Food;
import com.anas.models.MenuItem;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AddItemDialog {

    public static void display(Consumer<MenuItem> onAdd) {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Add New Menu Item");
        window.setMinWidth(300);

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER_LEFT);
        layout.setStyle("-fx-background-color: #2D3447;");

        Label header = new Label("New Item Details");
        header.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");

        // Inputs
        TextField nameField = new TextField();
        nameField.setPromptText("Item Name (e.g. BBQ Pizza)");
        styleField(nameField);

        TextField priceField = new TextField();
        priceField.setPromptText("Price (e.g. 500)");
        styleField(priceField);

        // Type Selection
        Label typeLbl = new Label("Item Type:");
        typeLbl.setStyle("-fx-text-fill: #a0a0a0;");
        
        ToggleGroup group = new ToggleGroup();
        RadioButton rbFood = new RadioButton("Food");
        rbFood.setStyle("-fx-text-fill: white;");
        rbFood.setToggleGroup(group);
        rbFood.setSelected(true);

        RadioButton rbDrink = new RadioButton("Drink");
        rbDrink.setStyle("-fx-text-fill: white;");
        rbDrink.setToggleGroup(group);

        Button btnAdd = new Button("Add Item");
        btnAdd.setMaxWidth(Double.MAX_VALUE);
        btnAdd.setStyle("-fx-background-color: #00ADB5; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");

        btnAdd.setOnAction(e -> {
            // Validation
            if (nameField.getText().isEmpty() || priceField.getText().isEmpty()) {
                new Alert(Alert.AlertType.ERROR, "Please enter name and price!").show();
                return;
            }

            try {
                String name = nameField.getText();
                double price = Double.parseDouble(priceField.getText());
                boolean isDrink = rbDrink.isSelected();

                // Create the correct object
                MenuItem newItem;
                if (isDrink) {
                    newItem = new Drink(name, price);
                } else {
                    newItem = new Food(name, price);
                }

                // Return result
                onAdd.accept(newItem);
                window.close();

            } catch (NumberFormatException ex) {
                new Alert(Alert.AlertType.ERROR, "Price must be a number!").show();
            }
        });

        layout.getChildren().addAll(header, nameField, priceField, typeLbl, rbFood, rbDrink, btnAdd);
        Animations.popIn(layout);
        
        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();
    }

    private static void styleField(TextField tf) {
        tf.setStyle("-fx-background-color: #393E46; -fx-text-fill: white; -fx-prompt-text-fill: gray;");
    }
}