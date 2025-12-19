package com.anas.gui;

import java.util.ArrayList;

import com.anas.data.FileManager;
import com.anas.logic.OrderCart;
import com.anas.logic.OrderManager;
import com.anas.models.Drink;
import com.anas.models.Food;
import com.anas.models.MenuItem;
import com.anas.models.Order;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MenuView {
    private final Stage stage;
    private final OrderCart cart = new OrderCart(); 

    // Global Lists
    private ArrayList<MenuItem> allMenuItems;
    private FlowPane menuGrid;
    
    // UI Components
    private VBox cartItemsContainer;
    private Label totalLabel;
    private Label lblTax;
    private Label lblSubtotal;

    // Store the currently selected button so we can reset it later
    private Button activeCategoryBtn = null;

    // Promo Code Components
    private Label lblDiscount;
    private TextField promoField;
    private boolean isPromoApplied = false; // State to track discount

    public MenuView(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        boolean isMaximized = stage.isMaximized();
        boolean isAlreadyOpen = stage.isShowing();
    
        // Default size for Menu
        double width = 1100;
        double height = 700;

        if (isAlreadyOpen && stage.getScene() != null) {
             width = stage.getScene().getWidth();
             height = stage.getScene().getHeight();
            }

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #222831;"); 

        // ============================
        // 1. TOP HEADER & SEARCH
        // ============================
        HBox header = new HBox(20);
        header.setPadding(new Insets(15, 30, 15, 30));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: #2D3447; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 5);");

        Label branding = new Label("Tech Bites");
        branding.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");
        
        TextField searchField = new TextField();
        searchField.setPromptText("🔍 Search...");
        searchField.setPrefWidth(300);
        searchField.setStyle("-fx-background-color: #393E46; -fx-text-fill: white; -fx-background-radius: 20; -fx-padding: 8 15 8 15;");
        searchField.textProperty().addListener((obs, old, newVal) -> filterMenuByName(newVal));

        Button btnTrack = new Button("Track Order");
        btnTrack.setFocusTraversable(false);
        btnTrack.setStyle("-fx-background-color: #393E46; -fx-text-fill: white; -fx-border-color: white; -fx-border-radius: 20;");
        Animations.animateButton(btnTrack);
        btnTrack.setOnAction(e -> showTrackerPopup());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS); 
        
        Button btnBack = new Button("Log Out");
        btnBack.setStyle("-fx-background-color: transparent; -fx-text-fill: #ff5e57; -fx-border-color: #ff5e57;");
        Animations.animateButton(btnBack);
        btnBack.setOnAction(e -> new LoginView(stage).show());

        header.getChildren().addAll(branding, searchField, btnTrack, spacer, btnBack);

        // 2. MOVING DEALS BANNER 
        StackPane bannerContainer = createMovingBanner();

        // Combine Header + Banner
        VBox topContainer = new VBox(header, bannerContainer);
        root.setTop(topContainer);

        // ============================
        // 3. LEFT CATEGORY SIDEBAR
        // ============================
        VBox categoryPanel = new VBox(15);
        categoryPanel.setPrefWidth(200);
        categoryPanel.setPadding(new Insets(20));
        categoryPanel.setStyle("-fx-background-color: #2D3447; -fx-border-width: 0 1 0 0; -fx-border-color: #555;");
        
        Label catTitle = new Label("Categories");
        catTitle.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");

        categoryPanel.getChildren().addAll(
            catTitle,
            new Separator(),
            createCategoryBtn("All Items", "All"),
            createCategoryBtn("❤ My Favorites", "Favorites"),
            createCategoryBtn("Burgers", "Burger"),
            createCategoryBtn("Pizza", "Pizza"),
            createCategoryBtn("Pasta", "Pasta"),
            createCategoryBtn("Drinks", "Drink"),
            createCategoryBtn("Fries", "Fries"),
            createCategoryBtn("Shakes", "Shake")
        );
        root.setLeft(categoryPanel);

        // ============================
        // 4. CENTER MENU GRID
        // ============================
        menuGrid = new FlowPane();
        menuGrid.setPadding(new Insets(20));
        menuGrid.setHgap(20); menuGrid.setVgap(20);
        menuGrid.setAlignment(Pos.TOP_LEFT);

        // Load Data
        allMenuItems = FileManager.loadMenu();
        if(allMenuItems.isEmpty()) {
            loadDummyData(); // Add fake data if file is empty
        }

        ScrollPane scrollPane = new ScrollPane(menuGrid);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #222831; -fx-border-color: transparent;");
        root.setCenter(scrollPane);

        // ============================
        // 5. RIGHT CART SIDEBAR
        // ============================
        VBox cartSidebar = createCartSidebar();
        root.setRight(cartSidebar);


        Scene scene = new Scene(root, width, height); 
        try { scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm()); } catch(Exception e){}
        root.setOpacity(0);
        stage.setScene(scene);

        if (isAlreadyOpen) {
            stage.setMaximized(isMaximized);
        } else {
            stage.centerOnScreen();
        }
        
        stage.show();
        Animations.screenTransition(root);

        Platform.runLater(() -> {
            filterMenu("All");
            showWelcomeDealPopup();
        });
    }

    // --- FEATURE: MOVING BANNER ---
    private StackPane createMovingBanner() {
        StackPane clipPane = new StackPane();
        clipPane.setPrefHeight(40);
        clipPane.setAlignment(Pos.CENTER_LEFT);
        clipPane.setStyle("-fx-background-color: #E58E26;");
        
        Label deals = new Label("🔥 DEAL OF THE DAY: Buy 1 Zinger get 1 Free!   |   🍕 50% OFF on Large Pizza!   |   🥤 Free Drink with every Pasta! 🔥    ");
        deals.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px;");
        deals.setWrapText(false); 
        
        // Animation
        TranslateTransition tt = new TranslateTransition(Duration.seconds(15), deals);
        tt.setFromX(1000); 
        tt.setToX(-600);  
        tt.setCycleCount(TranslateTransition.INDEFINITE); 
        tt.play();
        
        // Clipping (Hides text when it moves outside the box)
        Rectangle clip = new Rectangle(1000, 40);
        clipPane.setClip(clip);
        clipPane.widthProperty().addListener((obs, old, newVal) -> clip.setWidth(newVal.doubleValue()));
        
        clipPane.getChildren().add(deals);
        return clipPane;
    }

    // --- FEATURE: WELCOME POPUP ---
    private void showWelcomeDealPopup() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("🎉 Welcome Deal! 🎉");
        alert.setHeaderText("FLAT 30% OFF!");
        alert.setContentText("Use Code: TECH30 at checkout to get discount on everytime item.");

        // Attaching CSS to the Alert
        DialogPane dialogPane = alert.getDialogPane();

        // 1. Add the stylesheet
        try {
            dialogPane.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        } catch (Exception e) {
            System.out.println("Could not load CSS for Alert");
        }

        // 2. Add a custom class so we can target it in CSS
        dialogPane.getStyleClass().add("my-dark-dialog");

        // 3. Optional: Add your App Icon to the alert window
        try {
            Stage alertStage = (Stage) dialogPane.getScene().getWindow();
            alertStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/ZingerBurger.jpg"))); // Or your logo
      } catch (Exception e) {}
        alert.show();
    }

    // HELPER: Discount Alert Popup Style
    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        // Attach CSS
        DialogPane dialogPane = alert.getDialogPane();
        try {
            dialogPane.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
            dialogPane.getStyleClass().add("my-dark-dialog");
        } catch (Exception e) {
            System.out.println("CSS not found for alert");
        }
        
        alert.show();
    }
    // --- HELPER: Create Product Card ---
    private VBox createProductCard(MenuItem item) {
    VBox card = new VBox(10);
    card.setPrefSize(180, 280);
    card.setAlignment(Pos.CENTER);
    card.setStyle("-fx-background-color: #2D3447; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 3);");

    card.setCache(true);
    card.setCacheHint(CacheHint.SPEED);
    
    StackPane imgContainer = new StackPane();
    String specificName = item.getItemName().replaceAll(" ", "");
    java.net.URL imgUrl = getClass().getResource("/images/" + specificName + ".jpg");

    // 2. Fallback Logic: If specific image missing, find category image
    if (imgUrl == null) {
        if (item.getItemName().contains("Pasta")) imgUrl = getClass().getResource("/images/Pasta.jpg");
        else if (item.getItemName().contains("Burger")) imgUrl = getClass().getResource("/images/Burger.jpg");
        else if (item.getItemName().contains("Pizza")) imgUrl = getClass().getResource("/images/Pizza.jpg");
        else if (item.getItemName().contains("Shake")) imgUrl = getClass().getResource("/images/Shake.jpg");
        else if (item.getItemName().contains("Fries")) imgUrl = getClass().getResource("/images/fries.jpg");
    }

    // 3. Load Image using the URL String
    if (imgUrl != null) {
        // 'true' at the end enables Background Loading
        Image img = new Image(imgUrl.toExternalForm(), 150, 120, true, true); 
        ImageView imageView = new ImageView(img);
        
        Rectangle r = new Rectangle(150, 120); 
        r.setArcWidth(10); r.setArcHeight(10);
        imageView.setClip(r);
        imgContainer.getChildren().add(imageView);
    } else {
        // No Image Found -> Show Placeholder
        Rectangle r = new Rectangle(150, 120); 
        r.setFill(javafx.scene.paint.Color.web("#4e5d6c")); 
        r.setArcWidth(10); r.setArcHeight(10);
        
        String letterStr = (item.getItemName() != null && !item.getItemName().isEmpty()) ? item.getItemName().substring(0,1) : "?";
        Label letter = new Label(letterStr); 
        letter.setStyle("-fx-text-fill:white; -fx-font-size:40px;");
        imgContainer.getChildren().addAll(r, letter);
    }

    // --- HEADER ROW (Name + Heart) ---
    HBox header = new HBox(5);
    header.setAlignment(Pos.CENTER);

    Label nameLbl = new Label(item.getItemName());
    nameLbl.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

    Button btnFav = new Button();
    boolean isFav = com.anas.logic.FavoritesManager.isFavorite(item.getItemName());
    btnFav.setText(isFav ? "❤" : "♡"); 
    btnFav.setStyle("-fx-background-color: transparent; -fx-text-fill: #ff5e57; -fx-font-size: 16px; -fx-cursor: hand; -fx-padding: 0 0 0 5;");
    
    btnFav.setOnAction(e -> {
        com.anas.logic.FavoritesManager.toggleFavorite(item.getItemName());
        boolean newStatus = com.anas.logic.FavoritesManager.isFavorite(item.getItemName());
        btnFav.setText(newStatus ? "❤" : "♡");
        
        if (activeCategoryBtn != null && activeCategoryBtn.getText().contains("Favorites")) {
            filterMenu("Favorites");
        }
    });

    header.getChildren().addAll(nameLbl, btnFav);

    Label priceLbl = new Label("Rs. " + item.getBaseCost());
    priceLbl.setStyle("-fx-text-fill: #a0a0a0;");

    Button btnCustomize = new Button();

    if (item.isAvailable()) {
        btnCustomize.setText("Customize & Add");
        Animations.animateButton(btnCustomize);
        btnCustomize.setStyle("-fx-background-color: transparent; -fx-border-color: #E58E26; -fx-text-fill: #E58E26; -fx-border-radius: 20; -fx-cursor: hand;");
        btnCustomize.setOnAction(e -> ProductDetailsDialog.display(item, cart, () -> updateCartDisplay()));
    } else {
        btnCustomize.setText("SOLD OUT");
        btnCustomize.setDisable(true);
        btnCustomize.setStyle("-fx-background-color: #555; -fx-text-fill: #aaa; -fx-background-radius: 20; -fx-border-radius: 20;");
    }

    card.getChildren().addAll(imgContainer, header, priceLbl, btnCustomize);
    
    // OPTIONAL: If animation is still causing lag, wrap it in Platform.runLater
    // Platform.runLater(() -> Animations.fadeInUp(card));
    Animations.fadeInUp(card); 
    Animations.addHoverEffect(card);
    return card;
}
    // --- HELPERS (Filter & Cart) ---
    private Button createCategoryBtn(String title, String filter) {
        Button btn = new Button(title);
        btn.setPrefWidth(200); // Fixed width for stability
        btn.setMaxWidth(Double.MAX_VALUE);
        
        // Disable the "System Focus Ring"
        btn.setFocusTraversable(false);

        // Define Styles (ALWAYS keep alignment!)
        String baseStyle = "-fx-alignment: CENTER_LEFT; -fx-font-size: 14px; -fx-cursor: hand; -fx-padding: 10 15 10 15; ";
        String inactiveStyle = baseStyle + "-fx-background-color: transparent; -fx-text-fill: #a0a0a0;";
        String hoverStyle    = baseStyle + "-fx-background-color: #393E46; -fx-text-fill: #E58E26;";
        String activeStyle   = baseStyle + "-fx-background-color: #E58E26; -fx-text-fill: white; -fx-font-weight: bold;";

        // Set Initial State
        // If this is the "All Items" button, make it active by default
        if (filter.equals("All") && activeCategoryBtn == null) {
            btn.setStyle(activeStyle);
            activeCategoryBtn = btn;
        } else {
            btn.setStyle(inactiveStyle);
        }

        // --- MOUSE HOVER LOGIC ---
        btn.setOnMouseEntered(e -> {
            // Only show hover effect if this button is NOT the active one
            if (btn != activeCategoryBtn) {
                btn.setStyle(hoverStyle);
            }
        });

        btn.setOnMouseExited(e -> {
            // Return to normal only if NOT active
            if (btn != activeCategoryBtn) {
                btn.setStyle(inactiveStyle);
            }
        });

        // --- CLICK LOGIC (The Switch) ---
        btn.setOnAction(e -> {
            // 1. Reset the OLD active button (if exists)
            if (activeCategoryBtn != null) {
                activeCategoryBtn.setStyle(inactiveStyle);
            }

            // 2. Make THIS button active
            btn.setStyle(activeStyle);
            activeCategoryBtn = btn;

            // 3. Run the filter
            filterMenu(filter);
        });
        
        return btn;
    }
    private void filterMenu(String cat) {
        menuGrid.getChildren().clear();

        // Load favorites list once for efficiency
        java.util.ArrayList<String> favs = com.anas.logic.FavoritesManager.loadFavorites();

        for(MenuItem i : allMenuItems) {
            boolean match = false;
            String name = i.getItemName();
            
            if(cat.equals("All")) match = true;
            else if(cat.equals("Favorites") && favs.contains(name)) match = true;
            else if(cat.equals("Burger") && name.contains("Burger")) match = true;
            else if(cat.equals("Pizza") && name.contains("Pizza")) match = true;
            else if(cat.equals("Pasta") && name.contains("Pasta")) match = true;
            else if(cat.equals("Fries") && name.contains("Fries")) match = true;    
            else if(cat.equals("Shake") && name.contains("Shake")) match = true; 
            else if(cat.equals("Drink") && i instanceof Drink) match = true;
            if(match) menuGrid.getChildren().add(createProductCard(i));
        }
    }
    
    private void filterMenuByName(String q) {
         menuGrid.getChildren().clear();
         for(MenuItem i : allMenuItems) 
             if(i.getItemName().toLowerCase().contains(q.toLowerCase())) 
                menuGrid.getChildren().add(createProductCard(i));
    }

    private VBox createCartSidebar() {
        VBox sidebar = new VBox(15);
        sidebar.setPrefWidth(300);
        sidebar.setPadding(new Insets(20));
        sidebar.setStyle("-fx-background-color: #393E46; -fx-border-color: #555; -fx-border-width: 0 0 0 1;");
        
        Label title = new Label("Your Order");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");

        cartItemsContainer = new VBox(10);
        ScrollPane scroll = new ScrollPane(cartItemsContainer);
        scroll.setFitToWidth(true); 
        scroll.setStyle("-fx-background: #393E46; -fx-background-color: transparent;");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        // promo code section

        HBox promoBox = new HBox(10);
        promoBox.setAlignment(Pos.CENTER_LEFT);

        promoField = new TextField();
        promoField.setPromptText("Enter Code");
        promoField.setStyle("-fx-background-color: #222831; -fx-text-fill: white; -fx-border-color: #555; -fx-border-radius: 5; -fx-background-radius: 5;");
        promoField.setPrefWidth(150);

        Button btnApply = new Button("Apply");
        btnApply.setStyle("-fx-background-color: #E58E26; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        
        btnApply.setOnAction(e -> applyPromoCode()); // Action Link

        promoBox.getChildren().addAll(promoField, btnApply);

        VBox summary = new VBox(5);
        summary.setStyle("-fx-border-color: #555; -fx-border-width: 1 0 0 0; -fx-padding: 10 0 0 0;");
        
        lblSubtotal = new Label("Subtotal: 0.00"); lblSubtotal.setStyle("-fx-text-fill:#ccc;");
        lblTax = new Label("Tax: 0.00"); lblTax.setStyle("-fx-text-fill:#ccc;");

        lblDiscount = new Label("Discount: 0.00");
        lblDiscount.setStyle("-fx-text-fill: #4CAF50;"); // Green color for discount


        totalLabel = new Label("Total: 0.00"); totalLabel.setStyle("-fx-text-fill:#00ADB5; -fx-font-size: 20px; -fx-font-weight:bold;");
        summary.getChildren().addAll(lblSubtotal, lblTax, lblDiscount, totalLabel);
        
        Button btnCheckout = new Button("Checkout & Pay");
        btnCheckout.setMaxWidth(Double.MAX_VALUE);
        btnCheckout.setPrefHeight(40);
        btnCheckout.setStyle("-fx-background-color: #00ADB5; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        Animations.animateButton(btnCheckout);
        
        btnCheckout.setOnAction(e -> {
             // 1. Check Empty Cart
             if(cart.getList().isEmpty()) {
                 showAlert(Alert.AlertType.WARNING, "Cart Empty", null, "Please add items before checking out!");
                 return;
             }

             double rawSubtotal = cart.getSubTotal();
             double calculatedDiscount = 0; // Temp variable
             String code = "";
         
             if(isPromoApplied) {
                calculatedDiscount = rawSubtotal * com.anas.models.Billable.PROMO_DISCOUNT_RATE;
                code = promoField.getText().toUpperCase();
            }

            final double finalDiscountAmt = calculatedDiscount;
            final String activeCode = code;
            final double discountedSubtotal = rawSubtotal - finalDiscountAmt;

            // 3. Open Checkout Dialog
            CheckoutDialog.display(cart, discountedSubtotal, (paymentMethod) -> {
             
            // --- THIS RUNS AFTER USER CLICKS "CONFIRM" ---
             
             // 4. Calculate Tax based on their choice
             double taxRate = paymentMethod.equals("Card") ? 0.05 : 0.15;
             double taxAmount = discountedSubtotal * taxRate;
             double finalTotal = discountedSubtotal + taxAmount;

             // 5. Record Sale
             com.anas.logic.SalesManager.recordSale(finalTotal);

             com.anas.logic.SalesManager.updateItemCounts(cart.getList());
             
             // 6. Generate Receipt
             String receiptText = com.anas.logic.ReceiptManager.getReceiptText(
                 cart, 
                 rawSubtotal, 
                 finalDiscountAmt, 
                 activeCode, 
                 taxAmount, 
                 finalTotal, 
                 paymentMethod
             );
             
             ReceiptDialog.display(receiptText);
             FeedbackDialog.display();
             
             // Reset
             cart.clear(); 
             isPromoApplied = false; 
             promoField.setText("");
             updateCartDisplay();
         
                cart.clear(); 
                isPromoApplied = false; 
                promoField.setText("");
                updateCartDisplay();
            });
        });
        
        sidebar.getChildren().addAll(title, scroll, promoBox, summary, btnCheckout);
        return sidebar;
    }

    private void showTrackerPopup() {
        java.util.ArrayList<Order> allOrders = OrderManager.loadOrders();
        
        if (allOrders.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Track Order");
            alert.setHeaderText(null);
            alert.setContentText("No active orders found.");
            alert.show();
            return;
        }

        // 1. Create a Container for all orders
        VBox mainContainer = new VBox(15);
        mainContainer.setPadding(new Insets(10));
        mainContainer.setStyle("-fx-background-color: #2D3447;");

        // 2. Loop Backwards (Newest First)
        for (int i = allOrders.size() - 1; i >= 0; i--) {
            com.anas.models.Order o = allOrders.get(i);
            
            // Create a "Card" for each order
            VBox card = new VBox(8);
            card.setPadding(new Insets(15));
            // Style: Darker background, rounded corners
            card.setStyle("-fx-background-color: #393E46; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 2);");

            // --- HEADER (ID + Status) ---
            HBox header = new HBox(10);
            header.setAlignment(Pos.CENTER_LEFT);
            
            Label lblId = new Label("Order #" + o.getOrderId());
            lblId.setStyle("-fx-text-fill: #E58E26; -fx-font-weight: bold; -fx-font-size: 14px;");
            
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            
            // Determine Status Text & Color
            String statusText = o.getStatus();
            String color = "#ff5e57"; // Default Red

            if (o.getStatus().equals("Out for Delivery")) {
                statusText += " 🛵";
                color = "#00ADB5"; 
            } else if (o.getStatus().equals("Completed")) {
                statusText += " ✅";
                color = "#4CAF50"; 
            } else if (o.getStatus().equals("Pending")) {
                statusText += " ⏳";
                color = "#E58E26"; 
            } else if (o.getStatus().equals("Cancelled")) {
                statusText += " ❌";
                color = "#c0392b"; 
            }
            
            Label lblStatus = new Label(statusText);
            lblStatus.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-background-color: " + color + "; -fx-padding: 3 8 3 8; -fx-background-radius: 5;");

            header.getChildren().addAll(lblId, spacer, lblStatus);

            // --- ITEMS LIST ---
            VBox itemsBox = new VBox(2);
            java.util.Map<String, Integer> counts = new java.util.HashMap<>();
            for(MenuItem item : o.getItems()) {
                counts.put(item.getItemName(), counts.getOrDefault(item.getItemName(), 0) + 1);
            }

            for(String name : counts.keySet()) {
                int qty = counts.get(name);
                Label lblItem = new Label("• " + qty + " x " + name);
                lblItem.setStyle("-fx-text-fill: white; -fx-font-size: 13px;"); 
                itemsBox.getChildren().add(lblItem);
            }

            // --- FOOTER (Total) ---
            Label lblTotal = new Label("Total: Rs. " + String.format("%.2f", o.getTotalAmount()));
            lblTotal.setStyle("-fx-text-fill: #aaaaaa; -fx-font-size: 12px; -fx-padding: 5 0 0 0;");

            // Add standard elements to card
            card.getChildren().addAll(header, new Separator(), itemsBox, lblTotal);

            // --- CANCEL BUTTON (Only for Pending) ---
            if (o.getStatus().equals("Pending")) {
                Button btnCancel = new Button("Cancel Order");
                btnCancel.setStyle("-fx-background-color: transparent; -fx-border-color: #ff5e57; -fx-text-fill: #ff5e57; -fx-font-size: 12px; -fx-cursor: hand; -fx-border-radius: 5;");
                btnCancel.setMaxWidth(Double.MAX_VALUE); // Full width button

                // Hover Effects
                btnCancel.setOnMouseEntered(e -> btnCancel.setStyle("-fx-background-color: #ff5e57; -fx-text-fill: white; -fx-font-size: 12px; -fx-cursor: hand; -fx-background-radius: 5;"));
                btnCancel.setOnMouseExited(e -> btnCancel.setStyle("-fx-background-color: transparent; -fx-border-color: #ff5e57; -fx-text-fill: #ff5e57; -fx-font-size: 12px; -fx-border-radius: 5;"));

                // Action Logic
                btnCancel.setOnAction(e -> {
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to cancel Order #" + o.getOrderId() + "?", ButtonType.YES, ButtonType.NO);
                    confirm.showAndWait();

                    if (confirm.getResult() == ButtonType.YES) {
                        // 1. Update Backend
                        com.anas.logic.OrderManager.cancelOrder(o.getOrderId());
                        
                        // 2. Update UI Instantly (Visual Feedback)
                        lblStatus.setText("Cancelled ❌");
                        lblStatus.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-background-color: #c0392b; -fx-padding: 3 8 3 8; -fx-background-radius: 5;");
                        
                        // 3. Remove the cancel button so they can't click it again
                        card.getChildren().remove(btnCancel);
                    }
                });
                
                // Add a small spacer and the button
                Region btnSpacer = new Region();
                btnSpacer.setPrefHeight(5);
                card.getChildren().addAll(btnSpacer, btnCancel);
            }

            // Add card to the main list
            mainContainer.getChildren().add(card);
        }

        // 3. Put it in a ScrollPane
        ScrollPane scroll = new ScrollPane(mainContainer);
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(400);
        scroll.setPrefWidth(350);
        scroll.setStyle("-fx-background: #2D3447; -fx-background-color: transparent;");

        // 4. Show the Dialog
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Track My Orders");
        alert.setHeaderText("Order History (" + allOrders.size() + ")");
        alert.getDialogPane().setContent(scroll);
        
        // CSS Styling (Optional)
        try {
            alert.getDialogPane().getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
            alert.getDialogPane().getStyleClass().add("my-dark-dialog");
        } catch (Exception e) {}
        
        alert.show();
    }
    // PROMO LOGIC
    private void applyPromoCode() {
        String code = promoField.getText().trim();

        // Check for specific code
        if (code.equalsIgnoreCase("TECH30")) {
            if (!isPromoApplied) {
                isPromoApplied = true;
                updateCartDisplay(); 
    
                showAlert(Alert.AlertType.INFORMATION, 
                          "Success!", 
                          "Promo Code Applied", 
                          "You got a 30% discount on your order!");
            } else {
                showAlert(Alert.AlertType.WARNING, 
                          "Warning", 
                          null, 
                          "This code is already active!");
            }
        } else {
            // ERROR ALERT (Reset everything if code is wrong)
            isPromoApplied = false;
            updateCartDisplay();
            
            showAlert(Alert.AlertType.ERROR, 
                      "Invalid Code", 
                      null, 
                      "The code '" + code + "' is not valid.");
        }
    }

    private double calculateFinalTotal() {
        double subtotal = cart.getSubTotal();
        double tax = cart.getTax();
        double discount = 0.0;

        if (isPromoApplied) {
            discount = subtotal * com.anas.models.Billable.PROMO_DISCOUNT_RATE;
        }
        
        return (subtotal + tax) - discount;
    }

    private void updateCartDisplay() {
        cartItemsContainer.getChildren().clear(); 

        // 1. GROUP ITEMS BY NAME
        java.util.Map<String, Integer> counts = new java.util.HashMap<>();
        java.util.Map<String, com.anas.models.MenuItem> itemRefs = new java.util.HashMap<>();

        for (com.anas.models.MenuItem item : cart.getList()) {
            String name = item.getItemName();
            counts.put(name, counts.getOrDefault(name, 0) + 1);
            itemRefs.putIfAbsent(name, item); 
        }

        // 2. BUILD THE UI ROWS
        for (String name : counts.keySet()) {
            int qty = counts.get(name);
            com.anas.models.MenuItem item = itemRefs.get(name);
            double unitPrice = item.computeFinalCost(); 
            double lineTotal = unitPrice * qty;

            // --- MAIN ROW (Holds everything) ---
            HBox row = new HBox(10); 
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(10, 15, 10, 0));
            // Subtle dotted separator line
            row.setStyle("-fx-border-color: #393E46; -fx-border-width: 0 0 1 0; -fx-border-style: solid;");

            // --- LEFT SIDE: ITEM NAME & UNIT PRICE ---
            VBox nameBox = new VBox(3); // Vertical gap
            nameBox.setAlignment(Pos.CENTER_LEFT);
            
            // Allow the name box to grow and fill empty space
            HBox.setHgrow(nameBox, javafx.scene.layout.Priority.ALWAYS);

            Label lblName = new Label(name);
            lblName.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px;");
            lblName.setWrapText(true); 
            lblName.setMaxWidth(130);  

            Label lblUnit = new Label("@ Rs. " + (int)unitPrice); 
            lblUnit.setStyle("-fx-text-fill: #aaaaaa; -fx-font-size: 10px;");
            
            nameBox.getChildren().addAll(lblName, lblUnit);

            // --- RIGHT SIDE: CONTROLS & TOTAL ---
            VBox rightBox = new VBox(5);
            rightBox.setAlignment(Pos.CENTER_RIGHT);
            rightBox.setMinWidth(100); // Reserve space for buttons/price

            // 1. Total Price Label
            Label lblTotal = new Label("Rs. " + (int)lineTotal);
            lblTotal.setStyle("-fx-text-fill: #00ADB5; -fx-font-weight: bold; -fx-font-size: 13px;");
            lblTotal.setMinWidth(javafx.scene.layout.Region.USE_PREF_SIZE); 

            // 2. Quantity Controls (Tiny buttons)
            HBox qtyBox = new HBox(8);
            qtyBox.setAlignment(Pos.CENTER_RIGHT);
            
            Button btnMinus = new Button("-");
            styleMiniButton(btnMinus, "#ff5e57"); // Red

            Label lblQty = new Label(String.valueOf(qty));
            lblQty.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 11px;");

            Button btnPlus = new Button("+");
            styleMiniButton(btnPlus, "#00ADB5"); // Teal

            qtyBox.getChildren().addAll(btnMinus, lblQty, btnPlus);
            rightBox.getChildren().addAll(lblTotal, qtyBox);

            // --- BUTTON ACTIONS ---
            btnMinus.setOnAction(e -> {
                removeFromCartByName(name);
                if(cart.getList().isEmpty()) isPromoApplied = false; 
                updateCartDisplay();
            });

            btnPlus.setOnAction(e -> {
                cart.addItem(item); 
                updateCartDisplay();
            });

            // ADD TO ROW
            row.getChildren().addAll(nameBox, rightBox);
            cartItemsContainer.getChildren().add(row);
        }

        // 3. UPDATE TOTALS
        updateTotalsLabels();
    }

    // --- HELPER: MINI BUTTON STYLE ---
    // Copy this method into your class to make buttons round and clean
    private void styleMiniButton(Button btn, String colorHex) {
        btn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-border-color: " + colorHex + ";" +
            "-fx-border-radius: 50%;" +       // Circle shape
            "-fx-text-fill: " + colorHex + ";" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 10px;" +
            "-fx-min-width: 22px; -fx-min-height: 22px;" +
            "-fx-max-width: 22px; -fx-max-height: 22px;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 0;" // Remove default padding
        );
        
        // Simple Hover Effect
        btn.setOnMouseEntered(e -> btn.setStyle(
            "-fx-background-color: " + colorHex + ";" +
            "-fx-border-color: " + colorHex + ";" +
            "-fx-border-radius: 50%;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 10px;" +
            "-fx-min-width: 22px; -fx-min-height: 22px;" +
            "-fx-max-width: 22px; -fx-max-height: 22px;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 0;"
        ));

        btn.setOnMouseExited(e -> btn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-border-color: " + colorHex + ";" +
            "-fx-border-radius: 50%;" +
            "-fx-text-fill: " + colorHex + ";" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 10px;" +
            "-fx-min-width: 22px; -fx-min-height: 22px;" +
            "-fx-max-width: 22px; -fx-max-height: 22px;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 0;"
        ));
    }
    
    // --- HELPER: UPDATE LABELS ---
    private void updateTotalsLabels() {
        double sub = cart.getSubTotal();
        double tax = cart.getTax();
        double discount = isPromoApplied ? sub * com.anas.models.Billable.PROMO_DISCOUNT_RATE : 0.0;
        double grand = calculateFinalTotal();

        if(lblSubtotal != null) lblSubtotal.setText("Subtotal: Rs. " + String.format("%.2f", sub));
        if(lblTax != null) lblTax.setText("Tax (15%): Rs. " + String.format("%.2f", tax));
        if(lblDiscount != null) lblDiscount.setText("Discount: -Rs. " + String.format("%.2f", discount));
        if(totalLabel != null) totalLabel.setText("Total: Rs. " + String.format("%.2f", grand));
    }

    private void loadDummyData() {

        if (allMenuItems == null) allMenuItems = new ArrayList<>();
        allMenuItems.add(new Food("Zinger Burger", 550));
        allMenuItems.add(new Food("Beef Burger", 700));
        allMenuItems.add(new Food("Cheese Pizza", 2000));
        allMenuItems.add(new Food("Pasta", 1200));
        allMenuItems.add(new Drink("Coca Cola", 200));
        allMenuItems.add(new Drink("Mineral Water", 100));

    }
    // Helper to find and remove exactly one item with the given name
    private void removeFromCartByName(String name) {
        for (com.anas.models.MenuItem item : cart.getList()) {
            if (item.getItemName().equals(name)) {
                cart.removeItem(item);
                return; // Stop after removing just one
            }
        }
    }
}