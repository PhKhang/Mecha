package com.example.mechaclient;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import com.example.mechaclient.models.UserSession;

public class ChatApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ChatApplication.class.getResource("/views/LoginScreen.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        stage.setTitle("User Chat Application");
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> {
            UserSession.getInstance().Logout();
        });
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}