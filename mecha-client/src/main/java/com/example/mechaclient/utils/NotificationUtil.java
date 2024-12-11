package com.example.mechaclient.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class NotificationUtil {
    public static void showNotification(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }
}