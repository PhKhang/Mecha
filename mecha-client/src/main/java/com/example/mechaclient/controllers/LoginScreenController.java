package com.example.mechaclient.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import com.example.mechaclient.ChatApplication;

public class LoginScreenController {
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private void handleLogin() throws IOException {
        loadHomeScreen();
    }

    @FXML
    private void handleSignUp() throws IOException {   
        FXMLLoader fxmlLoader = new FXMLLoader(ChatApplication.class.getResource("views/SignupScreen.fxml"));

        Parent homeScreen = fxmlLoader.load();
        Scene scene = new Scene(homeScreen, 800, 600);
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.setScene(scene);
    }

    private void loadHomeScreen() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ChatApplication.class.getResource("views/HomeScreen.fxml"));
        Parent homeScreen = fxmlLoader.load();
        Scene scene = new Scene(homeScreen, 800, 600);
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.setScene(scene);
    }
}