// package com.anas;

// import javafx.application.Application;
// import javafx.geometry.Pos;
// import javafx.scene.Scene;
// import javafx.scene.control.Button;
// import javafx.scene.control.Label;
// import javafx.scene.control.TextField;
// import javafx.scene.input.KeyCode; // <--- NEW IMPORT
// import javafx.scene.layout.HBox;
// import javafx.scene.layout.VBox;
// import javafx.scene.paint.Color;
// import javafx.scene.text.Font;
// import javafx.scene.text.FontWeight;
// import javafx.stage.Stage;

// public class App extends Application {

//     // We define these variables UP HERE (Class Level) 
//     // so all methods can see them (Button, Label, TextField)
//     private TextField nameinput;
//     private Label resultLabel;

//     @Override
//     public void start(Stage primaryStage) {

//         // 1. Controls
//         Label instructionLabel = new Label("Enter your name: ");
//         instructionLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

//         nameinput = new TextField(); // Initialize the variable
//         nameinput.setMaxWidth(200);
//         nameinput.setPromptText("Type name here...");
//         nameinput.setStyle("-fx-border-color: #2196F3; -fx-border-radius: 5px;");

//         Button greetButton = new Button("Say Hello");
//         Button clearButton = new Button("Clear");

//         resultLabel = new Label(); // Initialize the variable
//         resultLabel.setFont(new Font("Arial", 24));

//         // Styling Buttons
//         greetButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-background-radius: 10px;");
//         clearButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-background-radius: 10px;");

//         // --- 2. LOGIC (The New Part) ---

//         // Event A: User Clicks the Button
//         greetButton.setOnAction(e -> {
//             greetUser(); // Call our helper method
//         });

//         // Event B: User Presses a Key inside the Text Box
//         nameinput.setOnKeyPressed(e -> {
//             // Check: Was the key pressed "Enter"?
//             if (e.getCode() == KeyCode.ENTER) {
//                 greetUser(); // Call the SAME helper method
//             } else if (e.getCode() == KeyCode.ESCAPE) {
//                 clearFields(); // Call the clear method
//             }
//         });

//         clearButton.setOnAction(e -> {
//             clearFields();
//             nameinput.requestFocus(); 
//         });

//         // 3. Layout
//         HBox buttonBox = new HBox(15); 
//         buttonBox.setAlignment(Pos.CENTER);
//         buttonBox.getChildren().addAll(greetButton, clearButton);

//         VBox root = new VBox(20); 
//         root.setAlignment(Pos.CENTER);
//         root.getChildren().addAll(instructionLabel, nameinput, buttonBox, resultLabel);
//         root.setStyle("-fx-background-color: #E3F2FD; -fx-padding: 20px;");

//         Scene scene = new Scene(root, 400, 300);
//         primaryStage.setTitle("Smart App");
//         primaryStage.setScene(scene);
//         primaryStage.show();
//     }

//     // --- HELPER METHOD ---
//     // We moved the logic here so we can reuse it!
//     private void greetUser() {
//         String name = nameinput.getText();
//         if(name.isEmpty()) {
//             resultLabel.setText("Please type a name!");
//             resultLabel.setTextFill(Color.RED);
//         } else {
//             resultLabel.setText("Hello, " + name + "!");
//             resultLabel.setTextFill(Color.web("#2196F3"));
//         }
//     }

//     private void clearFields() {
//         nameinput.setText("");
//         resultLabel.setText("");
//         nameinput.requestFocus(); 
//     }

//     public static void main(String[] args) {
//         launch(args);
//     }
// }

// package com.anas;

// import java.util.Optional;

// import javafx.application.Application;
// import javafx.geometry.Insets;
// import javafx.geometry.Pos; // <--- NEW: For padding
// import javafx.scene.Scene;
// import javafx.scene.control.Alert;
// import javafx.scene.control.Button; // Imports everything (Alert, Button, etc.)
// import javafx.scene.control.ButtonType;
// import javafx.scene.control.ListView;
// import javafx.scene.control.Menu; // <--- NEW: The Pro Layout
// import javafx.scene.control.MenuBar;
// import javafx.scene.control.MenuItem;
// import javafx.scene.control.TextField;
// import javafx.scene.input.KeyCode;
// import javafx.scene.layout.BorderPane;
// import javafx.scene.layout.HBox;
// import javafx.stage.Stage;

