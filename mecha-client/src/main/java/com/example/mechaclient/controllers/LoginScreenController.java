package com.example.mechaclient.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import com.example.mechaclient.ChatApplication;
import com.example.mechaclient.models.UserSession;

public class LoginScreenController {
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private void handleLogin() throws IOException {
        String username = usernameField.getText();
        String password = passwordField.getText();
        if (username.isEmpty() || password.isEmpty()) {
            System.out.println("Please enter username and password");
            return;
        }
        // System.out.println("try to login");
        try {
            UserSession.out.writeObject("LOGIN");
            UserSession.out.writeObject(username);
            UserSession.out.writeObject(password);
            String response = (String) UserSession.in.readObject();
            if ("SUCCESS".equals(response)) {
                int userId = (int) UserSession.in.readObject();
                UserSession.getInstance().setUsername(username);
                UserSession.getInstance().setUserId(userId);
                
                loadHomeScreen();
            } else {
                System.out.println("Invalid username or password");
            }
        } catch (Exception e){
            e.printStackTrace();
        }
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