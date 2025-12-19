module com.anas {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    
    exports com.anas.gui;
    opens com.anas.models to javafx.base;
    opens com.anas.gui to javafx.fxml;
}
