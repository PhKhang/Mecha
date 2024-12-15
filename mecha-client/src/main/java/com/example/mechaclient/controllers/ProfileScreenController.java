package com.example.mechaclient.controllers;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import com.example.mechaclient.ChatApplication;
import com.example.mechaclient.models.UserSession;
import com.example.mechaclient.models.UserSession.ServerMessageListener;
import com.example.mechaclient.utils.NotificationUtil;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

public class ProfileScreenController implements ServerMessageListener {
    
    @FXML private ImageView profileImage;
    @FXML private Circle clipCircle;
    @FXML private Label displayName;
    @FXML private Label displayEmail;
    
    @FXML private Button editButton;
    @FXML private Button confirmButton;
    @FXML private Button cancelButton;
    @FXML private HBox actionButtons;
    
    @FXML private TextField fullNameField;
    @FXML private TextField addressField;
    @FXML private TextField usernameField;
    @FXML private ChoiceBox<String> genderField;
    @FXML private DatePicker dobField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    
    @FXML private GridPane formInfo;
    final Object lock = new Object();

    @FXML
    public void initialize() {
        UserSession.getInstance().addMessageListener(this);

        formInfo.setPadding(new Insets(20, 30, 20, 30));
        // Store initial values for cancel operation
        storeInitialValues();
        ObservableList<String> genderOptions = FXCollections.observableArrayList("Male", "Female", "Other");
        genderField.setItems(genderOptions);
        usernameField.setEditable(false);
        usernameField.getStyleClass().setAll("text-field", "non-editable-field");
        setEditMode(false);

        loadUserProfile();
    }

