package com.example.mechaadmin;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.example.mechaadmin.dto.AccountDTO;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ProfileController implements Initializable {
    private Stage stage;
    private Scene scene;
    private Parent root;
    static AccountDTO account;

    @FXML
    TextField full;
    @FXML
    TextField username;
    @FXML
    TextField email;
    @FXML
    TextField address;
    @FXML
    TextField gender;
    @FXML
    TextField dob;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        full.setText(account.getFullName());
        username.setText(account.getUsername());
        email.setText(account.getEmail());
        address.setText(account.getAddress());
        dob.setText(account.getDob());
    }

    public void switchToMain(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("views/main.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    
    static void setAccount(AccountDTO a) {
        account = a;
        System.out.println("The username is: " + account.getUsername());
    }
}
