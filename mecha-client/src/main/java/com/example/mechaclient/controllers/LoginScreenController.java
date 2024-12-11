package com.example.mechaclient.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
// import java.io.ObjectInputStream;
// import java.io.ObjectOutputStream;
// import java.net.Socket;
import java.util.List;

import com.example.mechaclient.ChatApplication;
import com.example.mechaclient.models.ChatBox;
import com.example.mechaclient.models.ChatBox.ChatType;
import com.example.mechaclient.models.UserSession;
import com.example.mechaclient.models.UserSession.ServerMessageListener;
import com.example.mechaclient.utils.NotificationUtil;

public class LoginScreenController implements ServerMessageListener{
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    public void initialize() {
        UserSession.getInstance().addMessageListener(this);
        try {
            UserSession.getInstance().connectToServer();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.out.println("error while initialize in login screen: " + e.getMessage());
            e.printStackTrace();
        }
        UserSession.getInstance().startListening();
    }

    @FXML
    private void handleLogin() throws IOException {
        System.out.println("from login screen: login pressed");
        String username = usernameField.getText();
        String password = passwordField.getText();
        if (username.isEmpty()) {
            NotificationUtil.showNotification("Missing username" , "Please enter username");
            return;
        } else if (password.isEmpty()){
            NotificationUtil.showNotification("Missing password" , "Please enter password");
            return;
        }
        try {
            UserSession.out.writeObject("LOGIN");
            UserSession.out.writeObject(username);
            UserSession.out.writeObject(password);  
            System.out.println("send login signal to the server");          
        } catch (Exception e){
            System.out.println("Error while handle login: " + e.getMessage());
            // e.printStackTrace();
        }
    }

    @FXML
    private void handleSignUp() throws IOException {   
        
        FXMLLoader fxmlLoader = new FXMLLoader(ChatApplication.class.getResource("views/SignupScreen.fxml"));

        Parent homeScreen = fxmlLoader.load();
        Scene scene = new Scene(homeScreen, 800, 600);
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.setScene(scene);
        UserSession.getInstance().removeMessageListener(this);
    }

    private void loadHomeScreen() throws IOException {
        System.out.println("loading home screen...");
        FXMLLoader fxmlLoader = new FXMLLoader(ChatApplication.class.getResource("views/HomeScreen.fxml"));
        Parent homeScreen = fxmlLoader.load();
        Scene scene = new Scene(homeScreen, 800, 600);
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.setScene(scene);
        System.out.println("loading home screen complete");
        UserSession.getInstance().removeMessageListener(this);
    }

    @Override
    public void onMessageReceived(String serverMessage) {
        try {
            System.out.println("Login Screen receive message: " + serverMessage);
            if ("SUCCESS".equals(serverMessage)) {
                int userId = (int) UserSession.in.readObject();
                String username = (String) UserSession.in.readObject();
                UserSession.getInstance().setUsername(username);
                UserSession.getInstance().setUserId(userId);
                
                System.out.println("processing login complete!");
                Platform.runLater(() -> {
                    try {
                        loadHomeScreen();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        System.out.println("can not load home screen");
                        e.printStackTrace();
                    }
                });
                
            } else {
                NotificationUtil.showNotification("Login failed" , "Invalid username or password");
                System.out.println("Invalid username or password");
            }
        } catch (Exception e){
            System.out.println("error handling response from server in login screen: " + e.getMessage());
            
        }
    }
}