// public class App extends Application {

//     private TextField inputField;
//     private ListView<String> myListView;

//     @Override
//     public void start(Stage primaryStage) {

//         // --- 1. TOP ZONE: The Menu Bar ---
//         MenuBar menuBar = new MenuBar();

//         // Menu 1: File
//         Menu fileMenu = new Menu("File");
//         MenuItem exitItem = new MenuItem("Exit");
//         exitItem.setOnAction(e -> primaryStage.close()); // Close the window
//         fileMenu.getItems().add(exitItem);

//         // Menu 2: Edit
//         Menu editMenu = new Menu("Edit");
//         MenuItem clearItem = new MenuItem("Clear List");
//         clearItem.setOnAction(e -> clearAllItems()); // Reuse your method!
//         editMenu.getItems().add(clearItem);

//         Menu helpMenu = new Menu("Help");
//         MenuItem about = new MenuItem("About");
//         about.setOnAction(e -> {
//             Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
//             infoAlert.setHeaderText("About this App");
//             infoAlert.setContentText("Created by Muhammad Anas learning JavaFX");
//             infoAlert.showAndWait();
//         });
//         helpMenu.getItems().add(about);
//         // Add menus to the bar
//         menuBar.getMenus().addAll(fileMenu, editMenu, helpMenu);

//         // --- 2. CENTER ZONE: The List ---
//         myListView = new ListView<>();
//         // In a BorderPane, the Center automatically stretches! 
//         // We don't need setMaxHeight anymore.

//         // --- 3. BOTTOM ZONE: The Inputs ---
//         inputField = new TextField();
//         inputField.setPromptText("Item name...");

//         Button addButton = new Button("Add");
//         addButton.setOnAction(e -> addItem());

//         Button removeButton = new Button("Remove");
//         removeButton.setOnAction(e -> deleteItem());

//         // Group the bottom controls in an HBox
//         HBox bottomControls = new HBox(10); // 10px spacing
//         bottomControls.setPadding(new Insets(10)); // Add padding around the box
//         bottomControls.setAlignment(Pos.CENTER);
//         bottomControls.getChildren().addAll(inputField, addButton, removeButton);

//         // --- 4. THE ROOT LAYOUT (BorderPane) ---
//         BorderPane root = new BorderPane();

//         root.setTop(menuBar);        // Put Menu at the Top
//         root.setCenter(myListView);  // Put List in the Center
//         root.setBottom(bottomControls); // Put Controls at the Bottom

//         // --- 5. Scene ---
//         // Input logic (Enter key)
//         inputField.setOnKeyPressed(e -> {
//             if (e.getCode() == KeyCode.ENTER) addItem();
//         });

//         Scene scene = new Scene(root, 400, 400);
//         primaryStage.setTitle("Pro List App");
//         primaryStage.setScene(scene);
//         primaryStage.show();
//     }

//     // --- Helper Methods (Keep your existing ones!) ---

//     private void addItem() {
//         String text = inputField.getText();
//         if (!text.isEmpty()) {
//             myListView.getItems().add(text);
//             inputField.clear();
//         }
//     }

//     private void deleteItem() {
//         String selectedItem = myListView.getSelectionModel().getSelectedItem();
//         if (selectedItem != null) {
//         // ... Your existing Confirmation code is here ...
//         Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
//         // ... (keep your existing code) ...
//         Optional<ButtonType> result = alert.showAndWait();
//         if (result.isPresent() && result.get() == ButtonType.OK) {
//             myListView.getItems().remove(selectedItem);
//         }

//     } else { 
//         // <--- NEW PART: The Error Popup
//         Alert errorAlert = new Alert(Alert.AlertType.ERROR);
//         errorAlert.setTitle("Error");
//         errorAlert.setHeaderText("No Item Selected");
//         errorAlert.setContentText("Please select an item from the list to delete.");
//         errorAlert.showAndWait();
//     }
// }

//     private void clearAllItems() {
//         Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
//     alert.setTitle("Delete Everything?");
//     alert.setHeaderText("You are about to clear the entire list.");
//     alert.setContentText("Are you sure you want to do this?");

