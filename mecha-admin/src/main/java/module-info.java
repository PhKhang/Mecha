module com.example.mechaadmin {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;


    opens com.example.mechaadmin to javafx.fxml;
    exports com.example.mechaadmin;
}