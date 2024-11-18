package com.example.mechaadmin;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class SceneController implements Initializable {
    private Stage stage;
    private Scene scene;
    private Parent root;

    // Tai khoan ca nhan
    @FXML
    private TableView<Account> accountTable;

    @FXML
    private TableColumn<Account, String> accountCreation;

    @FXML
    private TableColumn<Account, String> accountFull;

    @FXML
    private TableColumn<Account, String> accountLog;

    @FXML
    private TableColumn<Account, String> accountStatus;

    @FXML
    private TableColumn<Account, String> accountUser;

    ObservableList<Account> accounts = FXCollections.observableArrayList(
            new Account("Trần Nguyễn Phúc Khang", "Phuc Khang", "Online", "22/11/2024", "23/11/2024 12:00:00",
                    "khang@example.com", "123 Đường ABC, Hà Nội", "01/01/1990", "Nam", 5, 10),
            new Account("Lê Trí Mẩn", "Man the Man", "Offline", "9/11/2024", "11/11/2024 10:34:00", "man@example.com",
                    "456 Đường DEF, TP.HCM", "02/02/1991", "Nam", 3, 8),
            new Account("Nguyễn Văn A", "Van A", "Online", "01/01/2024", "01/01/2024 08:00:00", "vana@example.com",
                    "789 Đường GHI, Đà Nẵng", "03/03/1992", "Nam", 7, 12),
            new Account("Trần Thị B", "Thi B", "Offline", "02/02/2024", "02/02/2024 09:00:00", "thib@example.com",
                    "101 Đường JKL, Cần Thơ", "04/04/1993", "Nữ", 4, 9),
            new Account("Lê Văn C", "Van C", "Online", "03/03/2024", "03/03/2024 10:00:00", "vanc@example.com",
                    "202 Đường MNO, Hải Phòng", "05/05/1994", "Nam", 6, 11),
            new Account("Phạm Thị D", "Thi D", "Offline", "04/04/2024", "04/04/2024 11:00:00", "thid@example.com",
                    "303 Đường PQR, Huế", "06/06/1995", "Nữ", 2, 7),
            new Account("Nguyễn Văn F", "Van F", "Online", "06/06/2024", "06/06/2024 13:00:00", "vanf@example.com",
                    "404 Đường STU, Nha Trang", "07/07/1996", "Nam", 5, 10),
            new Account("Trần Thị G", "Thi G", "Offline", "07/07/2024", "07/07/2024 14:00:00", "thig@example.com",
                    "505 Đường VWX, Vũng Tàu", "08/08/1997", "Nữ", 3, 8),
            new Account("Lê Văn H", "Van H", "Online", "08/08/2024", "08/08/2024 15:00:00", "vanh@example.com",
                    "606 Đường YZ, Quy Nhơn", "09/09/1998", "Nam", 7, 12),
            new Account("Phạm Thị I", "Thi I", "Offline", "09/09/2024", "09/09/2024 16:00:00", "thii@example.com",
                    "707 Đường ABC, Biên Hòa", "10/10/1999", "Nữ", 4, 9),
            new Account("Hoàng Văn E", "Van E", "Online", "05/05/2024", "05/05/2024 12:00:00", "vane@example.com",
                    "808 Đường DEF, Buôn Ma Thuột", "11/11/2000", "Nam", 6, 11));

    // Cac nhom

    @FXML
    private TableColumn<GroupChat, String> groupCreation;

    @FXML
    private TableColumn<GroupChat, String> groupMem;

    @FXML
    private TableColumn<GroupChat, String> groupMemNum;

    @FXML
    private TableColumn<GroupChat, String> groupName;

    @FXML
    private TableView<GroupChat> groupTable;

    ObservableList<GroupChat> groups = FXCollections.observableArrayList(
            new GroupChat("Nhóm 1", "22/11/2024"),
            new GroupChat("Nhóm 2", "23/11/2024"),
            new GroupChat("Nhóm 3", "24/11/2024"),
            new GroupChat("Nhóm 4", "25/11/2024"),
            new GroupChat("Nhóm 5", "26/11/2024"),
            new GroupChat("Nhóm 6", "27/11/2024"),
            new GroupChat("Nhóm 7", "28/11/2024"),
            new GroupChat("Nhóm 8", "29/11/2024"),
            new GroupChat("Nhóm 9", "30/11/2024"),
            new GroupChat("Nhóm 10", "01/12/2024"));

    @FXML
    private TableView<Account> friendCount;
    @FXML
    private TableColumn<Account, String> friendName;
    @FXML
    private TableColumn<Account, String> friendUser;
    @FXML
    private TableColumn<Account, String> friendCreation;
    @FXML
    private TableColumn<Account, Integer> friendDirect;
    @FXML
    private TableColumn<Account, Integer> friendIndirect;
            
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        accountFull.setCellValueFactory(new PropertyValueFactory<Account, String>("accountFull"));
        accountUser.setCellValueFactory(new PropertyValueFactory<Account, String>("accountUser"));
        accountStatus.setCellValueFactory(new PropertyValueFactory<Account, String>("accountStatus"));
        accountCreation.setCellValueFactory(new PropertyValueFactory<Account, String>("accountCreation"));
        accountLog.setCellValueFactory(new PropertyValueFactory<Account, String>("accountLog"));
        accountTable.setItems(accounts);

        groupName.setCellValueFactory(new PropertyValueFactory<GroupChat, String>("groupChatName"));
        groupCreation.setCellValueFactory(new PropertyValueFactory<GroupChat, String>("creationDate"));
        groupMem.setCellValueFactory(cellData -> new SimpleStringProperty(
                String.join(", ", cellData.getValue().getMemNames())));
        groupMemNum.setCellValueFactory(
                cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getMembers().size())));
        groupTable.setItems(groups);
        
        friendName.setCellValueFactory(new PropertyValueFactory<Account, String>("accountFull"));
        friendUser.setCellValueFactory(new PropertyValueFactory<Account, String>("accountUser"));
        friendCreation.setCellValueFactory(new PropertyValueFactory<Account, String>("accountCreation"));
        friendDirect.setCellValueFactory(new PropertyValueFactory<Account, Integer>("directFriends"));
        friendIndirect.setCellValueFactory(new PropertyValueFactory<Account, Integer>("indirectFriends"));
        friendCount.setItems(accounts);
    }

    @FXML
    private TextField boxFull;

    @FXML
    private TextField boxUser;

    @FXML
    private TextField boxAddr;

    @FXML
    private TextField boxDob;

    @FXML
    private TextField boxEmail;

    @FXML
    private TextField boxGen;

    // Tai khoan ca nhan
    @FXML
    public void accountClicked() {
        Account account = accountTable.getSelectionModel().getSelectedItem();
        boxFull.setText(String.valueOf(account.getAccountFull()));
        boxUser.setText(String.valueOf(account.getAccountUser()));
        boxAddr.setText(String.valueOf(account.getAccountAddress()));
        boxDob.setText(String.valueOf(account.getAccountDob()));
        boxEmail.setText(String.valueOf(account.getAccountEmail()));
        boxGen.setText(String.valueOf(account.getAccountGender()));
    }

    // Cac nhom
    @FXML
    private ScrollPane memList;

    @FXML
    public void groupClicked() {
        // GroupChat group = groupTable.getSelectionModel().getSelectedItem();
        memList.setVisible(true);

    }

    public void switchToMain(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("views/main.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void switchToProfile(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("views/profile.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
