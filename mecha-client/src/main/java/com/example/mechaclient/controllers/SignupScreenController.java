package com.example.mechaclient.controllers;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

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
    private TextField usernameField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField passwordField;
    @FXML
    private TextField confirmPasswordField;

    
    @FXML
    private Button signInButton;
    
    @FXML
    public void initialize() {
        // Add event handlers
        loginButton.setOnAction(event -> handleLogin(event));
        signInButton.setOnAction(event -> handleEmailSignIn(event));
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
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String address = "default address"; // Temporary address value

        // Validate inputs
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            System.out.println("All fields are required.");
            return;
        }

        if (!isValidEmail(email)) {
            System.out.println("Invalid email format.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            System.out.println("Passwords do not match.");
            return;
        }
        
        String passwordHash = hashPassword(password);

        // Send data to the server for registration
        try (Socket socket = new Socket("localhost", 12345);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            out.writeObject("SIGNUP");
            out.writeObject(username);
            out.writeObject(email);
            out.writeObject(passwordHash); 
            out.writeObject(address);

            String response = (String) in.readObject();
            if ("SUCCESS".equals(response)) {
                System.out.println("User registered successfully.");
                // redirect to login screen
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
            } else {
                System.out.println("Registration failed.");
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private String hashPassword(String password) {
        return password; // TODO: Implement a secure hashing function
    }


    
    
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(emailRegex);
    }
}