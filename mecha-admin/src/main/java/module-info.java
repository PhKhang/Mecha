module com.example.mechaadmin {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.mechaadmin to javafx.fxml;
    exports com.example.mechaadmin;
}