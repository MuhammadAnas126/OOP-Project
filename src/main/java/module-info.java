module com.anas {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.anas to javafx.fxml;
    exports com.anas;
}
