package com.example.mechaadmin;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

import com.example.mechaadmin.bus.AdminBUS;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController implements Initializable {
    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private TextField email;
    @FXML
    private TextField password;
    @FXML
    private Label error;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public void switchToMain(ActionEvent event) throws IOException {
        if (!AdminBUS.checkLogin(email.getText(), password.getText())) {
            error.setText("Invalid email or password");
            return;
        }
        
        root = FXMLLoader.load(getClass().getResource("views/main.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("styles/app.css")).toExternalForm());
        stage.setScene(scene);
        stage.show();
    }
    
    public void logIn(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("views/main.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("styles/app.css")).toExternalForm());
        stage.setScene(scene);
        stage.show();
    }
}
