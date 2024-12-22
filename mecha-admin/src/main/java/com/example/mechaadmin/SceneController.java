package com.example.mechaadmin;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.example.mechaadmin.bus.ReportBUS;
import com.example.mechaadmin.bus.UsageBUS;
import com.example.mechaadmin.bus.UserBUS;
import com.example.mechaadmin.dto.AccountDTO;
import com.example.mechaadmin.dto.ActivityDTO;
import com.example.mechaadmin.dto.GroupChatDTO;
import com.example.mechaadmin.dto.RecentLoginDTO;
import com.example.mechaadmin.dto.ReportInfoDTO;
import com.example.mechaadmin.dto.UsageDTO;

import javafx.application.Platform;
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
import javafx.scene.chart.AreaChart;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
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
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

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
    private TableView<RecentLoginDTO> loginTable;
    @FXML
    private TableColumn<RecentLoginDTO, String> loginTime;
    @FXML
    private TableColumn<RecentLoginDTO, String> loginUser;
    @FXML
    private TableColumn<RecentLoginDTO, String> loginFull;
    @FXML
    private TextField loginSearch;

    class LoginTable {
        private TableView<RecentLoginDTO> loginTable;
        private TableColumn<RecentLoginDTO, String> loginTime;
        private TableColumn<RecentLoginDTO, String> loginUser;
        private TableColumn<RecentLoginDTO, String> loginFull;

        ObservableList<RecentLoginDTO> accounts = FXCollections.observableArrayList();
        List<RecentLoginDTO> originalData = null;

        Predicate<RecentLoginDTO> filter = account -> false;
        String searchKey = "";

        @SuppressWarnings("unchecked")
        LoginTable(TableView<RecentLoginDTO> table) {
            loginTable = table;
            loginTime = (TableColumn<RecentLoginDTO, String>) loginTable.getColumns().get(0);
            loginFull = (TableColumn<RecentLoginDTO, String>) loginTable.getColumns().get(1);
            loginUser = (TableColumn<RecentLoginDTO, String>) loginTable.getColumns().get(2);

            loginUser.setCellValueFactory(new PropertyValueFactory<>("username"));
            loginFull.setCellValueFactory(new PropertyValueFactory<>("fullName"));
            loginTime.setCellValueFactory(cellData -> new SimpleStringProperty(
                    cellData.getValue().getTime().toString()));
            loginTable.setItems(accounts);
        }

        public void updateOriginal(List<RecentLoginDTO> list) {
            originalData = new ArrayList<>(list);
            accounts.clear();
            accounts.addAll(list);
        }

        public void updateOriginal() {
            accounts.clear();
            accounts.addAll(originalData);
        }

        public void updateContent(List<RecentLoginDTO> list) {
            accounts.clear();
            accounts.addAll(list);
        }

        public void search(String s) {
            searchKey = s.trim().toLowerCase();
            if (s.trim().equals("")) {
                List<RecentLoginDTO> filtered = new ArrayList<>(originalData);
                filtered.removeIf(filter);
                loginTable.getItems().clear();
                loginTable.getItems().addAll(filtered);
            } else {
                List<RecentLoginDTO> filtered = new ArrayList<>(originalData);
                filtered.removeIf(account -> !account.getFullName().toLowerCase().contains(searchKey)
                        && !account.getUsername().toLowerCase().contains(searchKey));
                filtered.removeIf(filter);
                loginTable.getItems().clear();
                loginTable.getItems().addAll(filtered);
            }
        }

        // public void setFilterPredicate(Predicate<RecentLoginDTO> filter) {
        // this.filter = filter;
        // List<AccountDTO> filtered = new ArrayList<>(originalData);
        // filtered.removeIf(filter);
        // accounts.clear();
        // accounts.addAll(filtered);
        // search(searchKey);
        // }
    }

    @FXML
    private TableView<ActivityDTO> activeTable;
    @FXML
    private TextField activeSearch;
    @FXML
    private DatePicker activeStart;
    @FXML
    private DatePicker activeEnd;
    @FXML
    private TableColumn<ActivityDTO, String> activeFull;
    @FXML
    private TableColumn<ActivityDTO, String> activeUser;
    @FXML
    private TableColumn<ActivityDTO, String> activeCreation;
    @FXML
    private TableColumn<ActivityDTO, String> activeOpen;
    @FXML
    private TableColumn<ActivityDTO, String> activeChat;
    @FXML
    private TableColumn<ActivityDTO, String> activeGroup;

    class ActivityTable {
        private TableView<ActivityDTO> activeTable;
        private TableColumn<ActivityDTO, String> activeFull;
        private TableColumn<ActivityDTO, String> activeUser;
        private TableColumn<ActivityDTO, String> activeCreation;
        private TableColumn<ActivityDTO, String> activeOpen;
        private TableColumn<ActivityDTO, String> activeChat;
        private TableColumn<ActivityDTO, String> activeGroup;

        ObservableList<ActivityDTO> activities = FXCollections.observableArrayList();
        List<ActivityDTO> originalData = null;

        Predicate<ActivityDTO> filter = activity -> false;
        String searchKey = "";

        @SuppressWarnings("unchecked")
        ActivityTable(TableView<ActivityDTO> table) {
            activeTable = table;
            activeFull = (TableColumn<ActivityDTO, String>) activeTable.getColumns().get(0);
            activeUser = (TableColumn<ActivityDTO, String>) activeTable.getColumns().get(1);
            activeCreation = (TableColumn<ActivityDTO, String>) activeTable.getColumns().get(2);
            activeOpen = (TableColumn<ActivityDTO, String>) activeTable.getColumns().get(3);
            activeChat = (TableColumn<ActivityDTO, String>) activeTable.getColumns().get(4);
            activeGroup = (TableColumn<ActivityDTO, String>) activeTable.getColumns().get(5);

            activeUser.setCellValueFactory(new PropertyValueFactory<>("username"));
            activeFull.setCellValueFactory(new PropertyValueFactory<>("fullName"));
            activeCreation.setCellValueFactory(new PropertyValueFactory<>("creationDate"));
            activeOpen.setCellValueFactory(new PropertyValueFactory<>("timeOpened"));
            activeChat.setCellValueFactory(new PropertyValueFactory<>("privateChat"));
            activeGroup.setCellValueFactory(new PropertyValueFactory<>("groupChat"));
            activeTable.setItems(activities);
        }

        public void updateOriginal(List<ActivityDTO> list) {
            originalData = new ArrayList<>(list);
            activities.clear();
            activities.addAll(list);
        }

        public void updateOriginal() {
            activities.clear();
            activities.addAll(originalData);
        }

        public void updateContent(List<ActivityDTO> list) {
            activities.clear();
            activities.addAll(list);
        }

        public void search(String s) {
            searchKey = s.trim().toLowerCase();
            if (s.trim().equals("")) {
                List<ActivityDTO> filtered = new ArrayList<>(originalData);
                filtered.removeIf(filter);
                activeTable.getItems().clear();
                activeTable.getItems().addAll(filtered);
            } else {
                List<ActivityDTO> filtered = new ArrayList<>(originalData);
                filtered.removeIf(activity -> !activity.getFullName().toLowerCase().contains(searchKey)
                        && !activity.getUsername().toLowerCase().contains(searchKey));
                filtered.removeIf(filter);
                activeTable.getItems().clear();
                activeTable.getItems().addAll(filtered);
            }
        }

        public void setFilterPredicate(Predicate<ActivityDTO> filter) {
            this.filter = filter;
            List<ActivityDTO> filtered = new ArrayList<>(originalData);
            filtered.removeIf(filter);
            activities.clear();
            activities.addAll(filtered);
            search(searchKey);
        }
    }

    // Report
    @FXML
    private TableView<ReportInfoDTO> reportTable;

    class ReportInfoTable {
        private TableView<ReportInfoDTO> reportTable;
        private TableColumn<ReportInfoDTO, String> reportId;
        private TableColumn<ReportInfoDTO, String> reportReporter;
        private TableColumn<ReportInfoDTO, String> reportReported;
        private TableColumn<ReportInfoDTO, String> reportReason;
        private TableColumn<ReportInfoDTO, String> reportTime;
        private TableColumn<ReportInfoDTO, String> reportStatus;

        ObservableList<ReportInfoDTO> reports = FXCollections.observableArrayList();
        List<ReportInfoDTO> originalData = null;

        Predicate<ReportInfoDTO> filter = report -> false;
        String searchKey = "";

        @SuppressWarnings("unchecked")
        ReportInfoTable(TableView<ReportInfoDTO> table) {
            reportTable = table;
            reportId = (TableColumn<ReportInfoDTO, String>) reportTable.getColumns().get(0);
            reportReporter = (TableColumn<ReportInfoDTO, String>) reportTable.getColumns().get(1);
            reportReported = (TableColumn<ReportInfoDTO, String>) reportTable.getColumns().get(2);
            reportReason = (TableColumn<ReportInfoDTO, String>) reportTable.getColumns().get(3);
            reportTime = (TableColumn<ReportInfoDTO, String>) reportTable.getColumns().get(4);
            reportStatus = (TableColumn<ReportInfoDTO, String>) reportTable.getColumns().get(5);

            reportId.setCellValueFactory(new PropertyValueFactory<>("reportId"));
            reportReporter.setCellValueFactory(new PropertyValueFactory<>("reporter"));
            reportReported.setCellValueFactory(new PropertyValueFactory<>("reported"));
            reportReason.setCellValueFactory(new PropertyValueFactory<>("reason"));
            reportTime.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
            reportStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
            reportTable.setItems(reports);
        }

        public void updateOriginal(List<ReportInfoDTO> list) {
            originalData = new ArrayList<>(list);
            reports.clear();
            reports.addAll(list);
        }

        public void updateOriginal() {
            reports.clear();
            reports.addAll(originalData);
        }

        public void updateContent(List<ReportInfoDTO> list) {
            reports.clear();
            reports.addAll(list);
        }

        public void search(String s) {
            searchKey = s.trim().toLowerCase();
            if (s.trim().equals("")) {
                List<ReportInfoDTO> filtered = new ArrayList<>(originalData);
                filtered.removeIf(filter);
                reportTable.getItems().clear();
                reportTable.getItems().addAll(filtered);
            } else {
                List<ReportInfoDTO> filtered = new ArrayList<>(originalData);
                filtered.removeIf(report -> !report.getReporter().toLowerCase().contains(searchKey)
                        && !report.getReported().toLowerCase().contains(searchKey)
                        && !report.getReason().toLowerCase().contains(searchKey)
                        && !report.getStatus().toLowerCase().contains(searchKey));
                filtered.removeIf(filter);
                reportTable.getItems().clear();
                reportTable.getItems().addAll(filtered);
            }
        }

        public void setFilterPredicate(Predicate<ReportInfoDTO> filter) {
            this.filter = filter;
            List<ReportInfoDTO> filtered = new ArrayList<>(originalData);
            filtered.removeIf(filter);
            reports.clear();
            reports.addAll(filtered);
            search(searchKey);
        }
    }

    ReportInfoTable rprtTable;

    @FXML
    private TableView<AccountDTO> newTable;
    @FXML
    AreaChart<String, Integer> newChart;
    @FXML
    private DatePicker newStart;
    @FXML
    private DatePicker newEnd;
    @FXML
    private TextField newSearch;

    class NewTable {
        public TableView<AccountDTO> newTable;
        public AreaChart<String, Integer> lineChart;
        public TableColumn<AccountDTO, String> newCreation;
        public TableColumn<AccountDTO, String> newFull;
        public TableColumn<AccountDTO, String> newUser;

        ObservableList<AccountDTO> accounts = FXCollections.observableArrayList();
        List<AccountDTO> originalData = null;

        Predicate<AccountDTO> filter = account -> false;
        String searchKey = "";

        @SuppressWarnings("unchecked")
        NewTable(TableView<AccountDTO> table, AreaChart<String, Integer> lineChart) {
            newTable = table;
            this.lineChart = lineChart;
            newCreation = (TableColumn<AccountDTO, String>) newTable.getColumns().get(0);
            newFull = (TableColumn<AccountDTO, String>) newTable.getColumns().get(1);
            newUser = (TableColumn<AccountDTO, String>) newTable.getColumns().get(2);

            newUser.setCellValueFactory(new PropertyValueFactory<>("username"));
            newFull.setCellValueFactory(new PropertyValueFactory<>("fullName"));
            newCreation.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
            newTable.setItems(accounts);
        }

        public void updateChart(List<AccountDTO> list) {
            XYChart.Series<String, Integer> series = new XYChart.Series<>();
            series.setName("New accounts");
            LocalDateTime startDate = list.stream()
                    .map(account -> account.getCreatedAt().toLocalDate().atStartOfDay())
                    .min(LocalDateTime::compareTo)
                    .orElse(LocalDateTime.now());

            LocalDateTime endDate = LocalDateTime.now();
            Map<LocalDateTime, Long> dateCountMap = list.stream()
                    .collect(Collectors.groupingBy(account -> account.getCreatedAt().toLocalDate().atStartOfDay(),
                            Collectors.counting()))
                    .entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (e1, e2) -> e1,
                            LinkedHashMap::new));

            while (!startDate.isAfter(endDate)) {
                dateCountMap.putIfAbsent(startDate, 0L);
                startDate = startDate.plusDays(1);
            }

            for (LocalDateTime date : dateCountMap.keySet()) {
                series.getData().add(new XYChart.Data<>(date.toString(), dateCountMap.get(date).intValue()));
            }

            lineChart.getData().clear();
            lineChart.getData().add(series);
        }

        public void updateOriginal(List<AccountDTO> list) {
            originalData = new ArrayList<>(list);
            accounts.clear();
            accounts.addAll(list);
            updateChart(list);
        }

        public void updateOriginal() {
            accounts.clear();
            accounts.addAll(originalData);
            updateChart(originalData);
        }

        public void updateContent(List<AccountDTO> list) {
            accounts.clear();
            accounts.addAll(list);
            updateChart(list);
        }

        public void search(String s) {
            searchKey = s.trim().toLowerCase();
            if (s.trim().equals("")) {
                List<AccountDTO> filtered = new ArrayList<>(originalData);
                filtered.removeIf(filter);
                newTable.getItems().clear();
                newTable.getItems().addAll(filtered);
            } else {
                List<AccountDTO> filtered = new ArrayList<>(originalData);
                filtered.removeIf(account -> !account.getFullName().toLowerCase().contains(searchKey)
                        && !account.getUsername().toLowerCase().contains(searchKey));
                filtered.removeIf(filter);
                newTable.getItems().clear();
                newTable.getItems().addAll(filtered);
            }
        }

        public void setFilterPredicate(Predicate<AccountDTO> filter) {
            this.filter = filter;
            List<AccountDTO> filtered = new ArrayList<>(originalData);
            filtered.removeIf(filter);
            accounts.clear();
            accounts.addAll(filtered);
            updateChart(filtered);
            search(searchKey);
        }
    }

    @FXML
    private AreaChart<String, Integer> activeChart;
    @FXML
    private TextField activeYear;

    // Active
    class ActiveTable {
        public AreaChart<String, Integer> activeChart;

        ObservableList<UsageDTO> accounts = FXCollections.observableArrayList();
        List<UsageDTO> originalData = null;

        Predicate<UsageDTO> filter = usage -> false;
        String searchKey = "";

        ActiveTable(AreaChart<String, Integer> activeChart) {
            this.activeChart = activeChart;
        }

        public void updateChart(List<UsageDTO> list) {
            XYChart.Series<String, Integer> series = new XYChart.Series<>();
            series.setName("Active accounts");

            series.getData().addAll(list.stream().map(account -> {
                return new XYChart.Data<>(account.getMonth().toString(), account.getOpened());
            }).collect(Collectors.toList()));

            activeChart.getData().clear();
            activeChart.getData().add(series);
        }

        public void updateOriginal(List<UsageDTO> list) {
            originalData = new ArrayList<>(list);
            accounts.clear();
            accounts.addAll(list);
            updateChart(list);
        }

        public void updateOriginal() {
            accounts.clear();
            accounts.addAll(originalData);
            updateChart(originalData);
        }

        public void updateContent(List<UsageDTO> list) {
            accounts.clear();
            accounts.addAll(list);
            updateChart(list);
        }

        public void search(String s, Function<Integer, List<UsageDTO>> a) {
            searchKey = s.trim().toLowerCase();
            if (s.trim().equals("")) {
                updateOriginal(a.apply(LocalDateTime.now().getYear()));
                List<UsageDTO> filtered = new ArrayList<>(originalData);
                filtered.removeIf(filter);
            } else {
                updateOriginal(a.apply(Integer.parseInt(s)));
                List<UsageDTO> filtered = new ArrayList<>(originalData);
                filtered.removeIf(filter);
            }
        }

        // public void setFilterPredicate(Predicate<UsageDTO> filter) {
        // this.filter = filter;
        // List<UsageDTO> filtered = new ArrayList<>(originalData);
        // filtered.removeIf(filter);
        // accounts.clear();
        // accounts.addAll(filtered);
        // search(searchKey);
        // }
    }
    
    class FriendTable {
        public TableView<AccountDTO> friendTable;
        public TableColumn<AccountDTO, String> friendName;
        public TableColumn<AccountDTO, String> friendUser;
        public TableColumn<AccountDTO, String> friendCreation;
        public TableColumn<AccountDTO, Integer> friendDirect;
        public TableColumn<AccountDTO, Integer> friendIndirect;

        ObservableList<AccountDTO> accounts = FXCollections.observableArrayList();
        List<AccountDTO> originalData = null;

        Predicate<AccountDTO> filter = account -> false;
        String searchKey = "";

        @SuppressWarnings("unchecked")
        FriendTable(TableView<AccountDTO> table) {
            friendTable = table;
            friendName = (TableColumn<AccountDTO, String>) friendTable.getColumns().get(0);
            friendUser = (TableColumn<AccountDTO, String>) friendTable.getColumns().get(1);
            friendCreation = (TableColumn<AccountDTO, String>) friendTable.getColumns().get(2);
            friendDirect = (TableColumn<AccountDTO, Integer>) friendTable.getColumns().get(3);
            friendIndirect = (TableColumn<AccountDTO, Integer>) friendTable.getColumns().get(4);

            friendName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
            friendUser.setCellValueFactory(new PropertyValueFactory<>("username"));
            friendCreation.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
            friendDirect.setCellValueFactory(new PropertyValueFactory<>("directFriends"));
            friendIndirect.setCellValueFactory(new PropertyValueFactory<>("indirectFriends"));
            friendTable.setItems(accounts);
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
                friendTable.getItems().clear();
                friendTable.getItems().addAll(filtered);
            } else {
                List<AccountDTO> filtered = new ArrayList<>(originalData);
                filtered.removeIf(account -> !account.getFullName().toLowerCase().contains(searchKey)
                        && !account.getUsername().toLowerCase().contains(searchKey));
                filtered.removeIf(filter);
                friendTable.getItems().clear();
                friendTable.getItems().addAll(filtered);
            }
        }    
        
        public void setFilterPredicate(Predicate<AccountDTO> filter) {
            this.filter = filter;
            List<AccountDTO> filtered = new ArrayList<>(originalData);
            filtered.removeIf(filter);
            accounts.clear();
            accounts.addAll(filtered);
            search(searchKey);
        }
    }

    @FXML
    private ChoiceBox<String> choiceFriend;
    @FXML
    private TextField countFriend;
    @FXML
    private TextField friendFind;
    
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
    @FXML
    TextField reportSearch;
    @FXML
    DatePicker reportStart;
    @FXML
    DatePicker reportEnd;

    @FXML
    Button khoaButt;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        UserBUS userBUS = new UserBUS();
        accTable = new AccountTable(accountTable);
        grpTable = new GroupChatTable(groupTable);

        // ----------------- User data -----------------
        Thread aThread = new Thread() {
            public void run() {
                while (true) {
                    System.out.println(accTable.accountTable.getSelectionModel().getSelectedItem());
                    if (accTable.accountTable.getSelectionModel().getSelectedItem() == null
                            || accTable.originalData == null || !accTable.accountTable.isFocused()) {
                        accTable.updateOriginal(userBUS.getAllUsers());
                        System.out.println("Reload account tabel");
                    }

                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // Platform.runLater(new Runnable() {
                    // public void run() {

                    // }
                    // });
                }
            }
        };
        aThread.setDaemon(true);
        aThread.start();

        accountSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            accTable.search(newValue);
        });
        accountMenu.getItems().addAll(new MenuItem("Tất cả"), new MenuItem("Online"),
                new MenuItem("Offline"));
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
        new Thread() {
            public void run() {
                grpTable.updateOriginal(userBUS.getAllGroups());
                // Platform.runLater(new Runnable() {
                // public void run() {
                // update ProgressIndicator on FX thread
                // pi.setProgress(progress);
                // }
                // });
            }
        }.start();
        groupSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println(newValue);
            grpTable.search(newValue);
        });

        // ----------------
        friendName.setCellValueFactory(new PropertyValueFactory<AccountDTO, String>("fullName"));
        friendUser.setCellValueFactory(new PropertyValueFactory<AccountDTO, String>("username"));
        friendCreation.setCellValueFactory(new PropertyValueFactory<AccountDTO, String>("createdAt"));
        friendDirect.setCellValueFactory(new PropertyValueFactory<AccountDTO, Integer>("directFriends"));
        friendIndirect.setCellValueFactory(new PropertyValueFactory<AccountDTO, Integer>("indirectFriends"));
        friendCount.setItems(accTable.accounts);

        // loginTime.setCellValueFactory(new PropertyValueFactory<AccountDTO,
        // String>("recentLogin"));
        // loginUser.setCellValueFactory(new PropertyValueFactory<AccountDTO,
        // String>("username"));
        // loginFull.setCellValueFactory(new PropertyValueFactory<AccountDTO,
        // String>("fullName"));
        // loginTable.setItems(accTable.accounts);

        // activeFull.setCellValueFactory(new PropertyValueFactory<AccountDTO,
        // String>("fullName"));
        // activeUser.setCellValueFactory(new PropertyValueFactory<AccountDTO,
        // String>("username"));
        // activeCreation.setCellValueFactory(new PropertyValueFactory<AccountDTO,
        // String>("createdAt"));
        // Random random = new Random(1);
        // activeOpen.setCellValueFactory(cellData -> {
        // return new SimpleStringProperty(String.valueOf(random.nextInt(20) + 2));
        // });
        // activeChat.setCellValueFactory(cellData -> {
        // return new SimpleStringProperty(String.valueOf(random.nextInt(10) + 1));
        // });
        // activeGroup.setCellValueFactory(cellData -> {
        // return new SimpleStringProperty(String.valueOf(random.nextInt(5)));
        // });
        // activeTable.setItems(accTable.accounts);

        // ----------------- Report data -----------------
        ReportBUS reportBUS = new ReportBUS();
        rprtTable = new ReportInfoTable(reportTable);
        new Thread() {
            public void run() {
                rprtTable.updateOriginal(reportBUS.getAll());
                List<ReportInfoDTO> reports = reportBUS.getAll();
                System.out.println(reports);
                // update ProgressIndicator on FX thread
                // Platform.runLater(new Runnable() {
                // public void run() {
                // pi.setProgress(progress);
                // }
                // });
            }
        }.start();

        reportSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            rprtTable.search(newValue);
        });

        reportStart.valueProperty().addListener((observable, oldValue, newValue) -> {
            LocalDateTime start = reportStart.getValue().atTime(0, 0);
            LocalDateTime end = reportEnd.getValue().atTime(23, 59);
            System.out.println("Start: " + start + " End: " + end);
            if (start != null && end != null) {
                Predicate<ReportInfoDTO> filter = report -> {
                    LocalDateTime date = report.getCreatedAt();
                    return date.isBefore(start) || date.isAfter(end);
                };
                rprtTable.setFilterPredicate(filter);
            } else {
                Predicate<ReportInfoDTO> filter = report -> false;
                rprtTable.setFilterPredicate(filter);
            }
        });

        reportEnd.valueProperty().addListener((observable, oldValue, newValue) -> {
            LocalDateTime start = reportStart.getValue().atTime(0, 0);
            LocalDateTime end = reportEnd.getValue().atTime(23, 59);
            if (start != null && end != null) {
                Predicate<ReportInfoDTO> filter = report -> {
                    LocalDateTime date = report.getCreatedAt();
                    return date.isBefore(start) || date.isAfter(end);
                };
                rprtTable.setFilterPredicate(filter);
            } else {
                Predicate<ReportInfoDTO> filter = report -> false;
                rprtTable.setFilterPredicate(filter);
            }
        });

        // ----------------- Line Chart -----------------
        NewTable nwTable = new NewTable(newTable, newChart);
        new Thread() {
            public void run() {
                nwTable.updateOriginal(userBUS.getAllUsers());
                // update ProgressIndicator on FX thread
                Platform.runLater(new Runnable() {
                    public void run() {
                        nwTable.updateChart(nwTable.originalData);
                    }
                });
            }
        }.start();

        newSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            nwTable.search(newValue);
        });

        newStart.valueProperty().addListener((observable, oldValue, newValue) -> {
            LocalDateTime start = newStart.getValue().atTime(0, 0);
            LocalDateTime end = newEnd.getValue().atTime(23, 59);
            System.out.println("Start: " + start + " End: " + end);
            if (start != null && end != null) {
                Predicate<AccountDTO> filter = account -> {
                    LocalDateTime date = account.getCreatedAt();
                    return date.isBefore(start) || date.isAfter(end);
                };
                nwTable.setFilterPredicate(filter);
            } else {
                Predicate<AccountDTO> filter = account -> false;
                nwTable.setFilterPredicate(filter);
            }
        });

        newEnd.valueProperty().addListener((observable, oldValue, newValue) -> {
            LocalDateTime start = newStart.getValue().atTime(0, 0);
            LocalDateTime end = newEnd.getValue().atTime(23, 59);
            if (start != null && end != null) {
                Predicate<AccountDTO> filter = account -> {
                    LocalDateTime date = account.getCreatedAt();
                    return date.isBefore(start) || date.isAfter(end);
                };
                nwTable.setFilterPredicate(filter);
            } else {
                Predicate<AccountDTO> filter = account -> false;
                nwTable.setFilterPredicate(filter);
            }
        });

        // ------- Active Chart -------
        UsageBUS usageBUS = new UsageBUS();
        ActiveTable actTable = new ActiveTable(activeChart);
        new Thread() {
            public void run() {
                actTable.updateOriginal(usageBUS.getAppOpened(LocalDateTime.now().getYear()));
                // update ProgressIndicator on FX thread
                Platform.runLater(new Runnable() {
                    public void run() {
                        actTable.updateChart(actTable.originalData);
                    }
                });
            }
        }.start();

        activeYear.textProperty().addListener((observable, oldValue, newValue) -> {
            actTable.search(newValue, (year) -> {
                return usageBUS.getAppOpened((int) year);
            });
        });

        // ----------------- Login Table -----------------
        LoginTable lginTable = new LoginTable(loginTable);
        new Thread() {
            public void run() {
                lginTable.updateOriginal(usageBUS.getAllRecentLogin());
                // update ProgressIndicator on FX thread
                // Platform.runLater(new Runnable() {
                // public void run() {
                // pi.setProgress(progress);
                // }
                // });
            }
        }.start();

        loginSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            lginTable.search(newValue);
        });

        //
        ActivityTable ctivTable = new ActivityTable(activeTable);
        ctivTable.updateOriginal(usageBUS.getAllActivity());

        activeSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            ctivTable.search(newValue);
        });

        choiceFriend.getSelectionModel().select(0);
        choiceActiveAct.getSelectionModel().select(0);
        choiceActiveCon.getSelectionModel().select(0);
        choiceStatus.getSelectionModel().select(0);

        activeStart.valueProperty().addListener((observable, oldValue, newValue) -> {
            LocalDateTime start = activeStart.getValue().atTime(0, 0);
            LocalDateTime end = activeEnd.getValue().atTime(23, 59);
            System.out.println("Start: " + start + " End: " + end);
            if (start != null && end != null) {
                Predicate<ActivityDTO> filter = activity -> {
                    LocalDateTime date = activity.getCreationDate();
                    return date.isBefore(start) || date.isAfter(end);
                };
                ctivTable.setFilterPredicate(filter);
            } else {
                Predicate<ActivityDTO> filter = activity -> false;
                ctivTable.setFilterPredicate(filter);
            }
        });

        activeEnd.valueProperty().addListener((observable, oldValue, newValue) -> {
            LocalDateTime start = activeStart.getValue().atTime(0, 0);
            LocalDateTime end = activeEnd.getValue().atTime(23, 59);
            if (start != null && end != null) {
                Predicate<ActivityDTO> filter = activity -> {
                    LocalDateTime date = activity.getCreationDate();
                    return date.isBefore(start) || date.isAfter(end);
                };
                ctivTable.setFilterPredicate(filter);
            } else {
                Predicate<ActivityDTO> filter = activity -> false;
                ctivTable.setFilterPredicate(filter);
            }
        });

        //
        
        FriendTable frndTable = new FriendTable(friendCount);
        Thread frndThread = new Thread() {
            public void run() {
                while (true) {
                    if (friendFind.isFocused() || choiceFriend.isFocused() || countFriend.isFocused()){}
                    else if (frndTable.friendTable.getSelectionModel().getSelectedItem() == null
                            || frndTable.originalData == null || !frndTable.friendTable.isFocused()) {
                        frndTable.updateOriginal(UserBUS.getAllUsersWithFriendCount());
                        System.out.println("Reload friend tabel");
                    }

                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        frndThread.setDaemon(true);
        frndThread.start();
        
        // friendCount.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
        //     if (newSelection != null) {
        //         System.out.println("Selected: " + newSelection.getFullName());
        //     }
        // });
        
        friendFind.textProperty().addListener((observable, oldValue, newValue) -> {
            frndTable.search(newValue);
        });
        
        choiceFriend.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                System.out.println("Selected: " + newSelection);
                Predicate<AccountDTO> filter = account -> {
                    int friendCount = account.getDirectFriends();
                    if (newSelection.equals("lớn hơn")) {
                        return countFriend.getText().trim().equals("") ? false : friendCount <= Integer.parseInt(countFriend.getText().trim());
                    } else if (newSelection.equals("nhỏ hơn")) {
                        return countFriend.getText().trim().equals("") ? false : friendCount >= Integer.parseInt(countFriend.getText().trim());
                    } else if (newSelection.equals("bằng")) {
                        return countFriend.getText().trim().equals("") ? false : (friendCount - Integer.parseInt(countFriend.getText().trim())) != 0;
                    }
                    return false;
                };
                frndTable.setFilterPredicate(filter);
            }
        });
        
        countFriend.textProperty().addListener((observable, oldValue, newValue) -> {
            if (choiceFriend.getSelectionModel().getSelectedItem() != null) {
                Predicate<AccountDTO> filter = account -> {
                    int friendCount = account.getDirectFriends();
                    if (choiceFriend.getSelectionModel().getSelectedItem().equals("lớn hơn")) {
                        return newValue.trim().equals("") ? false : friendCount <= Integer.parseInt(newValue.trim());
                    } else if (choiceFriend.getSelectionModel().getSelectedItem().equals("nhỏ hơn")) {
                        return newValue.trim().equals("") ? false : friendCount >= Integer.parseInt(newValue.trim());
                    } else if (choiceFriend.getSelectionModel().getSelectedItem().equals("bằng")) {
                        return newValue.trim().equals("") ? false : (friendCount - Integer.parseInt(newValue.trim())) != 0;
                    }
                    return false;
                };
                frndTable.setFilterPredicate(filter);
            }
        });
        

        choiceFriend.getItems().addAll("lớn hơn", "nhỏ hơn", "bằng");
        choiceActiveAct.getItems().addAll("Mở ứng dụng", "Chat cá nhân", "Chat nhóm");
        choiceActiveCon.getItems().addAll("lớn hơn", "nhỏ hơn", "bằng");
        choiceStatus.getItems().addAll("Pending", "Resolved", "Under Review");

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
        if (account.getAdminAction() == null) {
            System.out.println("Khóa người dùng");
            Platform.runLater(new Runnable() {
                public void run() {
                    khoaButt.setText("Khóa người dùng");
                }
            });
        } else if (account.getAdminAction().equals("locked")) {
            Platform.runLater(new Runnable() {
                public void run() {
                    khoaButt.setText("Mở khóa");
                }
            });
            System.out.println("Mở khóa");
        }
    }

    public void lockProfile(ActionEvent event) {
        AccountDTO account = accTable.accountTable.getSelectionModel().getSelectedItem();
        if (account == null)
            return;

        if (khoaButt.getText().equals("Khóa người dùng")) {
            UserBUS.lockAccount(account.getUserId());
            System.out.println("Lock user");
            System.out.println(account.getUsername() + " is now locked");
            khoaButt.setText("Mở khóa");
        } else {
            UserBUS.unlockAccount(account.getUserId());
            System.out.println("Unlock user");
            khoaButt.setText("Khóa người dùng");
        }
    }

    public void removeProfile(ActionEvent event) {
        AccountDTO account = accTable.accountTable.getSelectionModel().getSelectedItem();
        if (account == null)
            return;

        UserBUS.deleteAccount(account.getUserId());
        System.out.println("Removed a user");
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
        List<HBox> memberBoxList = group.getMembers().stream().map(mem -> {
            ImageView profile = new ImageView(new Image(
                    "https://pub-b0a9bdcea1cd4f6ca28d98f878366466.r2.dev/1.png",
                    40, 40, true, true, true));
            Circle clip = new Circle(20, 20, 20);
            profile.setClip(clip);
            profile.setFitWidth(40);
            profile.setFitHeight(40);
            Text username = new Text(mem);
            username.setFont(new Font(14));
            username.setTextAlignment(TextAlignment.CENTER);
            HBox memberBox = new HBox(profile, username);
            memberBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            return memberBox;
        }).toList();
        System.out.println("Member size: " + memberBoxList.size());
        VMemList.getChildren().addAll(memberBoxList);
    }

    @FXML
    Text reportIdText;
    @FXML
    ImageView reporterProfile;
    @FXML
    Text reporter;
    @FXML
    ImageView reportedProfile;
    @FXML
    Text reported;

    public void reportClicked() {
        System.out.println("Report clicked");
        ReportInfoDTO report = reportTable.getSelectionModel().getSelectedItem();
        reportIdText.setText("Report ID: " + report.getReportId());
        List<Integer> peopleIds = new ArrayList<>();
        rprtTable.originalData.forEach(r -> {
            if (r.getReportId() == report.getReportId()) {
                reporter.setText(r.getReporter());
                reported.setText(r.getReported());
                peopleIds.add(r.getReporterId());
                peopleIds.add(r.getReportedId());
            }
        });
        Thread a = new Thread(() -> {
            List<AccountDTO> people = new UserBUS().getByIdList(peopleIds);

            people.forEach(p -> {
                if (p.getUsername().equals(reporter.getText())) {
                    reporterProfile.setImage(new Image(p.getProfileUrl()));
                    // reporter.setText(p.getUsername());
                } else {
                    reportedProfile.setImage(new Image(p.getProfileUrl()));
                    // reported.setText(p.getUsername());
                }
            });
            System.out.println("Update report people done");
        });
        a.start();
    }

    public void switchToMain(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("views/main.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void switchToProfile(ActionEvent event) throws IOException {
        AccountDTO account = accTable.accountTable.getSelectionModel().getSelectedItem();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("views/profile.fxml"));
        ProfileController profileController = loader.getController();
        profileController.setAccount(account);
        root = loader.load();

        System.out.println("Sent account to profile: " + account.getUsername());

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void switchToNewProfile(ActionEvent event) throws IOException {
        AccountDTO account = accTable.accountTable.getSelectionModel().getSelectedItem();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("views/profile.fxml"));
        ProfileController profileController = loader.getController();
        profileController.setAccount(null);
        root = loader.load();

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