//     // 2. Show the Alert and Wait for a response
//     // The program "pauses" here until the user clicks a button
//     Optional<ButtonType> result = alert.showAndWait();

//     // 3. Check the result
//     if (result.isPresent() && result.get() == ButtonType.OK) {
//         // Only run this if they clicked OK
//         myListView.getItems().clear();
//     }
//     }
//     public static void main(String[] args) {
//         launch(args);
//     }

// }

// package com.anas;

// import java.util.Optional;

// import javafx.application.Application;
// import javafx.geometry.Insets;
// import javafx.geometry.Pos;
// import javafx.scene.Scene;
// import javafx.scene.control.Alert;
// import javafx.scene.control.Button;
// import javafx.scene.control.ButtonType;
// import javafx.scene.control.Label;
// import javafx.scene.control.ListView;
// import javafx.scene.control.Menu;
// import javafx.scene.control.MenuBar;
// import javafx.scene.control.MenuItem;
// import javafx.scene.control.PasswordField;
// import javafx.scene.control.TextArea;
// import javafx.scene.control.TextField;
// import javafx.scene.image.Image;
// import javafx.scene.image.ImageView;
// import javafx.scene.layout.BorderPane;
// import javafx.scene.layout.VBox;
// import javafx.stage.Stage;

// public class App extends Application {

//     // We keep track of the Stage so we can swap scenes anywhere
//     private Stage window;

//     // We store the two scenes here
//     private Scene loginScene;
//     private Scene mainScene;
//     private Label totalLabel;

//     // Data for the main app
//     private ListView<Item> myListView;
//     private TextField inputField;

//     @Override
//     public void start(Stage primaryStage) {
//         window = primaryStage;
//         window.setTitle("My Application");

//         // 1. Build the two screens
//         createLoginScene();
//         createMainScene();

//         // 2. Start with the Login Screen
//         window.setScene(loginScene);
//         window.show();
//     }

//     // --- SCREEN 1: THE LOGIN PAGE ---
//     private void createLoginScene() {
//         Label welcomeLabel = new Label("Welcome! Please Login.");
//         welcomeLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

//         TextField userField = new TextField();
//         userField.setPromptText("Username");
//         userField.setMaxWidth(200);

//         PasswordField passField = new PasswordField(); // <--- NEW: Hides text with dots
//         passField.setPromptText("Password");
//         passField.setMaxWidth(200);

//         Button loginButton = new Button("Login");
//         Label errorLabel = new Label(); // To show "Wrong password"
//         errorLabel.setStyle("-fx-text-fill: red;");

//         loginButton.setOnAction(e -> {
//             // SIMULATED LOGIN LOGIC
//             String user = userField.getText();
//             String pass = passField.getText();

//             // Check if password is "1234" (Simple test)
//             if (user.equals("admin") && pass.equals("root")) {
//                 Alert info = new Alert(Alert.AlertType.INFORMATION);
//                 info.setHeaderText("Welcome Administrator");
//                 info.setContentText("You have Administrator access.");
//                 info.showAndWait();
//                 // SUCCESS: Switch to the Main Scene!
//                 window.setTitle("Restaurant System (ADMIN)");
//                 window.setScene(mainScene);
//                 errorLabel.setText(""); // Clear errors
//                 passField.clear(); // Clear password for security
//             } else if (pass.equals("1234")) {
//                 window.setTitle("Restaurant System - " + user);
//                 window.setScene(mainScene);
//                 errorLabel.setText("");
//                 passField.clear();
//             }

//             else {
//                 errorLabel.setText("Invalid Password! (Try 1234)");
//             }
//         });

//         VBox layout = new VBox(15);
//         layout.setAlignment(Pos.CENTER);
//         layout.getChildren().addAll(welcomeLabel, userField, passField, loginButton, errorLabel);

//         // Save this layout into the 'loginScene' variable
//         loginScene = new Scene(layout, 300, 250);
//     }

