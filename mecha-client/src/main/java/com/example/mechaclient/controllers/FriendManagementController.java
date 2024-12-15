package com.example.mechaclient.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.image.*;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;

import com.example.mechaclient.ChatApplication;
import com.example.mechaclient.models.ChatBox;
import com.example.mechaclient.models.ChatBox.ChatType;
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

    public void initialize() {
        UserSession.getInstance().addMessageListener(this);
        // showFriendRequests();   
        setupFriendRequests();
        setupSearchResults();
        setupBlockedList();
        setupReportedList();
        
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
            Label usernameLabel = (Label) item.lookup(".fullname-label"); 
            if (usernameLabel != null) {
                String username = usernameLabel.getText();
                System.out.println("username: " + username);
                return username.toLowerCase().contains(query.toLowerCase());
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

    private void setupFriendRequests() {
        // ObservableList<HBox> items = FXCollections.observableArrayList();
        // items.add(createFriendRequestItem("Username1", "40 minutes ago"));
        // items.add(createFriendRequestItem("Username2", "40 minutes ago"));
        // friendRequestList.setItems(items);
    }

    private void setupSearchResults() {
        // ObservableList<HBox> items = FXCollections.observableArrayList();
        // items.add(createSearchResultItem("Username1"));
        // searchResultList.setItems(items);
    }

    private void setupBlockedList() {
        // ObservableList<HBox> items = FXCollections.observableArrayList();
        // items.add(createBlockedUserItem("Username1"));
        // blockedList.setItems(items);
    }
    
    private void setupReportedList() {
        // ObservableList<HBox> items = FXCollections.observableArrayList();
        // items.add(createReportedUserItem("Username1", "Pending"));
        // items.add(createReportedUserItem("Username2", "Resolved"));
        // reportedList.setItems(items);
    }

    private HBox createFriendRequestItem(int userId, String fullname) {
        HBox item = new HBox(10);
        item.setPadding(new Insets(8, 15, 8, 15));
        item.setAlignment(Pos.CENTER_LEFT);
        item.getStyleClass().add("box-item");
        
        
        VBox userInfo = new VBox(5);
        Label fullnameLabel = new Label(fullname != null ? fullname : "Unknown");
        fullnameLabel.setStyle("-fx-text-fill: black;");
        fullnameLabel.getStyleClass().add("fullname-label");
        Label timeLabel = new Label("username placeholder");
        timeLabel.getStyleClass().add("time-label");
        userInfo.getChildren().addAll(fullnameLabel, timeLabel);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button acceptButton = new Button("Accept");
        acceptButton.getStyleClass().add("accept-button");
        acceptButton.setOnAction(e -> {
            try {
                UserSession.out.writeObject("ACCEPT_FRIEND_REQUEST");
                UserSession.out.writeObject(UserSession.getInstance().getUserId()); // receiver
                UserSession.out.writeObject(userId); // sender
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
                UserSession.out.writeObject(UserSession.getInstance().getUserId()); // receiver
                UserSession.out.writeObject(userId); // sender
                showFriendRequests();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        item.getChildren().addAll(userInfo, spacer, acceptButton, declineButton);
        return item;
    }

    private HBox createSearchResultItem(int userId, String fullname) {
        HBox item = new HBox(5);
        item.setPadding(new Insets(8, 15, 8, 15));
        item.setAlignment(Pos.CENTER_LEFT);
        item.getStyleClass().add("box-item");
        
        Label fullnameLabel = new Label(fullname != null ? fullname : "Unknown");
        fullnameLabel.setStyle("-fx-text-fill: black;");
        fullnameLabel.getStyleClass().add("fullname-label");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
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
        item.getChildren().addAll(fullnameLabel, spacer, addButton);
        return item;
    }

    private HBox createUserFriendRequestItem(int userId, String fullname) {
        HBox item = new HBox(10);
        item.setPadding(new Insets(8, 15, 8, 15));
        item.setAlignment(Pos.CENTER_LEFT);
        item.getStyleClass().add("box-item");
    
        
        Label fullnameLabel = new Label(fullname != null ? fullname : "Unknown");
        fullnameLabel.setStyle("-fx-text-fill: black;");
        fullnameLabel.getStyleClass().add("fullname-label");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
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
            event.getTarget();
        });
        item.getChildren().addAll(fullnameLabel, spacer, cancelButton);
        return item;
    }

    private HBox createBlockedUserItem(int userId, String fullname) {
        System.out.println("createBlocked user called");
        HBox item = new HBox(10);
        item.setPadding(new Insets(8, 15, 8, 15));
        item.setAlignment(Pos.CENTER_LEFT);
        item.getStyleClass().add("box-item");
        
        Label fullnameLabel = new Label(fullname);
        fullnameLabel.setStyle("-fx-text-fill: black;");
        fullnameLabel.getStyleClass().add("fullname-label");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
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
            } catch (IOException ex){
                ex.printStackTrace();
            }
        });
        item.getChildren().addAll(fullnameLabel, spacer, removeButton);
        return item;
    }

    private HBox createReportedUserItem(int userId, String fullname, String status, Timestamp timeReported) {
        HBox item = new HBox(10);
        item.setPadding(new Insets(8, 15, 8, 15));
        item.setAlignment(Pos.CENTER_LEFT);
        item.getStyleClass().add("box-item");
        
        Label fullnameLabel = new Label(fullname);
        fullnameLabel.setStyle("-fx-text-fill: black;");
        fullnameLabel.getStyleClass().add("fullname-label");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label statusLabel = new Label("Status: ");

        Label statusValue = new Label(status);
        statusValue.getStyleClass().add(status.toLowerCase() + "-status");
        statusLabel.setStyle("-fx-text-fill: black;");
        statusValue.setStyle("-fx-text-fill: black;");
        item.getChildren().addAll(fullnameLabel, spacer, statusLabel, statusValue);
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
                    items.add(createSearchResultItem(userId, fullname));  
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
                    String friendFullname = friend[1];
                    items.add(createUserFriendRequestItem(friendId, friendFullname));   
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
                    items.add(createFriendRequestItem(friendId, friendFullname));   
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
                    items.add(createBlockedUserItem(userId, fullname));   
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
                    String status = reportedUser[2];
                    String timeReportedString = reportedUser[3];

                    Timestamp timeReported = Timestamp.valueOf(timeReportedString);
                    items.add(createReportedUserItem(userId, fullname, status, timeReported));   
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