package com.example.mechaadmin;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.ResourceBundle;

import com.example.mechaadmin.bus.UserBUS;
import com.example.mechaadmin.dto.AccountDTO;

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
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class SceneController implements Initializable {
    private Stage stage;
    private Scene scene;
    private Parent root;

    // Tai khoan ca nhan
    @FXML
    public TableView<AccountDTO> accountTable;

    class AccountTable {
        public TableView<AccountDTO> accountTable;
        public TableColumn<AccountDTO, String> accountCreation;
        public TableColumn<AccountDTO, String> accountFull;
        public TableColumn<AccountDTO, String> accountLog;
        public TableColumn<AccountDTO, String> accountStatus;
        public TableColumn<AccountDTO, String> accountUser;

        ObservableList<AccountDTO> accounts = FXCollections.observableArrayList();

        @SuppressWarnings("unchecked")
        AccountTable(TableView<AccountDTO> table) {
            accountTable = table;
            accountCreation = (TableColumn<AccountDTO, String>) accountTable.getColumns().get(0);
            accountFull = (TableColumn<AccountDTO, String>) accountTable.getColumns().get(1);
            accountLog = (TableColumn<AccountDTO, String>) accountTable.getColumns().get(2);
            accountStatus = (TableColumn<AccountDTO, String>) accountTable.getColumns().get(3);
            accountUser = (TableColumn<AccountDTO, String>) accountTable.getColumns().get(4);

            accountUser.setCellValueFactory(new PropertyValueFactory<>("username"));
            accountFull.setCellValueFactory(new PropertyValueFactory<>("fullName"));
            accountStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
            accountCreation.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
            accountLog.setCellValueFactory(new PropertyValueFactory<>("recentLogin"));
            accountTable.setItems(accounts);
        }
        
        public void updateContent(List<AccountDTO> list){
            accounts.clear();
            accounts.addAll(list);
        }
    }
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
    private TableView<AccountDTO> friendCount;
    @FXML
    private TableColumn<AccountDTO, String> friendName;
    @FXML
    private TableColumn<AccountDTO, String> friendUser;
    @FXML
    private TableColumn<AccountDTO, String> friendCreation;
    @FXML
    private TableColumn<AccountDTO, Integer> friendDirect;
    @FXML
    private TableColumn<AccountDTO, Integer> friendIndirect;

    @FXML
    private TableView<AccountDTO> loginTable;
    @FXML
    private TableColumn<AccountDTO, String> loginTime;
    @FXML
    private TableColumn<AccountDTO, String> loginUser;
    @FXML
    private TableColumn<AccountDTO, String> loginFull;

    @FXML
    private TableView<AccountDTO> activeTable;
    @FXML
    private TableColumn<AccountDTO, String> activeFull;
    @FXML
    private TableColumn<AccountDTO, String> activeUser;
    @FXML
    private TableColumn<AccountDTO, String> activeCreation;
    @FXML
    private TableColumn<AccountDTO, String> activeOpen;
    @FXML
    private TableColumn<AccountDTO, String> activeChat;
    @FXML
    private TableColumn<AccountDTO, String> activeGroup;

    ObservableList<Report> reports = FXCollections.observableArrayList(
            new Report("1", "Phuc Khang", "SpamLover", "Spamming", "22/11/2024", "Pending"),
            new Report("2", "Man the Man", "RuleBreaker", "Inappropriate Content", "23/11/2024", "Resolved"),
            new Report("3", "Van A", "Cheater", "Hacking", "24/11/2024", "Pending"),
            new Report("4", "Thi B", "Spammer", "Spamming", "25/11/2024", "Pending"),
            new Report("5", "Van C", "Hacker", "Hacking", "26/11/2024", "Resolved"),
            new Report("6", "Thi D", "Abuser", "Abuse", "27/11/2024", "Pending"),
            new Report("7", "Van F", "Spammer", "Spamming", "28/11/2024", "Resolved"),
            new Report("8", "Thi G", "Cheater", "Hacking", "29/11/2024", "Pending"),
            new Report("9", "Van H", "RuleBreaker", "Inappropriate Content", "30/11/2024", "Resolved"),
            new Report("10", "Thi I", "Abuser", "Abuse", "01/12/2024", "Pending"));
    @FXML
    private TableView<Report> reportTable;
    @FXML
    private TableColumn<Report, String> reportId;
    @FXML
    private TableColumn<Report, String> reportReporter;
    @FXML
    private TableColumn<Report, String> reportReported;
    @FXML
    private TableColumn<Report, String> reportReason;
    @FXML
    private TableColumn<Report, String> reportTime;
    @FXML
    private TableColumn<Report, String> reportStatus;

    @FXML
    private TableView<AccountDTO> newTable;
    @FXML
    private TableColumn<AccountDTO, String> newCreation;
    @FXML
    private TableColumn<AccountDTO, String> newFull;
    @FXML
    private TableColumn<AccountDTO, String> newUser;

    @FXML
    private ChoiceBox<String> choiceFriend;
    @FXML
    private ChoiceBox<String> choiceActiveAct;
    @FXML
    private ChoiceBox<String> choiceActiveCon;
    @FXML
    private ChoiceBox<String> choiceStatus;

    AccountTable accTable;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        UserBUS userBUS = new UserBUS();
        accTable = new AccountTable(accountTable);

        accTable.updateContent(userBUS.getAllUsers());
        System.out.println(accTable.accounts.size());
        System.out.println(accTable.accounts.get(0).getFullName());

        groupName.setCellValueFactory(new PropertyValueFactory<GroupChat, String>("groupChatName"));
        groupCreation.setCellValueFactory(new PropertyValueFactory<GroupChat, String>("creationDate"));
        groupMem.setCellValueFactory(cellData -> new SimpleStringProperty(
                String.join(", ", cellData.getValue().getMemNames())));
        groupMemNum.setCellValueFactory(
                cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getMembers().size())));
        groupTable.setItems(groups);

        friendName.setCellValueFactory(new PropertyValueFactory<AccountDTO, String>("fullName"));
        friendUser.setCellValueFactory(new PropertyValueFactory<AccountDTO, String>("username"));
        friendCreation.setCellValueFactory(new PropertyValueFactory<AccountDTO, String>("createdAt"));
        friendDirect.setCellValueFactory(new PropertyValueFactory<AccountDTO, Integer>("directFriends"));
        friendIndirect.setCellValueFactory(new PropertyValueFactory<AccountDTO, Integer>("indirectFriends"));
        friendCount.setItems(accTable.accounts);

        loginTime.setCellValueFactory(new PropertyValueFactory<AccountDTO, String>("recentLogin"));
        loginUser.setCellValueFactory(new PropertyValueFactory<AccountDTO, String>("username"));
        loginFull.setCellValueFactory(new PropertyValueFactory<AccountDTO, String>("fullName"));
        loginTable.setItems(accTable.accounts);

        activeFull.setCellValueFactory(new PropertyValueFactory<AccountDTO, String>("fullName"));
        activeUser.setCellValueFactory(new PropertyValueFactory<AccountDTO, String>("username"));
        activeCreation.setCellValueFactory(new PropertyValueFactory<AccountDTO, String>("createdAt"));
        Random random = new Random(1);
        activeOpen.setCellValueFactory(cellData -> {
            return new SimpleStringProperty(String.valueOf(random.nextInt(20) + 2));
        });
        activeChat.setCellValueFactory(cellData -> {
            return new SimpleStringProperty(String.valueOf(random.nextInt(10) + 1));
        });
        activeGroup.setCellValueFactory(cellData -> {
            return new SimpleStringProperty(String.valueOf(random.nextInt(5)));
        });
        activeTable.setItems(accTable.accounts);

        reportId.setCellValueFactory(new PropertyValueFactory<Report, String>("reportId"));
        reportReporter.setCellValueFactory(new PropertyValueFactory<Report, String>("reportReporter"));
        reportReported.setCellValueFactory(new PropertyValueFactory<Report, String>("reportReported"));
        reportReason.setCellValueFactory(new PropertyValueFactory<Report, String>("reportReason"));
        reportTime.setCellValueFactory(new PropertyValueFactory<Report, String>("reportDate"));
        reportStatus.setCellValueFactory(new PropertyValueFactory<Report, String>("reportStatus"));
        reportTable.setItems(reports);

        choiceFriend.getItems().addAll("lớn hơn", "nhỏ hơn", "bằng");
        choiceActiveAct.getItems().addAll("Mở ứng dụng", "Chat cá nhân", "Chat nhóm");
        choiceActiveCon.getItems().addAll("lớn hơn", "nhỏ hơn", "bằng");
        choiceStatus.getItems().addAll("Pending", "Resolved", "Under Review");

        newCreation.setCellValueFactory(new PropertyValueFactory<AccountDTO, String>("createdAt"));
        newFull.setCellValueFactory(new PropertyValueFactory<AccountDTO, String>("fullName"));
        newUser.setCellValueFactory(new PropertyValueFactory<AccountDTO, String>("username"));
        newTable.setItems(accTable.accounts);
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
        AccountDTO account = accTable.accountTable.getSelectionModel().getSelectedItem();
        boxFull.setText(String.valueOf(account.getFullName()));
        boxUser.setText(String.valueOf(account.getUsername()));
        boxAddr.setText(String.valueOf(account.getAddress()));
        boxDob.setText(String.valueOf(account.getDob()));
        boxEmail.setText(String.valueOf(account.getEmail()));
        boxGen.setText(String.valueOf(account.getGender()));
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

    @FXML
    public void handleMouseClick(MouseEvent event) {
        System.out.println("Clicked");
    }

    @FXML
    public void clickToProfile(MouseEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("views/profile.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("styles/app.css")).toExternalForm());
        stage.setScene(scene);
        stage.show();
    }
}