//     // --- SCREEN 2: YOUR MAIN APP (Refactored) ---
//     private void createMainScene() {
//         MenuBar menuBar = new MenuBar();
//         Menu fileMenu = new Menu("System");
//         MenuItem logoutItem = new MenuItem("Logout");
//         logoutItem.setOnAction(e -> window.setScene(loginScene));
//         fileMenu.getItems().add(logoutItem);
//         menuBar.getMenus().add(fileMenu);

//         myListView = new ListView<>();
//         Label orderLabel = new Label("Current Order");
//         orderLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

//         Button removeButton = new Button("Remove Item");
//         removeButton.setOnAction(e -> deleteItem());

//         totalLabel = new Label("Total: $0");
//         totalLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

//         Button btnCheckout = new Button("Checkout / Print Receipt");
//         btnCheckout.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
//         btnCheckout.setMaxWidth(Double.MAX_VALUE);
//         btnCheckout.setOnAction(e -> showReceipt());

//         VBox orderPane = new VBox(10);
//         orderPane.setPadding(new Insets(10));
//         orderPane.getChildren().addAll(orderLabel, myListView, removeButton, totalLabel, btnCheckout);

//         // --- MENU BUTTONS ---

//         javafx.scene.layout.GridPane menuGrid = new javafx.scene.layout.GridPane();
//         menuGrid.setHgap(10);
//         menuGrid.setVgap(10);
//         menuGrid.setPadding(new Insets(20));

//         String burgerUrl = "https://cdn-icons-png.flaticon.com/512/3075/3075977.png";
//         ImageView burgerIcon = new ImageView(new Image(burgerUrl));
//         burgerIcon.setFitWidth(40);
//         burgerIcon.setPreserveRatio(true);
//         Button btnBurger = new Button("Burger ($5)", burgerIcon);
//         btnBurger.setContentDisplay(javafx.scene.control.ContentDisplay.TOP);

//         String pizzaUrl = "https://cdn-icons-png.flaticon.com/512/1404/1404945.png";
//         ImageView pizzaIcon = new ImageView(new Image(pizzaUrl));
//         pizzaIcon.setFitWidth(40);
//         pizzaIcon.setPreserveRatio(true);
//         Button btnPizza = new Button("Pizza ($8)", pizzaIcon);
//         btnPizza.setContentDisplay(javafx.scene.control.ContentDisplay.TOP);

//         String colaUrl = "https://cdn-icons-png.flaticon.com/512/2405/2405479.png";
//         ImageView colaIcon = new ImageView(new Image(colaUrl));
//         colaIcon.setFitWidth(40);
//         colaIcon.setPreserveRatio(true);
//         Button btnCola = new Button("Cola ($2)", colaIcon);
//         btnCola.setContentDisplay(javafx.scene.control.ContentDisplay.TOP);

//         String friesUrl = "https://cdn-icons-png.flaticon.com/512/1046/1046786.png";
//         ImageView friesIcon = new ImageView(new Image(friesUrl));
//         friesIcon.setFitWidth(40);
//         friesIcon.setPreserveRatio(true);
//         Button btnFries = new Button("Fries ($3)", friesIcon);
//         btnFries.setContentDisplay(javafx.scene.control.ContentDisplay.TOP);

//         String buttonStyle = "-fx-font-size: 14px; -fx-min-width: 100px; -fx-min-height: 50px;";
//         btnBurger.setStyle(buttonStyle + "-fx-background-color: #FF9800;"); // Orange
//         btnPizza.setStyle(buttonStyle + "-fx-background-color: #F44336;"); // Red
//         btnCola.setStyle(buttonStyle + "-fx-background-color: #795548; -fx-text-fill: white;"); // Brown
//         btnFries.setStyle(buttonStyle + "-fx-background-color: #FFEB3B;"); // Yellow

//         btnBurger.setOnAction(e -> {
//             myListView.getItems().add(new Item("Burger", 5));
//             totalUpdate();
//         });
//         btnPizza.setOnAction(e -> {
//             myListView.getItems().add(new Item("Pizza", 8));
//             totalUpdate();
//         });
//         btnCola.setOnAction(e -> {
//             myListView.getItems().add(new Item("Cola", 2));
//             totalUpdate();
//         });
//         btnFries.setOnAction(e -> {
//             myListView.getItems().add(new Item("Fries", 3));
//             totalUpdate();
//         });