    private void loadUserProfile(){
        try {
            UserSession.out.writeObject("GET_USER_PROFILE");
            UserSession.out.writeObject(UserSession.getInstance().getUserId());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEdit() {
        storeInitialValues();
        setEditMode(true);
    }
    
    @FXML
    private void handleConfirm() {
        if (!isValidEmail(emailField.getText())) {
            NotificationUtil.showNotification("Invalid Email", "Please enter a valid email address.");
            return;
        }
        // if (!isValidDateFormat(dobField.getText())) {
        //     NotificationUtil.showNotification("Invalid Date Format", "Please enter the date in YYYY-MM-DD format.");
        //     return;
        // }

        try {
            UserSession.out.writeObject("UPDATE_USER_PROFILE");
            // info order: user_id, fullname, gender, address, email, date_of_birth 
            UserSession.out.writeObject(UserSession.getInstance().getUserId());
            UserSession.out.writeObject(fullNameField.getText());
            UserSession.out.writeObject(genderField.getValue());
            UserSession.out.writeObject(addressField.getText());
            UserSession.out.writeObject(emailField.getText());

            LocalDate dob = dobField.getValue();
            String formattedDate = dob.format(DateTimeFormatter.ISO_DATE);
            UserSession.out.writeObject(formattedDate);

            UserSession.getInstance().setFullname(fullNameField.getText()); // update this to get the correct name label in homescreen view(fullnameLabel)
            loadUserProfile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        setEditMode(false);
        updateDisplayValues();
    }
    
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email.matches(emailRegex);
    }

    @FXML
    private void handleCancel() {
        setEditMode(false);
        restoreInitialValues();
    }
    
    @FXML
    private void handleChangePassword() {
        Dialog<ButtonType> dialog = createPasswordChangeDialog();
        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get().getButtonData() == ButtonBar.ButtonData.OK_DONE) {
            PasswordField oldPasswordField = (PasswordField) dialog.getDialogPane().lookup("#oldPassword");
            PasswordField newPasswordField = (PasswordField) dialog.getDialogPane().lookup("#newPassword");
            PasswordField confirmPasswordField = (PasswordField) dialog.getDialogPane().lookup("#confirmPassword");

            String oldPassword = oldPasswordField.getText();
            String newPassword = newPasswordField.getText();
            String confirmPassword = confirmPasswordField.getText();

            if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()){
                NotificationUtil.showAlert(Alert.AlertType.INFORMATION, "Missing Field", "Please filling out all of the fields");
                handleChangePassword();
                return;
            }
            if (!newPassword.equals(confirmPassword)) {
                NotificationUtil.showAlert(Alert.AlertType.ERROR, "Error", "New password and confirm password do not match.");
                handleChangePassword();
                return;
            }
            try {
                UserSession.out.writeObject("UPDATE_PASSWORD");
                UserSession.out.writeObject(UserSession.getInstance().getUserId());
                UserSession.out.writeObject(oldPassword);
                UserSession.out.writeObject(newPassword);
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    private Dialog<ButtonType> createPasswordChangeDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Change Password");
        dialog.setHeaderText("Please enter your old and new passwords");

        ButtonType confirmButtonType = new ButtonType("Confirm Change", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        PasswordField oldPassword = new PasswordField();
        oldPassword.setId("oldPassword");
        PasswordField newPassword = new PasswordField();
        newPassword.setId("newPassword");
        PasswordField confirmPassword = new PasswordField();
        confirmPassword.setId("confirmPassword");

        

        grid.add(new Label("Old Password:"), 0, 0);
        grid.add(oldPassword, 1, 0);
        grid.add(new Label("New Password:"), 0, 1);
        grid.add(newPassword, 1, 1);
        grid.add(new Label("Confirm Password:"), 0, 2);
        grid.add(confirmPassword, 1, 2);

        dialog.getDialogPane().setContent(grid);

        Button confirmButton = (Button) dialog.getDialogPane().lookupButton(confirmButtonType);
        Button cancelButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL);

        confirmButton.getStyleClass().add("confirm-button");
        cancelButton.getStyleClass().add("cancel-button");

        dialog.getDialogPane().getStylesheets().add(ChatApplication.class.getResource("styles/ProfileStyle.css").toExternalForm());
        dialog.getDialogPane().getStyleClass().add("change-password-dialog");

        return dialog;
    }
    
    private void setEditMode(boolean editMode) {
        // Toggle visibility of edit/confirm/cancel buttons
        editButton.setVisible(!editMode);
        confirmButton.setVisible(editMode);
        cancelButton.setVisible(editMode);
        
        // Toggle field editability
        fullNameField.setEditable(editMode);

        genderField.setDisable(!editMode);

        addressField.setEditable(editMode);
        

        dobField.setDisable(!editMode);

        emailField.setEditable(editMode);
        
        // Update styles
        String fieldStyle = editMode ? "editable-field" : "non-editable-field";
        fullNameField.getStyleClass().setAll("text-field", fieldStyle);
        genderField.getStyleClass().add(fieldStyle);
        addressField.getStyleClass().setAll("text-field", fieldStyle);
        dobField.getStyleClass().add(fieldStyle);
        emailField.getStyleClass().setAll("text-field", fieldStyle);
    }
    
    private String[] initialValues = new String[5];
    private LocalDate initialDob;

    private void storeInitialValues() {
        initialValues[0] = fullNameField.getText();
        initialValues[1] = (String)genderField.getValue();
        initialValues[2] = addressField.getText();
        initialValues[3] = usernameField.getText();
        initialValues[4] = emailField.getText();
        initialDob = dobField.getValue();
    }
    
    private void restoreInitialValues() {
        fullNameField.setText(initialValues[0]);
        genderField.setValue(initialValues[1]);
        addressField.setText(initialValues[2]);
        usernameField.setText(initialValues[3]);
        emailField.setText(initialValues[4]);
        dobField.setValue(initialDob);
    }
    
    private void updateDisplayValues() {
        displayName.setText(fullNameField.getText());
        displayEmail.setText(emailField.getText());
    }

    @FXML
    private void returnToHomeScreen(ActionEvent event) throws IOException {
        UserSession.getInstance().removeMessageListener(this);
        FXMLLoader fxmlLoader = new FXMLLoader(ChatApplication.class.getResource("views/HomeScreen.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        stage = (Stage) fullNameField.getScene().getWindow(); // get scene based on an element that in the scene, in this case it is friendRequestContent
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void onMessageReceived(String serverMessage) {
        try {
            System.out.println("getting respond from server in ProfileScreen: " + serverMessage);
            
            if ("respond_GET_USER_PROFILE".equals(serverMessage)) {
                //Info: fullname, email, gender, dob, address
                List<String> userInfo = (List<String>) UserSession.getInstance().in.readObject();

                Platform.runLater(() -> {
                    fullNameField.setText(userInfo.get(0));
                    emailField.setText(userInfo.get(1));
                    genderField.setValue(userInfo.get(2));

                    LocalDate date = LocalDate.parse(userInfo.get(3), DateTimeFormatter.ISO_DATE);
                    dobField.setValue(date);

                    addressField.setText(userInfo.get(4));
                    usernameField.setText(UserSession.getInstance().getUsername());

                    updateDisplayValues();
                });
            } else if ("respond_UPDATE_PASSWORD".equals(serverMessage)){
                String response = (String) UserSession.in.readObject();
                Platform.runLater(() -> {
                    if ("SUCCESS".equals(response)){
                        System.out.println("change pass success");
                        NotificationUtil.showAlert(Alert.AlertType.INFORMATION, "Success", "Change password successfully.");
                    } else {
                        NotificationUtil.showAlert(Alert.AlertType.ERROR, "Error", "Old password is not correct.");
                        handleChangePassword();
                    }
                });
          
            }
        } catch (Exception e){
            System.out.println("Error handling response in ProfileScreen");
        }
    }
}