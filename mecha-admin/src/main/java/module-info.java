module com.example.mechaadmin {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;
    requires jakarta.persistence;


    opens com.example.mechaadmin to javafx.fxml;
    exports com.example.mechaadmin;
    opens com.example.mechaadmin.controllers to javafx.fxml;
    exports com.example.mechaadmin.controllers;
}