//         menuGrid.add(btnBurger, 0, 0);
//         menuGrid.add(btnPizza, 1, 0);
//         menuGrid.add(btnCola, 0, 1);
//         menuGrid.add(btnFries, 1, 1);

//         BorderPane root = new BorderPane();
//         root.setTop(menuBar);
//         root.setCenter((menuGrid));
//         root.setRight(orderPane);

//         mainScene = new Scene(root, 500, 400);

//     }
//     // --- HELPER METHODS ---

//     private void showReceipt() {
//         StringBuilder receiptText = new StringBuilder();
//         receiptText.append("********** RECEIPT **********\n");
//         receiptText.append("      Restaurant System      \n");
//         receiptText.append("*****************************\n\n");

//         int total = 0;
//         for (Item item : myListView.getItems()) {
//             receiptText.append(item.name).append("\t\t\t$").append(item.price).append("\n");
//             total += item.price;
//         }

//         receiptText.append("\n-----------------------------\n");
//         receiptText.append("TOTAL:\t\t\t$").append(total).append("\n");
//         receiptText.append("*****************************\n");
//         receiptText.append("   Thank you for dining!   ");

//         Stage receiptStage = new Stage();
//         receiptStage.setTitle("Print Receipt");

//         TextArea display = new TextArea(receiptText.toString());
//         display.setEditable(false);
//         display.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 12px;");

//         Scene scene = new Scene(display, 300, 400);
//         receiptStage.setScene(scene);
//         receiptStage.show();

//         myListView.getItems().clear();
//         totalUpdate();

//     }

//     private void totalUpdate() {
//         int total = 0;
//         for (Item item : myListView.getItems()) {
//             total += item.price;
//         }
//         totalLabel.setText("Total: $" + total);
//     }
//     // removed unused addItem() which was referenced only in commented code

//     private void deleteItem() {
//         Item selectedItem = myListView.getSelectionModel().getSelectedItem();
//         if (selectedItem != null) {
//             Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
//             alert.setHeaderText("Delete Item?");
//             alert.setContentText("Are you sure?");
//             Optional<ButtonType> result = alert.showAndWait();
//             if (result.isPresent() && result.get() == ButtonType.OK) {
//                 myListView.getItems().remove(selectedItem);
//                 totalUpdate();
//             }
//         } else {
//             Alert errorAlert = new Alert(Alert.AlertType.ERROR);
//             errorAlert.setHeaderText("No Selection");
//             errorAlert.setContentText("Please select an item.");
//             errorAlert.showAndWait();
//         }
//     }

//     public static void main(String[] args) {
//         launch(args);
//     }
// }

// // A simple blueprint for a Menu Item
// class Item {
//     String name;
//     int price;

//     public Item(String name, int price) {
//         this.name = name;
//         this.price = price;
//     }

//     // This is what the ListView shows to the user
//     @Override
//     public String toString() {
//         return name + " - $" + price;
//     }
// }

// package com.anas;

// import javafx.animation.FadeTransition;
// import javafx.animation.ScaleTransition;
// import javafx.application.Application;
// import javafx.geometry.Insets;
// import javafx.geometry.Pos;
// import javafx.scene.Scene;
// import javafx.scene.control.Alert;
// import javafx.scene.control.Button;
// import javafx.scene.control.Label;
// import javafx.scene.control.ListView;
// import javafx.scene.control.ScrollPane;
// import javafx.scene.control.Separator;
// import javafx.scene.image.Image;
// import javafx.scene.image.ImageView;
// import javafx.scene.layout.BorderPane;
// import javafx.scene.layout.FlowPane;
// import javafx.scene.layout.HBox;
// import javafx.scene.layout.Priority;
// import javafx.scene.layout.Region;
// import javafx.scene.layout.VBox;
// import javafx.scene.text.Font;
// import javafx.scene.text.FontWeight;
// import javafx.stage.Stage;
// import javafx.util.Duration;

// public class App extends Application {

// private ListView<Item> myListView;
// private Label totalLabel;
// private FlowPane foodGrid;

// @Override
// public void start(Stage primaryStage) {

