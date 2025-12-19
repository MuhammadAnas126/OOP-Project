package com.anas.gui;

import com.anas.data.AdminAuth;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginView {
    private final Stage stage;
    private MenuView cachedMenuView;

    public LoginView(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        boolean isMaximized = stage.isMaximized();
        boolean isAlreadyOpen = stage.isShowing();
        
        // DEFAULT SIZE (If first time opening)
        double width = 900;
        double height = 600;

        // Get Content Size, NOT Window Size
        if (isAlreadyOpen && stage.getScene() != null) {
            width = stage.getScene().getWidth();
            height = stage.getScene().getHeight();
        }

        //  LAYOUT: GridPane for strict 50/50 Split 
        GridPane root = new GridPane();
        root.setAlignment(Pos.CENTER);

        // --- COLUMNS: 60% Image | 40% Form ---
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(60); 
        col1.setHgrow(Priority.ALWAYS);

        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(40);
        col2.setHgrow(Priority.ALWAYS);
        
        root.getColumnConstraints().addAll(col1, col2);

        // --- ROW: 100% Height ---
        RowConstraints row1 = new RowConstraints();
        row1.setPercentHeight(100);
        row1.setVgrow(Priority.ALWAYS);
        root.getRowConstraints().add(row1);

        // ============================
        // 1. LEFT SIDE: HERO IMAGE
        // ============================
        StackPane imageContainer = new StackPane(); 
        try {
            // Use BackgroundImage for "Cover" behavior
            Image img = new Image(getClass().getResourceAsStream("/images/bg.jpg"), 0, 0, false, true);
            BackgroundImage bgImage = new BackgroundImage(
                img,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(1.0, 1.0, true, true, false, false) // Cover Mode
            );
            imageContainer.setBackground(new Background(bgImage));
        } catch (Exception e) {
            imageContainer.setStyle("-fx-background-color: #000;"); 
            imageContainer.getChildren().add(new Label("Image Missing"));
        }

        // ============================
        // 2. RIGHT SIDE: THE LOGIN FORM
        // ============================
        
        // A. The Form Content (Inputs & Buttons)
        VBox form = new VBox(20); 
        form.setAlignment(Pos.CENTER);
        form.setPadding(new javafx.geometry.Insets(40)); 
        form.setMaxWidth(350); // KEY FIX: Prevents stretching on large screens 

        // -- Form Elements --
        Label title = new Label("Tech Bites");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 32px; -fx-font-weight: bold; -fx-font-family: 'Arial';");
        
        Label subtitle = new Label("Management System");
        subtitle.setStyle("-fx-text-fill: #a0a0a0; -fx-font-size: 14px; -fx-padding: 0 0 20 0;");

        TextField userF = new TextField();
        userF.setPromptText("Admin Username");
        styleField(userF); 

        // Password Logic
        PasswordField passF = new PasswordField();
        passF.setPromptText("Admin Password");
        styleField(passF);
        
        TextField passText = new TextField();
        passText.setPromptText("Admin Password");
        styleField(passText);
        passText.setManaged(false); 
        passText.setVisible(false); 
        passText.textProperty().bindBidirectional(passF.textProperty());
        
        String paddingFix = "-fx-padding: 10 30 10 0;";
        passF.setStyle(passF.getStyle() + paddingFix);
        passText.setStyle(passText.getStyle() + paddingFix);
        // Eye Button

        Button btnEye = new Button("👁"); 
        btnEye.setStyle(
            "-fx-background-color: transparent; " +
            "-fx-text-fill: #a0a0a0; " +
            "-fx-font-size: 14px; " +
            "-fx-cursor: hand; " +
            "-fx-focus-color: transparent; " +  
            "-fx-faint-focus-color: transparent;"
        );
        
        btnEye.setOnAction(e -> {
            if(passF.isVisible()) {
                passF.setVisible(false); passF.setManaged(false);
                passText.setVisible(true); passText.setManaged(true);
                btnEye.setStyle("-fx-background-color: transparent; -fx-text-fill: #E58E26; -fx-font-size: 14px; -fx-cursor: hand; -fx-focus-color: transparent; -fx-faint-focus-color: transparent;"); 
            } else {
                passF.setVisible(true); passF.setManaged(true);
                passText.setVisible(false); passText.setManaged(false);
                btnEye.setStyle("-fx-background-color: transparent; -fx-text-fill: #a0a0a0; -fx-font-size: 14px; -fx-cursor: hand; -fx-focus-color: transparent; -fx-faint-focus-color: transparent;"); 
            }
        });
        StackPane passContainer = new StackPane(passF, passText, btnEye);
        passContainer.setAlignment(Pos.CENTER_RIGHT);
        
        Button btnAdmin = new Button("LOGIN AS MANAGER");
        btnAdmin.setMaxWidth(Double.MAX_VALUE);
        btnAdmin.setPrefHeight(45);
        btnAdmin.setStyle("-fx-background-color: #E58E26; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 30; -fx-cursor: hand;");
        Animations.animateButton(btnAdmin);

        Separator sep = new Separator();
        sep.setOpacity(0.3);

        Button btnCustomer = new Button("Customer Order");
        btnCustomer.setMaxWidth(Double.MAX_VALUE);
        btnCustomer.setPrefHeight(45);
        btnCustomer.setStyle("-fx-background-color: transparent; -fx-text-fill: #2ecc71; -fx-border-color: #2ecc71; -fx-border-radius: 30; -fx-background-radius: 30; -fx-font-size: 14px; -fx-cursor: hand;");

        btnCustomer.setOnMouseEntered(e -> {
            // Fill background with low opacity green on hover
                btnCustomer.setStyle("-fx-background-color: rgba(46, 204, 113, 0.15); -fx-text-fill: #2ecc71; -fx-border-color: #2ecc71; -fx-border-radius: 30; -fx-background-radius: 30; -fx-font-size: 14px; -fx-cursor: hand;");
            });

        btnCustomer.setOnMouseExited(e -> {
            // Return to transparent when mouse leaves
               btnCustomer.setStyle("-fx-background-color: transparent; -fx-text-fill: #2ecc71; -fx-border-color: #2ecc71; -fx-border-radius: 30; -fx-background-radius: 30; -fx-font-size: 14px; -fx-cursor: hand;");
            });
        // Event Handling
        btnCustomer.setOnAction(e -> {
            if (cachedMenuView != null) {
                cachedMenuView.show(); // Instant!
            } else {
                // Fallback just in case it wasn't ready yet
                new MenuView(stage).show();
            }
        });
        btnAdmin.setOnAction(e -> {
            if (AdminAuth.validate(userF.getText(), passF.getText())) {
                try { new AdminDashboard(stage).show(); } 
                catch (Exception ex) { new Alert(Alert.AlertType.ERROR, "Error: " + ex.getMessage()).show(); }
            } else {
                showAlert(Alert.AlertType.ERROR, "Access Denied", "Invalid Credentials");
            }
        });

        form.getChildren().addAll(title, subtitle, userF, passContainer, btnAdmin, sep, btnCustomer);

        // B. The Centering Container
        // This StackPane ensures the 350px form stays in the MIDDLE of the right side
        StackPane formCenteringContainer = new StackPane(form);
        formCenteringContainer.setAlignment(Pos.CENTER);
        formCenteringContainer.setStyle("-fx-background-color: #2D3447;");

        
        ScrollPane scrollForm = new ScrollPane(formCenteringContainer);
        scrollForm.setFitToWidth(true);
        scrollForm.setFitToHeight(true);
        scrollForm.setStyle("-fx-background: #2D3447; -fx-background-color: #2D3447; -fx-border-color: transparent;");

        // Add to Grid (Column 0 = Image, Column 1 = Scrollable Form)
        root.add(imageContainer, 0, 0);
        root.add(scrollForm, 1, 0);
        

        Scene scene = new Scene(root, width, height); 
        try { scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm()); } catch(Exception e){}
        root.setOpacity(0);
        stage.setScene(scene);
        stage.setTitle("Tech Bites - Login");

        // If it was maximized before, keep it maximized.
        if (isAlreadyOpen) {
            stage.setWidth(stage.getWidth());
            stage.setHeight(stage.getHeight());
            stage.setMaximized(isMaximized);
        } else {
            stage.centerOnScreen();
        }

        stage.setMinWidth(750);
        stage.setMinHeight(500);
        stage.show();
        Animations.screenTransition(root);

        // 2. Load the Menu in the background immediately after Login appears
        Platform.runLater(() -> {
            try {
                // This does the heavy lifting while the user is looking at the login screen
                cachedMenuView = new MenuView(stage); 
            } catch (Exception e) {
                System.out.println("Background loading failed: " + e.getMessage());
            }
        });
    }

    private void styleField(TextField field) {
    // base style string
    String baseStyle = 
        "-fx-background-color: transparent; " +
        "-fx-border-color: #a0a0a0; " +
        "-fx-border-width: 0 0 2 0; " +
        "-fx-text-fill: white; " +
        "-fx-font-size: 14px; " +
        "-fx-padding: 10 0 10 0;"; 

    field.setStyle(baseStyle);

    // Change border color on click
    field.focusedProperty().addListener((obs, oldVal, newVal) -> {
        if (newVal) {
            // Orange Border, Keep Padding 0
            field.setStyle("-fx-background-color: transparent; -fx-border-color: #E58E26; -fx-border-width: 0 0 2 0; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 0 10 0;");
        } else {
            // Grey Border, Keep Padding 0
            field.setStyle("-fx-background-color: transparent; -fx-border-color: #a0a0a0; -fx-border-width: 0 0 2 0; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 0 10 0;");
        }
    });
}

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.show();
    }
}