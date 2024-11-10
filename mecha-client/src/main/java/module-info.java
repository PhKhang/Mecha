module com.example.mechaclient {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.mechaclient to javafx.fxml;
    exports com.example.mechaclient;
}