package com.example.mechaclient.controllers;

import java.io.IOException;

import com.example.mechaclient.ChatApplication;
import com.example.mechaclient.models.UserSession;
import com.example.mechaclient.models.UserSession.ServerMessageListener;
import com.example.mechaclient.utils.NotificationUtil;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ForgotPasswordScreenController implements ServerMessageListener {
    @FXML
    private TextField emailField;

    public void initialize() {
        UserSession.getInstance().addMessageListener(this);
    }
    
    @FXML
    private void handleForgot() {
        System.out.println("from forgot password screen: forgot pressed");
        String email = emailField.getText();
        if (email.isEmpty()) {
            System.out.println("Email is empty");
            return;
        }
        try {
            UserSession.out.writeObject("FORGOT_PASSWORD");
            UserSession.out.writeObject(email);
        } catch (IOException e) {
            System.out.println("Error while handle forgot: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogin() {
        System.out.println("from forgot screen: to login screen pressed");
        try {
            UserSession.socket.close();
            UserSession.getInstance().removeMessageListener(this);
            FXMLLoader fxmlLoader = new FXMLLoader(
                    ChatApplication.class.getResource("/views/LoginScreen.fxml"));
            Parent homeScreen = fxmlLoader.load();
            Scene scene = new Scene(homeScreen, 800, 600);
            Stage stage = (Stage) emailField.getScene().getWindow();
            
            stage.setOnCloseRequest(event -> {
                UserSession.getInstance().Logout();
            });
            stage.setScene(scene);
        } catch (IOException e) {
            System.out.println("can not load login screen");
            e.printStackTrace();
        }
    }

    @Override
    public void onMessageReceived(String message) {
        System.out.println("Forgot password received message: " + message);
        try {
            System.out.println("Forgot password received message: " + message);
            if (message.equals("respond_FORGOT_PASSWORD")) {
                String reponse = (String) UserSession.in.readObject();
                if (reponse.equals("SUCCESS")) {
                    System.out.println("Forgot password success");
                    Platform.runLater(() -> {
                        handleLogin();
                    });
                    return;
                }
                System.out.println("Forgot password success");
                Platform.runLater(() -> {
                    NotificationUtil.showNotification("Reset password failed", "Invalid email");
                    System.out.println("Invalid email");
                });
                // UserSession.getInstance().remeMessageListener(this);
                // handleLogin();
            } 
        } catch (Exception e) {
            System.out.println("Error while handle forgot: " + e.getMessage());
            // e.printStackTrace();
        }
    }
}
