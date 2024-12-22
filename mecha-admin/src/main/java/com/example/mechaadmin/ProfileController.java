package com.example.mechaadmin;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.example.mechaadmin.bus.UserBUS;
import com.example.mechaadmin.dto.AccountDTO;
import com.example.mechaadmin.dto.RecentLoginDTO;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
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
    ChoiceBox<String> gender;
    @FXML
    DatePicker dob;

    @FXML
    Button khoa;
    @FXML
    Button luu;

    @FXML
    TableView<Object[]> friendListTable;
    @FXML
    TableView<RecentLoginDTO> logHistoryTable;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gender.getItems().addAll("Male", "Female");
        
        if (account != null) {
            full.setText(account.getFullName() != null ? account.getFullName() : "");
            username.setText(account.getUsername() != null ? account.getUsername() : "");
            email.setText(account.getEmail() != null ? account.getEmail() : "");
            address.setText(account.getAddress() != null ? account.getAddress() : "");
            gender.setValue(account.getGender() != null ? account.getGender() : "");
            dob.setValue(account.getDob() != null ? account.getDob().toLocalDate() : null);

            if (account.getAdminAction() != null && account.getAdminAction().equals("locked"))
                khoa.setText("Mở khóa");

            Thread aThread = new Thread() {
                public void run() {
                    while (true) {
                        final List<Object[]> friends = new ArrayList<>();
                        final List<RecentLoginDTO> logins = new ArrayList<>();
                        if (account != null && account.getUserId() != null) {
                            friends.addAll(UserBUS.getFriends(account.getUserId()));
                            logins.addAll(UserBUS.getRecentLogin(account.getUserId()));
                            System.out.println("Reload FRIENDS");
                        }
                        Platform.runLater(new Runnable() {
                            public void run() {
                                friendListTable.getColumns().clear();
                                friendListTable.getItems().clear();
                                TableColumn<Object[], String> column1 = new TableColumn<>("Username");
                                column1.setCellValueFactory(
                                        cellData -> new SimpleStringProperty((String) cellData.getValue()[0]));
                                TableColumn<Object[], String> column2 = new TableColumn<>("Họ tên");
                                column2.setPrefWidth(150);
                                column2.setCellValueFactory(
                                        cellData -> new SimpleStringProperty((String) cellData.getValue()[1]));

                                friendListTable.getColumns().addAll(column1, column2);

                                friendListTable.getItems().addAll(friends);

                                // ------------

                                logHistoryTable.getColumns().clear();
                                logHistoryTable.getItems().clear();
                                TableColumn<RecentLoginDTO, String> columnLog1 = new TableColumn<>("Thời gian");
                                columnLog1.setPrefWidth(300);
                                columnLog1.setCellValueFactory(new PropertyValueFactory<>("time"));
                                // TableColumn<RecentLoginDTO, String> columnLog2 = new TableColumn<>("Họ tên");
                                // columnLog2.setCellValueFactory(new PropertyValueFactory<>("username"));

                                logHistoryTable.getColumns().addAll(columnLog1);

                                logHistoryTable.getItems().addAll(logins);

                                System.out.println(friends.size());
                            }
                        });

                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                }
            };

            aThread.start();
        }
        else {
            luu.setText("Tạo");
        }
    }

    public void updateProfile(ActionEvent event) {
        account.setFullName(full.getText());
        account.setUsername(username.getText());
        account.setEmail(email.getText());
        account.setAddress(address.getText());
        account.setGender(gender.getValue());
        account.setDob(dob.getValue() != null ? dob.getValue().atStartOfDay() : null);
        if (luu.getText().equals("luu")) {
            UserBUS.updateAccount(account);
        } else {
            account.setCreatedAt(LocalDateTime.now());
            UserBUS.saveAccount(account);
        }
        System.out.println("The username is: " + account.getUsername());
        // System.out.println("The user id is: " + account.getUserId());
    }

    public void lockProfile(ActionEvent event) {
        if (account == null)
            return;

        if (khoa.getText().equals("Khóa người dùng")) {
            UserBUS.lockAccount(account.getUserId());
            System.out.println("Lock user");
            System.out.println(account.getUsername() + " is now locked");
            khoa.setText("Mở khóa");
        } else {
            UserBUS.unlockAccount(account.getUserId());
            System.out.println("Unlock user");
            khoa.setText("Khóa người dùng");
        }
    }

    public void removeProfile(ActionEvent event) {
        if (account == null)
            return;

        UserBUS.deleteAccount(account.getUserId());
        System.out.println("Removed a user");
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
    }
}
