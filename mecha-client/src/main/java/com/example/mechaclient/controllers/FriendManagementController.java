package com.example.mechaclient.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;

import com.example.mechaclient.ChatApplication;
import com.example.mechaclient.models.UserSession.ServerMessageListener;
import com.example.mechaclient.models.UserSession;

public class FriendManagementController implements ServerMessageListener {

    @FXML private VBox friendRequestContent;
    @FXML private VBox findFriendContent;
    @FXML private VBox blockedListContent;
    @FXML private VBox reportedListContent;
    private VBox currentContent;

    @FXML private ListView<HBox> friendRequestList;
    @FXML private ListView<HBox> searchResultList;
    @FXML private ListView<HBox> blockedList;
    @FXML private ListView<HBox> reportedList;
    @FXML private ListView<HBox> requestsSentList;
    private ListView<HBox> currentActiveList;

    private ObservableList<HBox> fullList = FXCollections.observableArrayList();

    @FXML private Button friendRequestTab;
    @FXML private Button findFriendTab;
    @FXML private Button blockedListTab;
    @FXML private Button reportedListTab;
    @FXML private Button findFriendSubTab;
    @FXML private Button requestsSentSubTab;
    private Button currentActiveTab;

    @FXML private TextField universalSearchField;
    @FXML private ChoiceBox<String> searchTypeChoiceBox;

    public void initialize() {
        UserSession.getInstance().addMessageListener(this);
        
        searchTypeChoiceBox.setItems(FXCollections.observableArrayList("Search by full name", "Search by @username"));
        searchTypeChoiceBox.setValue("Search by full name");

        setupListViewBehavior(friendRequestList);
        setupListViewBehavior(searchResultList);
        setupListViewBehavior(blockedList);
        setupListViewBehavior(reportedList);
        setupListViewBehavior(requestsSentList);
        Platform.runLater(() -> {
            universalSearchField.textProperty().addListener((observable, oldValue, newValue) -> filterList(currentActiveTab, currentContent, currentActiveList, newValue));
        });
        
    }

    private void filterList(Button currentActiveTab, VBox currentContent, ListView<HBox> listView, String query) {
        System.out.println("filter called, list: ");
        if (currentContent == findFriendContent)
            System.out.println("content list");
        if (currentContent == friendRequestContent)
            System.out.println("friend_request list");
        System.out.println("list size: " + fullList.size());
        ObservableList<HBox> allItems = fullList;
        ObservableList<HBox> filteredItems = allItems.filtered(item -> {
            if (item == null || query.isEmpty()) return true;
            String searchType = searchTypeChoiceBox.getValue();
            if ("Search by full name".equals(searchType)) {
                Label fullnameLabel = (Label) item.lookup(".fullname-label");
                if (fullnameLabel != null) {
                    String fullname = fullnameLabel.getText();
                    System.out.println("fullname: " + fullname);
                    return fullname.toLowerCase().contains(query.toLowerCase());
                }
            } else if ("Search by @username".equals(searchType)) {
                Label usernameLabel = (Label) item.lookup(".username-label");
                if (usernameLabel != null) {
                    String username = usernameLabel.getText();
                    System.out.println("username: " + username);
                    return username.toLowerCase().contains(query.toLowerCase());
                }
            }
            return false;
        });
        listView.setItems(filteredItems);
        setActiveTab(currentContent);
        currentActiveTab.setStyle("-fx-background-color: #cccccc;");
    }

    private void setupListViewBehavior(ListView<HBox> listView) {
        listView.setCellFactory(lv -> new ListCell<HBox>() {
            @Override
            protected void updateItem(HBox item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(item);
                }
            }
        });
        