// // --- 1. SIDEBAR ---
// VBox sidebar = new VBox(15);
// sidebar.setPadding(new Insets(20));
// sidebar.setStyle("-fx-background-color: white; -fx-effect:
// dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");
// sidebar.setPrefWidth(220);

// Label logo = new Label("⚡ FastFood");
// logo.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill:
// #FF5722;");

// Button btnBurgers = createCategoryButton("🍔 Burgers", e -> loadBurgers());
// Button btnPizza = createCategoryButton("🍕 Pizza", e -> loadPizza());
// Button btnDrinks = createCategoryButton("🥤 Drinks", e -> loadDrinks());

// sidebar.getChildren().addAll(logo, new Separator(), btnBurgers, btnPizza,
// btnDrinks);

// // --- 2. FOOD GRID (Scrollable) ---
// foodGrid = new FlowPane();
// foodGrid.setPadding(new Insets(20));
// foodGrid.setHgap(20);
// foodGrid.setVgap(20);
// foodGrid.setStyle("-fx-background-color: #F9F9F9;");

// ScrollPane scrollPane = new ScrollPane(foodGrid);
// scrollPane.setFitToWidth(true); // Ensures grid stretches
// scrollPane.setStyle("-fx-background: #F9F9F9; -fx-border-color:
// transparent;");

// // --- 3. CART (Right Side) ---
// VBox cartPane = new VBox(15);
// cartPane.setPadding(new Insets(20));
// cartPane.setPrefWidth(320);
// cartPane.setStyle("-fx-background-color: white;");

// Label cartTitle = new Label("Your Order");
// cartTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));

// myListView = new ListView<>();
// VBox.setVgrow(myListView, Priority.ALWAYS);

// totalLabel = new Label("Total: $0");
// totalLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));

// Button checkoutBtn = new Button("Checkout");
// checkoutBtn.setMaxWidth(Double.MAX_VALUE);
// checkoutBtn.setStyle("-fx-background-color: #FF5722; -fx-text-fill: white;
// -fx-font-weight: bold; -fx-padding: 15; -fx-background-radius: 8; -fx-cursor:
// hand;");
// checkoutBtn.setOnAction(e -> {
// myListView.getItems().clear();
// updateTotal();
// Alert alert = new Alert(Alert.AlertType.INFORMATION, "Order Placed!");
// alert.show();
// });

// cartPane.getChildren().addAll(cartTitle, myListView, totalLabel,
// checkoutBtn);

// // --- LAYOUT ---
// BorderPane root = new BorderPane();
// root.setLeft(sidebar);
// root.setCenter(scrollPane);
// root.setRight(cartPane);

// Scene scene = new Scene(root, 1100, 700);
// primaryStage.setTitle("High Performance Food App");
// primaryStage.setScene(scene);
// primaryStage.show();

// // Load default data
// loadBurgers();
// }

// // --- ANIMATED CARD CREATOR ---
// private VBox createFoodCard(String name, String desc, int price, String
// imageUrl) {
// VBox card = new VBox(10);
// card.setPadding(new Insets(15));
// card.setPrefWidth(220);
// card.setStyle("-fx-background-color: white; -fx-background-radius: 15;
// -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");

// // 1. ASYNC IMAGE LOADING (Fixes Lag!)
// // The 'true' at the end means "Load in background"
// Image image = new Image(imageUrl, true);
// ImageView imgView = new ImageView(image);
// imgView.setFitWidth(190);
// imgView.setFitHeight(120);
// imgView.setPreserveRatio(false); // Fill the box

// // Clip Image (Round Corners)
// javafx.scene.shape.Rectangle clip = new javafx.scene.shape.Rectangle(190,
// 120);
// clip.setArcWidth(15);
// clip.setArcHeight(15);
// imgView.setClip(clip);

// // 2. Labels
// Label nameLbl = new Label(name);
// nameLbl.setFont(Font.font("Arial", FontWeight.BOLD, 15));

// Label descLbl = new Label(desc);
// descLbl.setWrapText(true);
// descLbl.setStyle("-fx-text-fill: gray; -fx-font-size: 11px;");

