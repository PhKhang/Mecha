package com.example.mechaadmin;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.function.Predicate;

import com.example.mechaadmin.bus.UserBUS;
import com.example.mechaadmin.dto.AccountDTO;
import com.example.mechaadmin.dto.GroupChatDTO;

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
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
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
        List<AccountDTO> originalData = null;

        Predicate<AccountDTO> filter = account -> false;
        String searchKey = "";

        @SuppressWarnings("unchecked")
        AccountTable(TableView<AccountDTO> table) {
            accountTable = table;
            accountCreation = (TableColumn<AccountDTO, String>) accountTable.getColumns().get(3);
            accountFull = (TableColumn<AccountDTO, String>) accountTable.getColumns().get(0);
            accountLog = (TableColumn<AccountDTO, String>) accountTable.getColumns().get(4);
            accountStatus = (TableColumn<AccountDTO, String>) accountTable.getColumns().get(2);
            accountUser = (TableColumn<AccountDTO, String>) accountTable.getColumns().get(1);

            accountUser.setCellValueFactory(new PropertyValueFactory<>("username"));
            accountFull.setCellValueFactory(new PropertyValueFactory<>("fullName"));
            accountStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
            accountCreation.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
            accountLog.setCellValueFactory(new PropertyValueFactory<>("recentLogin"));
            accountTable.setItems(accounts);
        }

        public void updateOriginal(List<AccountDTO> list) {
            originalData = new ArrayList<>(list);
            accounts.clear();
            accounts.addAll(list);
        }

        public void updateOriginal() {
            accounts.clear();
            accounts.addAll(originalData);
        }

        public void updateContent(List<AccountDTO> list) {
            accounts.clear();
            accounts.addAll(list);
        }

        public void search(String s) {
            searchKey = s.trim().toLowerCase();
            if (s.trim().equals("")) {
                List<AccountDTO> filtered = new ArrayList<>(originalData);
                filtered.removeIf(filter);
                accTable.updateContent(filtered);
            } else {
                List<AccountDTO> filtered = new ArrayList<>(originalData);
                filtered.removeIf(account -> !account.getFullName().toLowerCase().contains(searchKey)
                        && !account.getUsername().toLowerCase().contains(searchKey));
                filtered.removeIf(filter);
                accTable.updateContent(filtered);
            }
        }

        public void setFilterPredicate(Predicate<AccountDTO> filter) {
            this.filter = filter;
            List<AccountDTO> filtered = new ArrayList<>(originalData);
            filtered.removeIf(filter);
            accTable.updateContent(filtered);
            search(searchKey);
        }
    }

    // Cac nhom
    @FXML
    public TableView<GroupChatDTO> groupTable;

    class GroupChatTable {
        public TableView<GroupChatDTO> groupTable;
        private TableColumn<GroupChatDTO, String> groupCreation;
        private TableColumn<GroupChatDTO, String> groupMem;
        private TableColumn<GroupChatDTO, String> groupMemNum;
        private TableColumn<GroupChatDTO, String> groupName;

        ObservableList<GroupChatDTO> groups = FXCollections.observableArrayList();
        List<GroupChatDTO> originalData = null;

        Predicate<GroupChatDTO> filter = group -> false;
        String searchKey = "";

        @SuppressWarnings("unchecked")
        GroupChatTable(TableView<GroupChatDTO> table) {
            groupTable = table;
            groupName = (TableColumn<GroupChatDTO, String>) groupTable.getColumns().get(0);
            groupCreation = (TableColumn<GroupChatDTO, String>) groupTable.getColumns().get(1);
            groupMemNum = (TableColumn<GroupChatDTO, String>) groupTable.getColumns().get(2);
            groupMem = (TableColumn<GroupChatDTO, String>) groupTable.getColumns().get(3);

            groupName.setCellValueFactory(new PropertyValueFactory<>("groupName"));
            groupCreation.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
            groupMem.setCellValueFactory(cellData -> new SimpleStringProperty(
                    String.join(", ", cellData.getValue().getMembers())));
            groupMemNum.setCellValueFactory(new PropertyValueFactory<>("totalMembers"));
            groupTable.setItems(groups);
        }

        public void updateOriginal(List<GroupChatDTO> list) {
            originalData = new ArrayList<>(list);
            groups.clear();
            groups.addAll(list);
        }

        public void updateOriginal() {
            groups.clear();
            groups.addAll(originalData);
        }

        public void updateContent(List<GroupChatDTO> list) {
            groups.clear();
            groups.addAll(list);
        }

        public void search(String s) {
            searchKey = s.trim().toLowerCase();
            if (s.trim().equals("")) {
                List<GroupChatDTO> filtered = new ArrayList<>(originalData);
                filtered.removeIf(filter);
                grpTable.updateContent(filtered);
            } else {
                List<GroupChatDTO> filtered = new ArrayList<>(originalData);
                filtered.removeIf(group -> !group.getGroupName().toLowerCase().contains(searchKey));
                filtered.removeIf(filter);
                grpTable.updateContent(filtered);
            }
        }
    }

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
    GroupChatTable grpTable;

    @FXML
    TextField accountSearch;
    @FXML
    MenuButton accountMenu;
    @FXML
    TextField groupSearch;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        UserBUS userBUS = new UserBUS();
        accTable = new AccountTable(accountTable);
        grpTable = new GroupChatTable(groupTable);

        // ----------------- User data -----------------
        accTable.updateOriginal(userBUS.getAllUsers());

        accountSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            accTable.search(newValue);
        });
        accountMenu.getItems().addAll(new MenuItem("Tất cả"), new MenuItem("Đang hoạt động"),
                new MenuItem("Không hoạt động"));
        accountMenu.getItems().get(0).setOnAction((e) -> {
            Predicate<AccountDTO> filter = account -> false;
            accTable.setFilterPredicate(filter);
        });
        accountMenu.getItems().get(1).setOnAction((e) -> {
            Predicate<AccountDTO> filter = account -> !account.getStatus().toLowerCase().contains("online");
            accTable.setFilterPredicate(filter);
        });
        accountMenu.getItems().get(2).setOnAction((e) -> {
            Predicate<AccountDTO> filter = account -> !account.getStatus().toLowerCase().contains("offline");
            accTable.setFilterPredicate(filter);
        });

        // ----------------- Group data -----------------
        grpTable.updateOriginal(userBUS.getAllGroups());
        groupSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println(newValue);
            grpTable.search(newValue);
        });

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
    private VBox VMemList;

    @FXML
    public void groupClicked() {
        GroupChatDTO group = groupTable.getSelectionModel().getSelectedItem();
        VMemList.getChildren().clear();
        VMemList.getChildren().addAll(group.getMembers().stream().map(member -> {
            ImageView profile = new ImageView(new Image(
                    "https://pub-b0a9bdcea1cd4f6ca28d98f878366466.r2.dev/1733287277424-468916132_438255359333153_2130388637852454432_n.jpg",
                    40, 40, true, true));
            Circle clip = new Circle(20, 20, 20);
            profile.setClip(clip);
            // ImageView crown = new ImageView(new Image("..\\images\\crown.png", 20, 20, true, true));
            Text username = new Text(member);
            username.setFont(new Font(14));
            username.setTextAlignment(TextAlignment.CENTER);
            HBox memberBox = new HBox(
                    profile,
                    username);
            memberBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            return memberBox;
        }).toArray(HBox[]::new));
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
