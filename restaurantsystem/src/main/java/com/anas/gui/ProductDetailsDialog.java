package com.anas.gui;

import com.anas.logic.OrderCart;
import com.anas.models.Food;
import com.anas.models.MenuItem;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ProductDetailsDialog {

    // Helper to calculate price based on size
    private static double currentPrice = 0.0;

    public static void display(MenuItem item, OrderCart cart, Runnable onAdd) {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(item.getItemName());

        VBox layout = new VBox(20);
        layout.setPadding(new Insets(30));
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #2D3447;");

        // 1. BIG IMAGE
        ImageView imgView = new ImageView();
        try {
            String imgName = "/images/" + item.getItemName().replaceAll(" ", "") + ".jpg";
            Image img = new Image(ProductDetailsDialog.class.getResourceAsStream(imgName), 200, 150, true, true);
            imgView.setImage(img);
        } catch (Exception e) { 
            // Fallback
        }

        // 2. TITLE & CAPTION
        Label nameLbl = new Label(item.getItemName());
        nameLbl.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold;");

        Label captionLbl = new Label(getDescription(item.getItemName()));
        captionLbl.setStyle("-fx-text-fill: #a0a0a0; -fx-font-style: italic;");
        captionLbl.setMaxWidth(350);

        // 3. SIZE SELECTOR (Radio Buttons)
        VBox sizeBox = new VBox(10);
        sizeBox.setAlignment(Pos.CENTER_LEFT);
        
        Label sizeTitle = new Label("Select Size:");
        sizeTitle.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        
        ToggleGroup sizeGroup = new ToggleGroup();
        
        RadioButton rbSmall = new RadioButton("Small (Base Price)");
        RadioButton rbMedium = new RadioButton("Medium (+Rs. 200)");
        RadioButton rbLarge = new RadioButton("Large (+Rs. 400)");
        
        styleRadio(rbSmall); styleRadio(rbMedium); styleRadio(rbLarge);
        rbSmall.setToggleGroup(sizeGroup);
        rbMedium.setToggleGroup(sizeGroup);
        rbLarge.setToggleGroup(sizeGroup);
        rbSmall.setSelected(true); // Default

        sizeBox.getChildren().addAll(sizeTitle, rbSmall, rbMedium, rbLarge);

        // 4. LIVE PRICE UPDATE
        Label priceLbl = new Label("Total: Rs. " + item.computeFinalCost());
        priceLbl.setStyle("-fx-text-fill: #E58E26; -fx-font-size: 22px; -fx-font-weight: bold;");

        // Logic to update price when size changes
        currentPrice = item.getBaseCost(); // Reset
        
        sizeGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == rbSmall) currentPrice = item.getBaseCost();
            else if (newVal == rbMedium) currentPrice = item.getBaseCost() + 200;
            else if (newVal == rbLarge) currentPrice = item.getBaseCost() + 400;
            
            priceLbl.setText("Total: Rs. " + currentPrice);
        });

        // 5. ADD TO CART BUTTON
        Button btnAdd = new Button("Add to Cart");
        btnAdd.setStyle("-fx-background-color: #00ADB5; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px; -fx-cursor: hand;");
        btnAdd.setPrefWidth(200);
        
        btnAdd.setOnAction(e -> {
            // Create a NEW item based on selection (Polymorphism trick)
            String sizeName = ((RadioButton)sizeGroup.getSelectedToggle()).getText().split("\\(")[0].trim();
            String newName = item.getItemName() + " (" + sizeName + ")";
            
            // Create a specific Food object with the NEW calculated price
            Food customItem = new Food(newName, currentPrice);
            
            cart.addItem(customItem);
            onAdd.run(); // Refresh the main menu UI
            window.close();
        });

        layout.getChildren().addAll(imgView, nameLbl, captionLbl, sizeBox, priceLbl, btnAdd);
        Animations.popIn(layout);
        
        Scene scene = new Scene(layout, 400, 600);
        window.setScene(scene);
        window.showAndWait();
    }
    
    private static void styleRadio(RadioButton rb) {
        rb.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
    }

    // Helper: Returns a custom description for each food item
    private static String getDescription(String itemName) {
    String name = itemName.toLowerCase();
    
    if (name.contains("zinger")) return "Crispy chicken fillet with spicy mayo & fresh lettuce.";
    if (name.contains("beef")) return "Double juicy beef patty with melted cheese.";
    if (name.contains("cheese pizza")) return "Loaded with Mozzarella and our secret sauce.";
    if (name.contains("pepperoni")) return "Classic NY style with crispy pepperoni slices.";
    if (name.contains("pasta")) return "Creamy white sauce with grilled chicken chunks.";
    if (name.contains("cola") || name.contains("coke")) return "Chilled refreshing cola.";
    if (name.contains("shake")) return "Thick shake blended with real Oreo crumbs.";
    if (name.contains("water")) return "Pure mineral water.";
    
    return "Freshly prepared with high quality ingredients."; // Default fallback
}
}