// Button addBtn = new Button("Add +");
// addBtn.setStyle("-fx-background-color: #FF5722; -fx-text-fill: white;
// -fx-background-radius: 20; -fx-cursor: hand;");
// addBtn.setOnAction(e -> {
// myListView.getItems().add(new Item(name, price));
// updateTotal();
// });

// HBox bottom = new HBox(10, new Label("$" + price), new Region(), addBtn);
// HBox.setHgrow(bottom.getChildren().get(1), Priority.ALWAYS); // Spacer
// bottom.setAlignment(Pos.CENTER_LEFT);

// card.getChildren().addAll(imgView, nameLbl, descLbl, bottom);

// // --- ANIMATIONS ---

// // A. Fade In when created
// FadeTransition fade = new FadeTransition(Duration.millis(500), card);
// fade.setFromValue(0);
// fade.setToValue(1);
// fade.play();

// // B. Scale Up on Hover
// card.setOnMouseEntered(e -> {
// card.setStyle("-fx-background-color: white; -fx-background-radius: 15;
// -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 5);");
// ScaleTransition scale = new ScaleTransition(Duration.millis(200), card);
// scale.setToX(1.05);
// scale.setToY(1.05);
// scale.play();
// });

// card.setOnMouseExited(e -> {
// card.setStyle("-fx-background-color: white; -fx-background-radius: 15;
// -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");
// ScaleTransition scale = new ScaleTransition(Duration.millis(200), card);
// scale.setToX(1.0);
// scale.setToY(1.0);
// scale.play();
// });

// return card;
// }

// // --- CATEGORY LOADING ---
// private void loadBurgers() {
// foodGrid.getChildren().clear();
// // Using reliable placeholder images
// foodGrid.getChildren().add(createFoodCard("Classic Burger", "Beef, Lettuce,
// Tomato", 8, "https://cdn-icons-png.flaticon.com/512/3075/3075977.png"));
// foodGrid.getChildren().add(createFoodCard("Cheese Burger", "Double Cheese",
// 10, "https://cdn-icons-png.flaticon.com/512/3075/3075929.png"));
// foodGrid.getChildren().add(createFoodCard("Chicken Spicy", "Fried Chicken,
// Mayo", 9, "https://cdn-icons-png.flaticon.com/512/1046/1046751.png"));
// }

// private void loadPizza() {
// foodGrid.getChildren().clear();
// foodGrid.getChildren().add(createFoodCard("Pepperoni", "Spicy Slices", 12,
// "https://cdn-icons-png.flaticon.com/512/1404/1404945.png"));
// foodGrid.getChildren().add(createFoodCard("Veggie", "Mushrooms, Peppers", 10,
// "https://cdn-icons-png.flaticon.com/512/3132/3132693.png"));
// }

// private void loadDrinks() {
// foodGrid.getChildren().clear();
// foodGrid.getChildren().add(createFoodCard("Cola", "Zero Sugar", 3,
// "https://cdn-icons-png.flaticon.com/512/2405/2405479.png"));
// foodGrid.getChildren().add(createFoodCard("Orange Juice", "Fresh Squeezed",
// 4, "https://cdn-icons-png.flaticon.com/512/2405/2405555.png"));
// }

// // --- HELPERS ---
// private Button createCategoryButton(String text,
// javafx.event.EventHandler<javafx.event.ActionEvent> action) {
// Button btn = new Button(text);
// btn.setMaxWidth(Double.MAX_VALUE);
// btn.setStyle("-fx-background-color: transparent; -fx-alignment: CENTER_LEFT;
// -fx-font-size: 16px; -fx-cursor: hand;");
// btn.setOnAction(action);
// btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #FFF3E0;
// -fx-alignment: CENTER_LEFT; -fx-font-size: 16px; -fx-cursor: hand;"));
// btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: transparent;
// -fx-alignment: CENTER_LEFT; -fx-font-size: 16px;"));
// return btn;
// }

// private void updateTotal() {
// int total = 0;
// for (Item item : myListView.getItems()) total += item.price;
// totalLabel.setText("Total: $" + total);
// }

// public static void main(String[] args) { launch(args); }
// }

// class Item {
// String name; int price;
// public Item(String name, int price) { this.name = name; this.price = price; }
// @Override public String toString() { return name + " - $" + price; }
// }
