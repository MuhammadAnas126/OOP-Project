package com.anas.gui;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.anas.data.FileManager;
import com.anas.logic.SalesManager;
import com.anas.models.MenuItem;
import com.anas.models.Order;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AdminDashboard {
    private final Stage stage;
    private BorderPane root;
    private Label headerTitle;
    private Button btnFeedback;
    private VBox centerContent;

    // Track the currently active button
    private Button activeSidebarBtn = null;

    // Remembers if you opened the messages in this session
    private boolean hasViewedReviews = false;

    public AdminDashboard(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        boolean isMaximized = stage.isMaximized();
        boolean isAlreadyOpen = stage.isShowing();
    
        // Default size logic
        double width = 1100;
        double height = 700;
        if (isAlreadyOpen && stage.getScene() != null) {
            width = stage.getScene().getWidth();
            height = stage.getScene().getHeight();
        }

        root = new BorderPane();
        root.setStyle("-fx-background-color: #222831;");

        // ============================
        // LEFT SIDEBAR
        // ============================

        VBox sidebar = new VBox(15);
        sidebar.setPrefWidth(240);
        sidebar.setPadding(new Insets(20));
        sidebar.setStyle("-fx-background-color: #2D3447; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 0);");

        Label menuLbl = new Label("Admin Panel");
        menuLbl.setStyle("-fx-text-fill: #7f8fa6; -fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 0 0 10 0;");

        // 1. Create Navigation Buttons (Initially Inactive)
        Button btnDash      = createSidebarButton("📊 Overview", false);
        Button btnAnalytics = createSidebarButton("📈 Analytics", false);
        Button btnMenu      = createSidebarButton("🍔 Manage Menu", false);
        Button btnKitchen   = createSidebarButton("🍳 Live Kitchen", false);
        btnFeedback         = createSidebarButton("💬 Customer Reviews", false); // Field variable

        // 2. Setup Badge for Feedback
        updateSidebarBadge();

        // 3. Spacer
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // 4. Create Ghost Logout Button (New Style)
        Button btnLogout = createLogoutButton();
        btnLogout.setOnAction(e -> new LoginView(stage).show());

        // ============================
        // BUTTON ACTIONS
        // ============================
        btnDash.setOnAction(e -> {
            setActive(btnDash, "Dashboard Overview");
            loadDashboardView(true);
        });

        btnMenu.setOnAction(e -> {
            setActive(btnMenu, "Menu Management");
            loadMenuManagementView(true);
        });

        btnKitchen.setOnAction(e -> {
            setActive(btnKitchen, "Live Kitchen Orders");
            loadOrdersView(true);
        });

        btnFeedback.setOnAction(e -> {
            setActive(btnFeedback, "Customer Feedback");
            markAsRead();
            loadFeedbackView(true);
        });

        btnAnalytics.setOnAction(e -> {
            setActive(btnAnalytics, "Sales Analytics");
            loadAnalyticsView(true);
        });

        // Add everything to Sidebar
        sidebar.getChildren().addAll(menuLbl, btnDash, btnAnalytics, btnMenu, btnKitchen, btnFeedback, spacer, btnLogout);
        root.setLeft(sidebar);
        // ============================
        // TOP HEADER
        // ============================
        HBox header = new HBox();
        header.setPadding(new Insets(15, 30, 15, 30));
        header.setStyle("-fx-background-color: #222831; -fx-border-color: #393E46; -fx-border-width: 0 0 1 0;");

        headerTitle = new Label("Dashboard Overview");
        headerTitle.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold;");

        header.getChildren().add(headerTitle);
        root.setTop(header);

        centerContent = new VBox(); 
        centerContent.setFillWidth(true);
        root.setCenter(centerContent);

        loadDashboardView(true);
        setActive(btnDash); // Make Overview highlighted by default 

        Scene scene = new Scene(root, width, height);
        try { scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm()); } catch(Exception e){}

        root.setOpacity(0);
        stage.setScene(scene);
        stage.setTitle("Tech Bites - Admin Dashboard");

        if (isAlreadyOpen) {
            stage.setMaximized(isMaximized);
        } else {
            stage.centerOnScreen();
        }
        
        stage.show();
        Animations.screenTransition(root);
    }

    // --- VIEW 1: DASHBOARD OVERVIEW ---
    private void setActive(Button btn) {
        // 1. Reset the OLD active button (if exists)
        if (activeSidebarBtn != null) {
            activeSidebarBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px; -fx-cursor: hand; -fx-alignment: CENTER_LEFT;");
        }
        
        btn.setStyle("-fx-background-color: #393E46; -fx-text-fill: #E58E26; -fx-font-size: 14px; -fx-font-weight: bold; -fx-cursor: hand; -fx-alignment: CENTER_LEFT; -fx-border-color: #E58E26; -fx-border-width: 0 0 0 4;");
        
        // 3. Update tracker
        activeSidebarBtn = btn;
    }

    private void loadDashboardView(boolean animate) {
        headerTitle.setText("Dashboard Overview");
        centerContent.getChildren().clear();

        // MAIN LAYOUT
        VBox mainLayout = new VBox(20);
        mainLayout.setPadding(new Insets(30));
        mainLayout.setFillWidth(true);

        // --- TOP ROW: STATS & TOOLS ---
        HBox topRow = new HBox(20);
        topRow.setAlignment(Pos.CENTER_LEFT);

        double totalMoney = SalesManager.getTotalRevenue();
        int totalOrders = SalesManager.getTotalOrders();
        int pendingReviews = getFeedbackCount();

        VBox salesCard = createStatCard("Total Sales", String.format("Rs. %.2f", totalMoney), "#00ADB5");
        VBox orderCard = createStatCard("Orders Today", String.valueOf(totalOrders), "#E58E26");

        VBox reviewCard = new VBox(10);
        reviewCard.setPadding(new Insets(20));
        reviewCard.setPrefSize(200, 100);
        String cardColor = (pendingReviews > 0 && !hasViewedReviews) ? "#ff5e57" : "#2ecc71";
        
        reviewCard.setStyle("-fx-background-color: " + cardColor + "; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 0, 3);");
        
        Label lblTitle = new Label("Pending Reviews");
        lblTitle.setStyle("-fx-text-fill: white; -fx-opacity: 0.8;");
        Label lblValue = new Label(String.valueOf(pendingReviews));
        lblValue.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold;");
        
        reviewCard.getChildren().addAll(lblTitle, lblValue);

        if (pendingReviews > 0) {
            reviewCard.setCursor(javafx.scene.Cursor.HAND);
            reviewCard.setOnMouseClicked(e -> {
                setActive(btnFeedback);
                markAsRead();
                loadFeedbackView(true);
            });
        }

        // Toolbar (Refresh & Reset)
        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button btnRefresh = new Button("↻ Refresh");
        btnRefresh.setStyle("-fx-background-color: #393E46; -fx-text-fill: white; -fx-cursor: hand;");
        btnRefresh.setOnAction(e -> loadDashboardView(true));

        Button btnReset = new Button("⚠ Reset Sales");
        btnReset.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        btnReset.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure? This will delete ALL revenue history.", ButtonType.YES, ButtonType.NO);
            alert.showAndWait();
            if (alert.getResult() == ButtonType.YES) {
                SalesManager.clearRevenueData();
                loadDashboardView(false); // Reload to show 0.00
            }
        });

        HBox tools = new HBox(10, btnRefresh, btnReset);
        tools.setAlignment(Pos.CENTER_RIGHT);

        topRow.getChildren().addAll(salesCard, orderCard, reviewCard, spacer, tools);

        // --- BOTTOM SECTION: SALES HISTORY TABLE ---
        Label tableTitle = new Label("Recent Sales History (Completed & Cancelled)");
        tableTitle.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold; -fx-cursor: hand;");

        TableView<Order> table = new TableView<>();
        
        //FILTER LOGIC: Only show Finished Orders
        ArrayList<Order> allOrders = com.anas.logic.OrderManager.loadOrders();
        ArrayList<Order> historyOrders = new ArrayList<>();
        
        for(Order o : allOrders) {
            // Only add if it is DONE or CANCELLED
            if(o.getStatus().equals("Completed") || o.getStatus().equals("Cancelled")) {
                historyOrders.add(o);
            }
        }
        ObservableList<Order> obsList = FXCollections.observableArrayList(historyOrders);

        TableColumn<Order, String> idCol = new TableColumn<>("Order ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        
        TableColumn<Order, String> nameCol = new TableColumn<>("Customer");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        
        TableColumn<Order, Double> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        
        TableColumn<Order, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        statusCol.setCellFactory(column -> new TableCell<>() {
        @Override protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (item == null || empty) { setText(null); setStyle(""); } 
            else {
                setText(item);
                if (item.equals("Cancelled")) setStyle("-fx-text-fill: #ff5e57; -fx-font-weight: bold;");
                else if (item.equals("Completed")) setStyle("-fx-text-fill: #2ecc71; -fx-font-weight: bold;");
                }
            }
        });

        TableColumn<Order, Void> actionCol = new TableColumn<>("Action");
        actionCol.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("❌");
            {
                btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #ff5e57; -fx-font-weight: bold; -fx-cursor: hand;");
                btn.setOnAction(event -> {
                    Order order = getTableView().getItems().get(getIndex());
                    // Delete Logic
                    com.anas.logic.OrderManager.deleteOrder(order.getOrderId());
                    loadDashboardView(false); // Refresh table immediately
                });
            }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) setGraphic(null);
                else setGraphic(btn);
            }
        });

        table.getColumns().addAll(idCol, nameCol, amountCol, statusCol, actionCol);
        table.setItems(obsList);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.setPrefHeight(400); // Takes up space nicely
        table.setStyle("-fx-background-color: #2D3447;");

        table.getStyleClass().add("modern-table");
        // Add everything to layout
        mainLayout.getChildren().addAll(topRow, new Separator(), tableTitle, table);
        
        centerContent.getChildren().add(mainLayout);
        if(animate) {
        Animations.fadeInUp(mainLayout);
        }
    }

    private void loadMenuManagementView(boolean animate) {
        headerTitle.setText("Manage Menu Items");
        centerContent.getChildren().clear();

        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        content.setFillWidth(true);

        // Table View
        TableView<MenuItem> table = new TableView<>();
        ArrayList<MenuItem> data = FileManager.loadMenu();
        ObservableList<MenuItem> observableList = FXCollections.observableArrayList(data);

        TableColumn<MenuItem, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("itemName"));

        TableColumn<MenuItem, Double> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("baseCost"));

        //  Status Column
        TableColumn<MenuItem, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().isAvailable() ? "In Stock" : "OUT OF STOCK")
        );

        table.getColumns().addAll(nameCol, priceCol, statusCol);
        table.setItems(observableList);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.setPrefHeight(400);
        
        // Coloring: Make "Out of Stock" rows look red 
        statusCol.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.equals("OUT OF STOCK")) {
                        setStyle("-fx-text-fill: #ff5e57; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #2ecc71;");
                    }
                }
            }
        });

        // --- BUTTONS ROW ---
        HBox buttonsBox = new HBox(15);
        buttonsBox.setAlignment(Pos.CENTER_LEFT);

        Button btnAdd = new Button("✚ Add Item");
        btnAdd.setStyle("-fx-background-color: #00ADB5; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        btnAdd.setOnAction(e -> {
            AddItemDialog.display(newItem -> {
                data.add(newItem);          
                FileManager.saveMenu(data); 
                loadMenuManagementView(false);   
            });
        });

        // TOGGLE AVAILABILITY BUTTON
        Button btnToggle = new Button("🛑 Toggle Availability");
        btnToggle.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        btnToggle.setOnAction(e -> {
            MenuItem selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                // Flip the status (True -> False / False -> True)
                selected.setAvailable(!selected.isAvailable());
                FileManager.saveMenu(data); // Save change
                loadMenuManagementView(false);   // Refresh table
            } else {
                new Alert(Alert.AlertType.WARNING, "Select an item to update").show();
            }
        });

        Button btnDelete = new Button("🗑 Delete");
        btnDelete.setStyle("-fx-background-color: #ff5e57; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        btnDelete.setOnAction(e -> {
            MenuItem selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                data.remove(selected);
                FileManager.saveMenu(data);
                loadMenuManagementView(false);
            }
        });
        
        Button btnClearAll = new Button("⚠ Clear All");
        btnClearAll.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        btnClearAll.setOnAction(e -> {
            if(data.isEmpty()) return;
            
            // Confirm before deleting everything
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete the ENTIRE menu?", ButtonType.YES, ButtonType.NO);
            alert.showAndWait();
            
            if (alert.getResult() == ButtonType.YES) {
                data.clear();               // Wipe list
                FileManager.saveMenu(data); // Save empty list
                loadMenuManagementView(false);   // Refresh
            }
        });

        Button btnRestore = new Button("↻ Defaults");
        btnRestore.setStyle("-fx-background-color: #E58E26; -fx-text-fill: white; -fx-cursor: hand;");
        btnRestore.setOnAction(e -> {
            data.clear();
            data.add(new com.anas.models.Food("Zinger Burger", 550));
            data.add(new com.anas.models.Food("Beef Burger", 700));
            data.add(new com.anas.models.Food("Cheese Pizza", 2000));
            data.add(new com.anas.models.Drink("Coca Cola", 200));
            FileManager.saveMenu(data);
            loadMenuManagementView(false); 
        });

        buttonsBox.getChildren().addAll(btnAdd, btnToggle, btnDelete, btnClearAll, btnRestore);
        content.getChildren().addAll(table, buttonsBox);
        
        table.getStyleClass().add("modern-table");
        centerContent.getChildren().add(content);
        if (animate) {
            Animations.fadeInUp(content);
        }
    }

    // --- VIEW 3: FEEDBACK READER ---
    private void loadFeedbackView(boolean animate) {
        headerTitle.setText("Customer Feedback");
        centerContent.getChildren().clear();

        VBox content = new VBox(15);
        content.setPadding(new Insets(30));
        content.setFillWidth(true); 

        // content VBox grow to fill the center area
        VBox.setVgrow(content, Priority.ALWAYS);

        VBox reviewsList = new VBox(15);
        ScrollPane scroll = new ScrollPane(reviewsList);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        try {
            List<String> lines = Files.readAllLines(Paths.get("reviews.txt"));
            if (lines.isEmpty()) {
                VBox emptyState = new VBox(10);
                emptyState.setAlignment(Pos.CENTER);
        
                Label icon = new Label("💬");
                icon.setStyle("-fx-font-size: 50px;");
        
                Label msg = new Label("No reviews yet");
                msg.setStyle("-fx-text-fill: #a0a0a0; -fx-font-size: 18px;");
        
                emptyState.getChildren().addAll(icon, msg);
        
                // Add to main list container
                reviewsList.getChildren().add(emptyState);
                reviewsList.setAlignment(Pos.CENTER); // Center content vertically
                } else {
                    for (String line : lines) {
                        if (!line.trim().isEmpty()) reviewsList.getChildren().add(createReviewCard(line));
                    }
                }
        } catch (IOException e) {
            reviewsList.getChildren().add(new Label("Error loading reviews."));
        }

        Button btnClear = new Button("Clear All Reviews");
        btnClear.setPrefWidth(150);
        btnClear.setStyle("-fx-background-color: #E58E26; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        btnClear.setOnAction(e -> { 
            btnClear();
            loadFeedbackView(false);
        });

        content.getChildren().addAll(scroll, btnClear);
        centerContent.getChildren().add(content);
        if (animate) {
            Animations.fadeInUp(content);
        }
    }

    // --- VIEW 5: ANALYTICS ---
    private void loadAnalyticsView(boolean animate) {
        headerTitle.setText("Sales Analytics");
        centerContent.getChildren().clear();

        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        content.setFillWidth(true);

        // --- 1. TOOLS BAR (Clear & Refresh) ---
        Button btnClearSales = new Button("⚠ Clear History");
        btnClearSales.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        btnClearSales.setOnAction(e -> {
            // Check if empty
            if (com.anas.logic.SalesManager.getItemCounts().isEmpty()) return;

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Clear Charts");
            alert.setHeaderText("Delete All Sales History?");
            alert.setContentText("This will reset all graphs to zero. This cannot be undone.");

            if (alert.showAndWait().get() == ButtonType.OK) {
                com.anas.logic.SalesManager.clearChartData();
                loadAnalyticsView(false); // Refresh
            }
        });

        Button btnRefresh = new Button("↻ Refresh");
        btnRefresh.setStyle("-fx-background-color: #00ADB5; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        btnRefresh.setOnAction(e -> loadAnalyticsView(false));

        HBox tools = new HBox(15);
        tools.setAlignment(Pos.CENTER_RIGHT);
        tools.getChildren().addAll(btnClearSales, btnRefresh);

        // --- 2. CHART CONTAINER (Side by Side) ---
        HBox chartsBox = new HBox(20);
        chartsBox.setAlignment(Pos.CENTER);
        chartsBox.setPrefHeight(450);

        // --- LEFT: PIE CHART (Distribution) ---
        VBox pieBox = new VBox(10);
        pieBox.setStyle("-fx-background-color: #2D3447; -fx-background-radius: 10; -fx-padding: 20;");
        HBox.setHgrow(pieBox, Priority.ALWAYS); // Grow to fill half
        
        Label pieTitle = new Label("Product Popularity");
        pieTitle.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
        
        PieChart pieChart = new PieChart();
        java.util.Map<String, Integer> counts = SalesManager.getItemCounts();
        int totalItems = 0;
        for (int quantity : counts.values()) {
            totalItems += quantity;
        }
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        
        if (counts.isEmpty()) {
            pieData.add(new PieChart.Data("No Sales Yet", 1));
            btnClearSales.setDisable(true); 
            btnClearSales.setStyle("-fx-background-color: #555; -fx-text-fill: #888;");
        } else {
            for (String key : counts.keySet()) {
                pieData.add(new PieChart.Data(key, counts.get(key)));
            }
        }
        pieChart.setData(pieData);
        pieChart.setLegendVisible(false); // Clean look
        pieBox.getChildren().addAll(pieTitle, pieChart);

        // --- RIGHT: BAR CHART (Top Sellers) ---
        VBox barBox = new VBox(10);
        barBox.setStyle("-fx-background-color: #2D3447; -fx-background-radius: 10; -fx-padding: 20;");
        HBox.setHgrow(barBox, Priority.ALWAYS);

        Label barTitle = new Label("Sales Volume");
        barTitle.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");

        // Axis
        javafx.scene.chart.CategoryAxis xAxis = new javafx.scene.chart.CategoryAxis();
        javafx.scene.chart.NumberAxis yAxis = new javafx.scene.chart.NumberAxis();
        yAxis.setLabel("Quantity Sold");
        xAxis.setTickLabelFill(javafx.scene.paint.Color.WHITE);
        yAxis.setTickLabelFill(javafx.scene.paint.Color.WHITE);

        javafx.scene.chart.BarChart<String, Number> barChart = new javafx.scene.chart.BarChart<>(xAxis, yAxis);
        barChart.setLegendVisible(false);
        
        javafx.scene.chart.XYChart.Series<String, Number> series = new javafx.scene.chart.XYChart.Series<>();
        if (!counts.isEmpty()) {
            for (String key : counts.keySet()) {
                series.getData().add(new javafx.scene.chart.XYChart.Data<>(key, counts.get(key)));
            }
        }
        barChart.getData().add(series);
        barBox.getChildren().addAll(barTitle, barChart);

        chartsBox.getChildren().addAll(pieBox, barBox);

        // 2. SUMMARY TEXT
        Label summary = new Label("Total Items Sold: " + totalItems); 
        summary.setStyle("-fx-text-fill: #00ADB5; -fx-font-size: 18px; -fx-font-weight: bold;");

        content.getChildren().addAll(tools, summary, chartsBox);
        centerContent.getChildren().add(content);

        if (animate) {
            Animations.fadeInUp(content);
        }
    }

    // --- VIEW 4: LIVE KITCHEN ORDERS ---
    private void loadOrdersView(boolean animate) {
        headerTitle.setText("Live Kitchen Orders");
        centerContent.getChildren().clear();

        // --- BUTTON: CLEAR ALL ORDERS ---
        Button btnClearAll = new Button("⚠ Clear History");
        btnClearAll.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        btnClearAll.setOnAction(e -> {
            // 1. Check if there are orders to clear
            ArrayList<com.anas.models.Order> currentList = com.anas.logic.OrderManager.loadOrders();
            if (currentList.isEmpty()) return;

            // 2. Confirmation Dialog
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Clear All Orders");
            alert.setHeaderText("Delete ALL Order History?");
            alert.setContentText("This cannot be undone. All active and past orders will be wiped.");
            
            // 3. Delete Action
            if (alert.showAndWait().get() == ButtonType.OK) {
                // Call the manager to clear everything
                com.anas.logic.OrderManager.clearAllOrders(); 
                loadOrdersView(false); // Refresh
            }
        });

        Button btnRefresh = new Button("↻ Refresh");
        btnRefresh.setStyle("-fx-background-color: #00ADB5; -fx-text-fill: white; -fx-cursor: hand;");
        btnRefresh.setOnAction(e -> loadOrdersView(false));
        
        HBox tools = new HBox(15);
        tools.setPadding(new Insets(0, 30, 0, 30));
        tools.setAlignment(Pos.CENTER_RIGHT);
        tools.getChildren().addAll(btnClearAll, btnRefresh);

        VBox ordersContainer = new VBox(15);
        ordersContainer.setPadding(new Insets(10));
        
        ScrollPane scroll = new ScrollPane(ordersContainer);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        ArrayList<Order> orders = com.anas.logic.OrderManager.loadOrders();
        if (orders.isEmpty()) {
            Label empty = new Label("No Active Orders");
            empty.setStyle("-fx-text-fill: #a0a0a0; -fx-font-size: 18px;");
            empty.setAlignment(Pos.CENTER);
            ordersContainer.getChildren().add(empty);
            ordersContainer.setAlignment(Pos.CENTER);

            btnClearAll.setDisable(true); 
            btnClearAll.setStyle("-fx-background-color: #555555; -fx-text-fill: #888;");
        } else {
            java.util.Collections.reverse(orders);
            for (Order o : orders) {
                ordersContainer.getChildren().add(createOrderCard(o));
            }
        }

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(tools, scroll);
        
        centerContent.getChildren().add(layout);
        if (animate) {
            Animations.fadeInUp(layout);
        }
    }

    // --- HELPERS ---
    
    private VBox createOrderCard(Order o) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: #2D3447; -fx-border-color: #555; -fx-border-radius: 10; -fx-background-radius: 10;");

        HBox header = new HBox(10);
        Label idLbl = new Label("Order #" + o.getOrderId());
        idLbl.setStyle("-fx-text-fill: #E58E26; -fx-font-weight: bold;");
        
        Label statusLbl = new Label(o.getStatus());
        String statusColor = "#ff5e57"; // Default Red (Pending)
        if (o.getStatus().equals("Completed")) statusColor = "#4CAF50"; // Green
        else if (o.getStatus().equals("Out for Delivery")) statusColor = "#00ADB5"; // Teal
        else if (o.getStatus().equals("Cancelled")) statusColor = "#c0392b"; // Dark Red
        statusLbl.setStyle("-fx-text-fill: white; -fx-background-color: " + statusColor + "; -fx-padding: 3 8 3 8; -fx-background-radius: 5;");
        
        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(idLbl, spacer, statusLbl);

        Label details = new Label(o.getCustomerName() + " | " + o.getAddress() + " | " + o.getPaymentMethod());
        details.setStyle("-fx-text-fill: #a0a0a0;");

        VBox itemsBox = new VBox(5);
        itemsBox.setPadding(new Insets(10, 0, 10, 0));

        java.util.Map<String, Integer> counts = new java.util.HashMap<>();
        for (com.anas.models.MenuItem item : o.getItems()) {
            counts.put(item.getItemName(), counts.getOrDefault(item.getItemName(), 0) + 1);
        }

        // Display the items
        for (String name : counts.keySet()) {
            int qty = counts.get(name);
            
            Label itemLbl = new Label("• " + qty + " x " + name);
            
            // Highlight logic: If qty > 1, make it Gold/Yellow so Chef notices immediately
            if (qty > 1) {
                itemLbl.setStyle("-fx-text-fill: #FFD700; -fx-font-weight: bold; -fx-font-family: 'Monospaced'; -fx-font-size: 13px;");
            } else {
                itemLbl.setStyle("-fx-text-fill: white; -fx-font-family: 'Monospaced'; -fx-font-size: 13px;");
            }
            
            itemsBox.getChildren().add(itemLbl);
        }

        HBox actions = new HBox(10);
        Button btnNext = new Button("Advance Status");
        btnNext.setStyle("-fx-background-color: #00ADB5; -fx-text-fill: white; -fx-cursor: hand;");
        btnNext.setOnAction(e -> {
            String next = "Completed";
            if(o.getStatus().equals("Pending")) next = "Preparing";
            else if(o.getStatus().equals("Preparing")) next = "Out for Delivery";
            else if(o.getStatus().equals("Out for Delivery")) next = "Completed";
            
            com.anas.logic.OrderManager.updateOrderStatus(o.getOrderId(), next);
            loadOrdersView(false);
        });

        Button btnDelete = new Button("Cancel Order");
        btnDelete.setStyle("-fx-background-color: #ff5e57; -fx-text-fill: white; -fx-cursor: hand;");
        btnDelete.setOnAction(e -> {
            com.anas.logic.OrderManager.cancelOrder(o.getOrderId());
            loadOrdersView(false);
        });

        Button btnTrash = new Button("🗑");
        btnTrash.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        btnTrash.setTooltip(new Tooltip("Delete Permanently"));
        btnTrash.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Order");
            alert.setHeaderText("Delete Order #" + o.getOrderId() + "?");
            alert.setContentText("This will remove it from the Customer Tracker and Sales History permanently. Are you sure?");
            
            // Only delete if they click OK
            if (alert.showAndWait().get() == ButtonType.OK) {
                com.anas.logic.OrderManager.deleteOrder(o.getOrderId());
                loadOrdersView(false);
            }
        });

        if (!o.getStatus().equals("Completed") && !o.getStatus().equals("Cancelled")) {
        actions.getChildren().add(btnNext);
        actions.getChildren().add(btnDelete);
        }

        actions.getChildren().add(btnTrash);

        card.getChildren().addAll(header, details, new Separator(), itemsBox, actions);
        return card;
    }

    private VBox createStatCard(String title, String value, String colorHex) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setPrefSize(200, 100);
        card.setStyle("-fx-background-color: #2D3447; -fx-background-radius: 10; -fx-border-color: " + colorHex + "; -fx-border-width: 0 0 0 5;");
        Label lblTitle = new Label(title); lblTitle.setStyle("-fx-text-fill: #a0a0a0;");
        Label lblValue = new Label(value); lblValue.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold;");
        card.getChildren().addAll(lblTitle, lblValue);
        Animations.addHoverEffect(card);
        return card;
    }

    private HBox createReviewCard(String line) {
        HBox card = new HBox(15);
        card.setPadding(new Insets(15));
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle("-fx-background-color: #2D3447; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 2);");

        String initial = "U";
        String name = "Customer";
        String message = line;
        String date = "Just now";

        try {
            if (line.contains("|")) {
                String[] parts = line.split("\\|");
                if (parts.length >= 3) {
                    date = parts[0].trim();
                    name = parts[1].trim();
                    message = parts[2].trim();
                    if (!name.isEmpty()) initial = name.substring(0, 1).toUpperCase();
                }
            }
        } catch (Exception e) {}

        StackPane avatar = new StackPane();
        javafx.scene.shape.Circle circle = new javafx.scene.shape.Circle(20, javafx.scene.paint.Color.web("#00ADB5"));
        Label initialLbl = new Label(initial);
        initialLbl.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 18px;");
        avatar.getChildren().addAll(circle, initialLbl);

        VBox textContainer = new VBox(5);
        HBox headerInfo = new HBox(10);
        Label nameLbl = new Label(name); nameLbl.setStyle("-fx-text-fill: #a0a0a0; -fx-font-size: 12px; -fx-font-weight: bold;");
        Label dateLbl = new Label("• " + date); dateLbl.setStyle("-fx-text-fill: #555; -fx-font-size: 10px;");
        headerInfo.getChildren().addAll(nameLbl, dateLbl);

        Label msgLbl = new Label(message);
        msgLbl.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        msgLbl.setWrapText(true);
        msgLbl.setMaxWidth(550);

        HBox ratingBox = new HBox(2);
        if (message.contains("Stars]")) {
            char ratingChar = message.charAt(1);
            if (Character.isDigit(ratingChar)) {
                int stars = Character.getNumericValue(ratingChar);
                for (int i = 0; i < stars; i++) {
                    Label star = new Label("★");
                    star.setStyle("-fx-text-fill: #FFD700; -fx-font-size: 16px;");
                    ratingBox.getChildren().add(star);
                }
                try { msgLbl.setText(message.substring(message.indexOf("]") + 1).trim()); } catch (Exception ex) {}
            }
        }

        textContainer.getChildren().addAll(headerInfo, ratingBox, msgLbl);
        card.getChildren().addAll(avatar, textContainer);
        return card;
    }

    private void btnClear() {
        try {
            Files.write(Paths.get("reviews.txt"), new byte[0]);
            hasViewedReviews = true;
            updateSidebarBadge();
        } catch (IOException ex) {
            new Alert(Alert.AlertType.ERROR, "Error clearing reviews").show();
        }
    }
    // 1. Standard Navigation Button
    private Button createSidebarButton(String text, boolean isActive) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setPrefHeight(45);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setCursor(javafx.scene.Cursor.HAND);
        btn.setPadding(new Insets(10, 15, 10, 15));

        // Apply Style
        if (isActive) {
            btn.setStyle("-fx-background-color: rgba(229, 142, 38, 0.15); -fx-text-fill: #E58E26; -fx-font-weight: bold; -fx-font-size: 14px; -fx-border-color: #E58E26; -fx-border-width: 0 0 0 4;");
        } else {
            btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #bdc3c7; -fx-font-size: 14px;");
        }

        // Hover Effects
        btn.setOnMouseEntered(e -> {
            if (btn != activeSidebarBtn) btn.setStyle("-fx-background-color: #384158; -fx-text-fill: white; -fx-background-radius: 10; -fx-font-size: 14px;");
        });
        btn.setOnMouseExited(e -> {
            if (btn != activeSidebarBtn) btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #bdc3c7; -fx-font-size: 14px;");
        });

        return btn;
    }

    // 2. Ghost Logout Button
    private Button createLogoutButton() {
        Button btn = new Button("Log Out");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setPrefHeight(40);
        btn.setCursor(javafx.scene.Cursor.HAND);
        
        String defaultStyle = "-fx-background-color: transparent; -fx-text-fill: #ff5e57; -fx-border-color: #ff5e57; -fx-border-radius: 20; -fx-background-radius: 20; -fx-font-size: 14px;";
        String hoverStyle   = "-fx-background-color: #ff5e57; -fx-text-fill: white; -fx-border-color: #ff5e57; -fx-border-radius: 20; -fx-background-radius: 20; -fx-font-size: 14px; -fx-font-weight: bold;";
        
        btn.setStyle(defaultStyle);
        btn.setOnMouseEntered(e -> btn.setStyle(hoverStyle));
        btn.setOnMouseExited(e -> btn.setStyle(defaultStyle));
        
        return btn;
    }
    private void setActive(Button clickedBtn, String titleText) {
    // 1. Update the Header Title
    if (headerTitle != null) headerTitle.setText(titleText);
    
    if (activeSidebarBtn != null) {
        // Reset the OLD button to Inactive style
        activeSidebarBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #bdc3c7; -fx-font-size: 14px;");
    }

    // 3. Set the NEW button to Active style
    clickedBtn.setStyle(
        "-fx-background-color: rgba(229, 142, 38, 0.15);" + 
        "-fx-text-fill: #E58E26;" +
        "-fx-font-weight: bold;" +
        "-fx-font-size: 14px;" +
        "-fx-border-color: #E58E26;" +
        "-fx-border-width: 0 0 0 4;"
    );

    // 4. Update Reference
    activeSidebarBtn = clickedBtn;
    
    // 5. Refresh Badge colors (if the active button is the feedback button)
    updateSidebarBadge();
}


    private void updateSidebarBadge() {
    int count = getFeedbackCount();
    
    // Check if this button is currently the active one to decide text color
    boolean isBtnActive = (btnFeedback == activeSidebarBtn);

    if (count > 0) {
        // Create the Container
        String statusColor = hasViewedReviews ? "#2ecc71" : "#ff5e57";
        HBox content = new HBox(10); 
        content.setAlignment(Pos.CENTER_LEFT);
        
        // If active, use Brand Orange (#E58E26). If inactive, use the Status Color (Red/Green).
        String textColor = isBtnActive ? "#E58E26" : statusColor;
        Label textLbl = new Label("Customer Reviews");
        textLbl.setStyle("-fx-text-fill: " + textColor + "; -fx-font-size: 14px; -fx-font-weight: " + (isBtnActive ? "bold" : "normal") + ";");
        
        // Red Circle Badge
        StackPane badge = new StackPane();
        javafx.scene.shape.Circle circle = new javafx.scene.shape.Circle(10, javafx.scene.paint.Color.web(statusColor));

        Label countLbl = new Label(String.valueOf(count));
        countLbl.setStyle("-fx-text-fill: white; -fx-font-size: 10px; -fx-font-weight: bold;");

        badge.getChildren().addAll(circle, countLbl);
        content.getChildren().addAll(textLbl, badge);
        
        btnFeedback.setGraphic(content);
        btnFeedback.setText(""); // Hide default text since we are using Graphic
    } else {
        btnFeedback.setGraphic(null);
        btnFeedback.setText("Customer Reviews");
        // Reset text fill based on active state
        btnFeedback.setStyle(isBtnActive 
            ? "-fx-background-color: rgba(229, 142, 38, 0.15); -fx-text-fill: #E58E26; -fx-font-weight: bold; -fx-border-color: #E58E26; -fx-border-width: 0 0 0 4;"
            : "-fx-background-color: transparent; -fx-text-fill: #bdc3c7; -fx-font-size: 14px;"
        );
    }
    }
    private int getFeedbackCount() {
        try { return Files.readAllLines(Paths.get("reviews.txt")).size(); } catch (Exception e) { return 0; }
    }
    
    private void markAsRead() { hasViewedReviews = true; updateSidebarBadge(); }
}