module com.example.mechaclient {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    
    opens com.example.mechaclient to javafx.fxml;
    opens com.example.mechaclient.controllers to javafx.fxml;

    exports com.example.mechaclient;
    exports com.example.mechaclient.controllers;
}