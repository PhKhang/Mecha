package com.example.mechaclient.controllers;

import java.io.IOException;

import com.example.mechaclient.ChatApplication;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class ProfileScreenController {
    
    @FXML private ImageView profileImage;
    @FXML private Circle clipCircle;
    @FXML private Label displayName;
    @FXML private Label displayEmail;
    
    @FXML private Button editButton;
    @FXML private Button confirmButton;
    @FXML private Button cancelButton;
    @FXML private HBox actionButtons;
    
    @FXML private TextField fullNameField;
    @FXML private TextField genderField;
    @FXML private TextField addressField;
    @FXML private TextField usernameField;
    @FXML private TextField dobField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    
    private boolean isEditMode = false;
    @FXML private GridPane formInfo;

    @FXML
    public void initialize() {
        formInfo.setPadding(new Insets(20, 30, 20, 30));
        // Store initial values for cancel operation
        storeInitialValues();
    }
    
    @FXML
    private void handleEdit() {
        storeInitialValues();
        isEditMode = true;
        setEditMode(true);
    }
    
    @FXML
    private void handleConfirm() {
        isEditMode = false;
        setEditMode(false);
        updateDisplayValues();
    }
    
    @FXML
    private void handleCancel() {
        isEditMode = false;
        setEditMode(false);
        restoreInitialValues();
    }
    
    @FXML
    private void handleChangePassword() {
        // Implement password change logic
        System.out.println("Change password clicked");
    }
    
    private void setEditMode(boolean editMode) {
        // Toggle visibility of edit/confirm/cancel buttons
        editButton.setVisible(!editMode);
        confirmButton.setVisible(editMode);
        cancelButton.setVisible(editMode);
        
        // Toggle field editability
        fullNameField.setEditable(editMode);
        genderField.setEditable(editMode);
        addressField.setEditable(editMode);
        usernameField.setEditable(editMode);
        dobField.setEditable(editMode);
        emailField.setEditable(editMode);
        
        // Update styles
        String fieldStyle = editMode ? "editable-field" : "non-editable-field";
        fullNameField.getStyleClass().setAll("text-field", fieldStyle);
        genderField.getStyleClass().setAll("text-field", fieldStyle);
        addressField.getStyleClass().setAll("text-field", fieldStyle);
        usernameField.getStyleClass().setAll("text-field", fieldStyle);
        dobField.getStyleClass().setAll("text-field", fieldStyle);
        emailField.getStyleClass().setAll("text-field", fieldStyle);
    }
    
    private String[] initialValues = new String[6];
    
    private void storeInitialValues() {
        initialValues[0] = fullNameField.getText();
        initialValues[1] = genderField.getText();
        initialValues[2] = addressField.getText();
        initialValues[3] = usernameField.getText();
        initialValues[4] = dobField.getText();
        initialValues[5] = emailField.getText();
    }
    
    private void restoreInitialValues() {
        fullNameField.setText(initialValues[0]);
        genderField.setText(initialValues[1]);
        addressField.setText(initialValues[2]);
        usernameField.setText(initialValues[3]);
        dobField.setText(initialValues[4]);
        emailField.setText(initialValues[5]);
    }
    
    private void updateDisplayValues() {
        displayName.setText(usernameField.getText());
        displayEmail.setText(emailField.getText());
    }

    @FXML
    private void returnToHomeScreen(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ChatApplication.class.getResource("views/HomeScreen.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        stage = (Stage) fullNameField.getScene().getWindow(); // get scene based on an element that in the scene, in this case it is friendRequestContent
        stage.setScene(scene);
        stage.show();
    }
}