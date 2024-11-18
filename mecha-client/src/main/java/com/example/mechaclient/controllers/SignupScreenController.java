package com.example.mechaclient.controllers;

import java.io.IOException;

import com.example.mechaclient.ChatApplication;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Node;
public class SignupScreenController {
    
    @FXML
    private Button loginButton;
    
    @FXML
    private TextField emailField;
    
    @FXML
    private Button signInEmailButton;
    
    @FXML
    public void initialize() {
        // Add event handlers
        loginButton.setOnAction(event -> handleLogin(event));
        signInEmailButton.setOnAction(event -> handleEmailSignIn(event));
    }
    
    private void handleLogin(Event event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(ChatApplication.class.getResource("views/LoginScreen.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 800, 600);
    
            // Get the current stage (window)
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.setScene(scene);  
            stage.show();  
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void handleEmailSignIn(Event event) {
        String email = emailField.getText();
        if (isValidEmail(email)) {
            // TODO: Implement email sign in logic
            System.out.println("Sign in with email: " + email);
        } else {
            // TODO: Show error message
            System.out.println("Invalid email format");
        }
    }
    
    
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(emailRegex);
    }
}