        // listView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
        //     if (oldSelection != null) {
        //         oldSelection.setStyle("-fx-background-color: transparent;");
        //     }
        //     if (newSelection != null) {
        //         newSelection.setStyle("-fx-background-color:rgb(55, 204, 250);");
        //     }
        // });
    }

    @FXML
    private void showFindFriendResults() {
        universalSearchField.clear();
        try {
            UserSession.out.writeObject("GET_POTENTIAL_FRIENDS");
            UserSession.out.writeObject(UserSession.getInstance().getUserId());
        } catch (IOException e) {
            e.printStackTrace();
        }

        setActiveSubTab(searchResultList, findFriendSubTab);

        currentActiveList = searchResultList;
        currentContent = findFriendContent;
        currentActiveTab = findFriendTab;
    }

    @FXML
    private void showRequestsSent() {
        universalSearchField.clear();
        setActiveSubTab(requestsSentList, requestsSentSubTab);
        loadUserFriendRequestsSent();

        currentActiveList = requestsSentList;
        currentActiveTab = findFriendTab;
    }

    private void setActiveSubTab(ListView<HBox> listView, Button activeTab) {
        searchResultList.setVisible(false);
        requestsSentList.setVisible(false);

        findFriendSubTab.pseudoClassStateChanged(PseudoClass.getPseudoClass("selected"), false);
        requestsSentSubTab.pseudoClassStateChanged(PseudoClass.getPseudoClass("selected"), false);

        listView.setVisible(true);
        activeTab.pseudoClassStateChanged(PseudoClass.getPseudoClass("selected"), true);
    }

    private void loadUserFriendRequestsSent() {
        try {
            UserSession.out.writeObject("GET_USER_FRIEND_REQUEST");
            UserSession.out.writeObject(UserSession.getInstance().getUserId());
        } catch (Exception e){
            e.printStackTrace();
        }
        
    }

    @FXML
    private void returnToHomeScreen(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ChatApplication.class.getResource("views/HomeScreen.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        stage = (Stage) friendRequestContent.getScene().getWindow(); // get scene based on an element that in the scene, in this case it is friendRequestContent
        stage.setScene(scene);
        stage.setOnCloseRequest(e -> {
            UserSession.getInstance().Logout();
        });
        stage.show();
        UserSession.getInstance().removeMessageListener(this);
    }

    @FXML   
    private void showFriendRequests() {
        try {
            UserSession.out.writeObject("GET_FRIEND_REQUEST");
            UserSession.out.writeObject(UserSession.getInstance().getUserId());
        } catch (IOException e) {
            e.printStackTrace();
        }
        setActiveTab(friendRequestContent);
        friendRequestTab.setStyle("-fx-background-color: #cccccc;");
        currentActiveList = friendRequestList;
        currentContent = friendRequestContent;
        currentActiveTab = friendRequestTab;
    }

    @FXML
    private void showFindFriend() {
        setActiveTab(findFriendContent);
        findFriendTab.setStyle("-fx-background-color: #cccccc;");
        showFindFriendResults();
    }

    @FXML
    private void showBlockedList() {
        try {
            UserSession.out.writeObject("GET_BLOCKED_LIST");
            UserSession.out.writeObject(UserSession.getInstance().getUserId());
        } catch (IOException e) {
            e.printStackTrace();
        }
        setActiveTab(blockedListContent);
        blockedListTab.setStyle("-fx-background-color: #cccccc;");
        currentActiveList = blockedList;
        currentContent = blockedListContent;
        currentActiveTab = blockedListTab;
    }

    @FXML
    private void showReportedList() {
        try {
            UserSession.out.writeObject("GET_REPORT_LIST");
            UserSession.out.writeObject(UserSession.getInstance().getUserId());
        } catch (IOException e){
            e.printStackTrace();
        }
        setActiveTab(reportedListContent);
        reportedListTab.setStyle("-fx-background-color: #cccccc;");
        currentActiveList = reportedList;
        currentContent = reportedListContent;
        currentActiveTab = reportedListTab;
    }

    private void setActiveTab(VBox content) {
        if (content != currentContent){
            universalSearchField.clear();
        }
        friendRequestContent.setVisible(false);
        findFriendContent.setVisible(false);
        blockedListContent.setVisible(false);
        reportedListContent.setVisible(false);
        
        friendRequestTab.setStyle("");
        findFriendTab.setStyle("");
        blockedListTab.setStyle("");
        reportedListTab.setStyle("");
        
        content.setVisible(true);
    }

    private HBox createFriendRequestItem(int userId, String fullname, String username, String address, String gender, String dob, String timeSent) {
        HBox item = new HBox(10);
        item.setPadding(new Insets(8, 15, 8, 15));
        item.setAlignment(Pos.CENTER_LEFT);
        item.getStyleClass().add("box-item");

        // Left Info Section
        VBox userInfo = new VBox(5);

        // Fullname and Username (First Line)
        HBox nameLine = new HBox(5);
        Label fullnameLabel = new Label(fullname != null ? fullname : "Unknown");
        fullnameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: black;");
        fullnameLabel.getStyleClass().add("fullname-label");

        Label usernameLabel = new Label(username != null ? "@" + username : "@unknown");
        usernameLabel.setStyle("-fx-text-fill: #1f1f1f; -fx-font-size: 14px;");
        usernameLabel.getStyleClass().add("username-label");
        nameLine.getChildren().addAll(fullnameLabel, usernameLabel);

        // Gender (Second Line)
        Label genderLabel = new Label(gender != null ? gender : "Unknown");
        genderLabel.setStyle("-fx-text-fill: black; -fx-font-size: 12px;");

        // Date of Birth and Address (Third Line)
        Label dobAndAddressLabel = new Label((dob != null ? dob : "Unknown DOB") + ", " + (address != null ? address : "Unknown Address"));
        dobAndAddressLabel.setStyle("-fx-text-fill: black; -fx-font-size: 12px;");

        userInfo.getChildren().addAll(nameLine, genderLabel, dobAndAddressLabel);

        // Spacer
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Time Sent
        String formattedTime = formatTime(timeSent); // format time to: HH:MM, DD/MM/YYYY
        Label timeLabel = new Label("Sent At: " + formattedTime);
        timeLabel.setStyle("-fx-text-fill: grey; -fx-font-size: 12px;");

        // Action Buttons
        Button acceptButton = new Button("Accept");
        acceptButton.getStyleClass().add("accept-button");
        acceptButton.setOnAction(e -> {
            try {
                UserSession.out.writeObject("ACCEPT_FRIEND_REQUEST");
                UserSession.out.writeObject(UserSession.getInstance().getUserId());
                UserSession.out.writeObject(userId);
                System.out.println("accepting friend");
                showFriendRequests();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        Button declineButton = new Button("Decline");
        declineButton.getStyleClass().add("decline-button");
        declineButton.setOnAction(e -> {
            try {
                UserSession.out.writeObject("DECLINE_FRIEND_REQUEST");
                UserSession.out.writeObject(UserSession.getInstance().getUserId());
                UserSession.out.writeObject(userId);
                showFriendRequests();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        item.getChildren().addAll(userInfo, spacer, timeLabel, acceptButton, declineButton);
        return item;
    }

    // Helper Method to Format Time
    private String formatTime(String timeSent) {
        try {
            LocalDateTime timestamp = LocalDateTime.parse(timeSent, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            return timestamp.format(DateTimeFormatter.ofPattern("HH:mm, dd/MM/yyyy"));
        } catch (Exception e) {
            e.printStackTrace();
            return "Invalid Time";
        }
    }


    private HBox createSearchResultItem(int userId, String fullname, String username, String address, String gender, String dob) { 
        HBox item = new HBox(5);
        item.setPadding(new Insets(8, 15, 8, 15));
        item.setAlignment(Pos.CENTER_LEFT);
        item.getStyleClass().add("box-item");
    
        // Left Info Section
        VBox infoBox = new VBox(3);
        infoBox.setAlignment(Pos.CENTER_LEFT);
    
        // Full name and username
        HBox nameLine = new HBox(5); // Spacing between fullname and username
        Label fullnameLabel = new Label(fullname != null ? fullname : "Unknown");
        fullnameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: black;");
        fullnameLabel.getStyleClass().add("fullname-label");

        Label usernameLabel = new Label(username != null ? "@" + username : "@unknown");
        usernameLabel.setStyle("-fx-text-fill: #1f1f1f; -fx-font-size: 14px;");
        usernameLabel.getStyleClass().add("username-label");
        nameLine.getChildren().addAll(fullnameLabel, usernameLabel);
    
        // Gender (second line)
        Label genderLabel = new Label(gender != null ? gender : "Unknown");
        genderLabel.setStyle("-fx-text-fill: black; -fx-font-size: 12px;");
    
        // Date of Birth and Address (third line)
        Label dobAndAddressLabel = new Label((dob != null ? dob : "Unknown DOB") + ", " + (address != null ? address : "Unknown Address"));
        dobAndAddressLabel.setStyle("-fx-text-fill: black; -fx-font-size: 12px;");
    
        infoBox.getChildren().addAll(nameLine, genderLabel, dobAndAddressLabel);
    
        // Spacer between info and button
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
    
        // Add Button
        Button addButton = new Button("Add friend");
        addButton.getStyleClass().add("add-button");
        addButton.setOnAction(event -> {
            try {
                UserSession.out.writeObject("ADD_FRIEND_REQUEST");
                UserSession.out.writeObject(UserSession.getInstance().getUserId());
                UserSession.out.writeObject(userId);
                System.out.println("sending friend request to server");
                Platform.runLater(() -> {
                    showFindFriend();
                });
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            event.getTarget();
        });
    
        item.getChildren().addAll(infoBox, spacer, addButton);
        return item;
    }

    private HBox createUserFriendRequestItem(int userId, String fullname, String username, String address, String gender, String dob, String timeSent) {
        HBox item = new HBox(10);
        item.setPadding(new Insets(8, 15, 8, 15));
        item.setAlignment(Pos.CENTER_LEFT);
        item.getStyleClass().add("box-item");
    
        VBox userInfo = new VBox(5);
        
        // Fullname and username
        HBox nameRow = new HBox(5);
        Label fullnameLabel = new Label(fullname != null ? fullname : "Unknown");
        fullnameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: black;");
        fullnameLabel.getStyleClass().add("fullname-label");

        Label usernameLabel = new Label("@" + username);
        usernameLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666666;");
        usernameLabel.getStyleClass().add("username-label");
        nameRow.getChildren().addAll(fullnameLabel, usernameLabel);
        
        // Gender
        Label genderLabel = new Label("Gender: " + (gender != null ? gender : "N/A"));
        genderLabel.setStyle("-fx-text-fill: black; -fx-font-size: 12px;");
        
        // Date of birth and address
        Label dobAddressLabel = new Label((dob != null ? dob : "N/A") + ", " + (address != null ? address : "N/A"));
        dobAddressLabel.setStyle("-fx-text-fill: black; -fx-font-size: 12px;");
    
        userInfo.getChildren().addAll(nameRow, genderLabel, dobAddressLabel);
    
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Time sent
        String formattedTime = formatTime(timeSent);
        Label timeLabel = new Label("Request sent: " + formattedTime);
        timeLabel.setStyle("-fx-text-fill: #888888; -fx-font-size: 10px;");
    
        Button cancelButton = new Button("Cancel request");
        cancelButton.getStyleClass().add("cancel-button");
        cancelButton.setOnAction(event -> {
            try {
                UserSession.out.writeObject("CANCEL_FRIEND_REQUEST");
                UserSession.out.writeObject(UserSession.getInstance().getUserId());
                UserSession.out.writeObject(userId);
                System.out.println("remove user's friend request");
                Platform.runLater(() -> {
                    showFindFriend();
                    showRequestsSent();
                });
    
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    
        item.getChildren().addAll(userInfo, spacer, timeLabel, cancelButton);
        return item;
    }

    private HBox createBlockedUserItem(int userId, String fullname, String username, String address, String gender, String dob, String timeSent) {
        System.out.println("createBlocked user called");
        HBox item = new HBox(10);
        item.setPadding(new Insets(8, 15, 8, 15));
        item.setAlignment(Pos.CENTER_LEFT);
        item.getStyleClass().add("box-item");
    
        // User Info Section
        VBox userInfo = new VBox(5);
    
        // Fullname and Username (First Line)
        HBox nameLine = new HBox(5);
        Label fullnameLabel = new Label(fullname != null ? fullname : "Unknown");
        fullnameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: black;");
        fullnameLabel.getStyleClass().add("fullname-label");

        Label usernameLabel = new Label(username != null ? "@" + username : "@unknown");
        usernameLabel.setStyle("-fx-text-fill: #1f1f1f; -fx-font-size: 14px;");
        usernameLabel.getStyleClass().add("username-label");
        nameLine.getChildren().addAll(fullnameLabel, usernameLabel);
    
        // Gender (Second Line)
        Label genderLabel = new Label(gender != null ? gender : "Unknown");
        genderLabel.setStyle("-fx-text-fill: black; -fx-font-size: 12px;");
    
        // Date of Birth and Address (Third Line)
        Label dobAndAddressLabel = new Label((dob != null ? dob : "Unknown DOB") + ", " + (address != null ? address : "Unknown Address"));
        dobAndAddressLabel.setStyle("-fx-text-fill: black; -fx-font-size: 12px;");
    
        // Time Sent
        String formattedTime = formatTime(timeSent);
        Label timeLabel = new Label("Blocked at: " + formattedTime);
        timeLabel.setStyle("-fx-text-fill: grey; -fx-font-size: 12px;");
    
        userInfo.getChildren().addAll(nameLine, genderLabel, dobAndAddressLabel);
    
        // Spacer
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
    
        // Remove Block Button
        Button removeButton = new Button("Remove block");
        removeButton.getStyleClass().add("remove-block-button");
        removeButton.setOnAction(e -> {
            try {
                UserSession.out.writeObject("REMOVE_BLOCK");
                UserSession.out.writeObject(UserSession.getInstance().getUserId());
                UserSession.out.writeObject(userId);
                Platform.runLater(() -> {
                    showBlockedList();
                });
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    
        item.getChildren().addAll(userInfo, spacer, timeLabel, removeButton);
        return item;
    }

    private HBox createReportedUserItem(int userId, String fullname, String status, String username, String address, String gender, String dob, String timeSent) {
        HBox item = new HBox(10);
        item.setPadding(new Insets(8, 15, 8, 15));
        item.setAlignment(Pos.CENTER_LEFT);
        item.getStyleClass().add("box-item");
    
        // User Info Section
        VBox userInfo = new VBox(5);
    
        // Fullname and Username (First Line)
        HBox nameLine = new HBox(5);
        Label fullnameLabel = new Label(fullname != null ? fullname : "Unknown");
        fullnameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: black;");
        fullnameLabel.getStyleClass().add("fullname-label");
        
        Label usernameLabel = new Label(username != null ? "@" + username : "@unknown");
        usernameLabel.setStyle("-fx-text-fill: #1f1f1f; -fx-font-size: 14px;");
        usernameLabel.getStyleClass().add("username-label");
        nameLine.getChildren().addAll(fullnameLabel, usernameLabel);
    
        // Gender (Second Line)
        Label genderLabel = new Label(gender != null ? gender : "Unknown");
        genderLabel.setStyle("-fx-text-fill: black; -fx-font-size: 12px;");
    
        // Date of Birth and Address (Third Line)
        Label dobAndAddressLabel = new Label((dob != null ? dob : "Unknown DOB") + ", " + (address != null ? address : "Unknown Address"));
        dobAndAddressLabel.setStyle("-fx-text-fill: black; -fx-font-size: 12px;");
    
        userInfo.getChildren().addAll(nameLine, genderLabel, dobAndAddressLabel);
    
        // Spacer
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Time Sent
        String formattedTime = formatTime(timeSent);
        Label timeLabel = new Label("Reported At: " + formattedTime);
        timeLabel.setStyle("-fx-text-fill: grey; -fx-font-size: 12px;");
    
    
        // Status Section
        Label statusLabel = new Label("Status: ");
        statusLabel.setStyle("-fx-text-fill: black; -fx-font-size: 12px;");
        Label statusValue = new Label(status);
        statusValue.getStyleClass().add(status.toLowerCase() + "-status");
        statusValue.setStyle("-fx-font-weight: bold; -fx-text-fill: black; -fx-font-size: 12px;");
   
        item.getChildren().addAll(userInfo, spacer, timeLabel, statusLabel, statusValue);
        return item;
    }

    @Override
    public void onMessageReceived(String serverMessage) {
        try {
            if ("respond_GET_POTENTIAL_FRIENDS".equals(serverMessage)) {
                List<String[]> potentialFriends = (List<String[]>) UserSession.in.readObject();
                ObservableList<HBox> items = FXCollections.observableArrayList();
                for (String[] friend : potentialFriends) {
                    int userId = Integer.parseInt(friend[0]);
                    String fullname = friend[1];
                    String username = friend[2];
                    String address = friend[3];
                    String gender = friend[4];
                    String dob = friend[5];
                    items.add(createSearchResultItem(userId, fullname, username, address, gender, dob));  
                }
                Platform.runLater(() -> {
                    fullList = items;
                    searchResultList.setItems(items);
                });
            } else if ("respond_GET_USER_FRIEND_REQUEST".equals(serverMessage)){
                List<String[]> friendRequests = (List<String[]>) UserSession.in.readObject();
                ObservableList<HBox> items = FXCollections.observableArrayList();
                System.out.println("got friend request");
                for (String[] friend : friendRequests) {
                    int friendId = Integer.parseInt(friend[0]);
                    String fullname = friend[1];
                    String username = friend[2];
                    String address = friend[3];
                    String gender = friend[4];
                    String dob = friend[5];
                    String timeSent = friend[6];
                    items.add(createUserFriendRequestItem(friendId, fullname, username, address, gender, dob, timeSent));   
                }
                Platform.runLater(() -> {
                    fullList = items;
                    requestsSentList.setItems(items);
                });
            } else if ("respond_GET_FRIEND_REQUEST".equals(serverMessage)){
                List<String[]> friendRequestToUser = (List<String[]>) UserSession.in.readObject();
                ObservableList<HBox> items = FXCollections.observableArrayList();
                for (String[] friend : friendRequestToUser) {
                    int friendId = Integer.parseInt(friend[0]);
                    String friendFullname = friend[1];
                    String friendUsername = friend[2];
                    String address = friend[3];
                    String gender = friend[4];
                    String dob = friend[5];
                    String timeSent = friend[6];
                    items.add(createFriendRequestItem(friendId, friendFullname, friendUsername, address, gender, dob, timeSent));   
                }
                Platform.runLater(() -> {
                    fullList = items;
                    friendRequestList.setItems(items);
                });
            } else if ("respond_GET_BLOCKED_LIST".equals(serverMessage)){
                List<String[]> blockedUserList = (List<String[]>) UserSession.in.readObject();
                ObservableList<HBox> items = FXCollections.observableArrayList();
                for (String[] blockedUser : blockedUserList) {
                    int userId = Integer.parseInt(blockedUser[0]);
                    String fullname = blockedUser[1];
                    String username = blockedUser[2];
                    String address = blockedUser[3];
                    String gender = blockedUser[4];
                    String dob = blockedUser[5];
                    String timeSent = blockedUser[6]; 
                    items.add(createBlockedUserItem(userId, fullname, username, address, gender, dob, timeSent));   
                }
                Platform.runLater(() -> {
                    fullList = items;
                    blockedList.setItems(items);
                });
            } else if ("respond_GET_REPORT_LIST".equals(serverMessage)){
                List<String[]> reportedUserList = (List<String[]>) UserSession.in.readObject();
                ObservableList<HBox> items = FXCollections.observableArrayList();
                // Timestamp timestamp = Timestamp.valueOf(timeSent);
                for (String[] reportedUser : reportedUserList) {
                    // order: reportedUserId, reportedUserFullname, reportedUserStatus, reportedTime
                    int userId = Integer.parseInt(reportedUser[0]);
                    String fullname = reportedUser[1];
                    String username = reportedUser[2];
                    String address = reportedUser[3];
                    String gender = reportedUser[4];
                    String dob = reportedUser[5];
                    String timeSent = reportedUser[6]; 
                    String status = reportedUser[7];
                    
                    items.add(createReportedUserItem(userId, fullname, status, username, address, gender, dob, timeSent));   
                    Platform.runLater(() -> {
                        fullList = items;
                        reportedList.setItems(items);
                    });
                }
            }
        } catch (Exception e){
            System.out.println("error handling response from friend management controller");
            e.printStackTrace();
        } 
    }
}