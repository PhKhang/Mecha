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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;

import com.example.mechaclient.ChatApplication;

public class FriendManagementController {

    @FXML private VBox friendRequestContent;
    @FXML private VBox findFriendContent;
    @FXML private VBox blockedListContent;
    @FXML private VBox reportedListContent;
    
    @FXML private ListView<HBox> friendRequestList;
    @FXML private ListView<HBox> searchResultList;
    @FXML private ListView<HBox> blockedList;
    @FXML private ListView<HBox> reportedList;
    
    @FXML private Button friendRequestTab;
    @FXML private Button findFriendTab;
    @FXML private Button blockedListTab;
    @FXML private Button reportedListTab;

    private final Image defaultAvatar = new Image(ChatApplication.class.getResourceAsStream("images/default-ava.png"));

    public void initialize() {
        showFriendRequests();
        setupFriendRequests();
        setupSearchResults();
        setupBlockedList();
        setupReportedList();
        
        setupListViewBehavior(friendRequestList);
        setupListViewBehavior(searchResultList);
        setupListViewBehavior(blockedList);
        setupListViewBehavior(reportedList);
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

        listView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (oldSelection != null) {
                oldSelection.setStyle("-fx-background-color: transparent;");
            }
            if (newSelection != null) {
                newSelection.setStyle("-fx-background-color: #d0e8ff;");
            }
        });
    }

    @FXML
    private void returnToHomeScreen(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ChatApplication.class.getResource("views/HomeScreen.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        stage = (Stage) friendRequestContent.getScene().getWindow(); // get scene based on an element that in the scene, in this case it is friendRequestContent
        stage.setScene(scene);
        stage.show();
    }
    @FXML   
    private void showFriendRequests() {
        setActiveTab(friendRequestContent);
        friendRequestTab.setStyle("-fx-background-color: #cccccc;");
    }

    @FXML
    private void showFindFriend() {
        setActiveTab(findFriendContent);
        findFriendTab.setStyle("-fx-background-color: #cccccc;");
    }

    @FXML
    private void showBlockedList() {
        setActiveTab(blockedListContent);
        blockedListTab.setStyle("-fx-background-color: #cccccc;");
    }

    @FXML
    private void showReportedList() {
        setActiveTab(reportedListContent);
        reportedListTab.setStyle("-fx-background-color: #cccccc;");
    }

    private void setActiveTab(VBox content) {
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
        ObservableList<HBox> items = FXCollections.observableArrayList();
        items.add(createFriendRequestItem("Username1", "40 minutes ago"));
        items.add(createFriendRequestItem("Username2", "40 minutes ago"));
        friendRequestList.setItems(items);
    }

    private void setupSearchResults() {
        ObservableList<HBox> items = FXCollections.observableArrayList();
        items.add(createSearchResultItem("Username1"));
        searchResultList.setItems(items);
    }

    private void setupBlockedList() {
        ObservableList<HBox> items = FXCollections.observableArrayList();
        items.add(createBlockedUserItem("Username1"));
        blockedList.setItems(items);
    }

    private void setupReportedList() {
        ObservableList<HBox> items = FXCollections.observableArrayList();
        items.add(createReportedUserItem("Username1", "Pending"));
        items.add(createReportedUserItem("Username2", "Resolved"));
        reportedList.setItems(items);
    }

    private HBox createFriendRequestItem(String username, String time) {
        HBox item = new HBox(10);
        item.setPadding(new Insets(8, 15, 8, 15));
        item.setAlignment(Pos.CENTER_LEFT);
        
        ImageView avatar = createCircularAvatar(defaultAvatar);
        
        VBox userInfo = new VBox(5);
        Label usernameLabel = new Label(username);
        usernameLabel.setStyle("-fx-text-fill: black;");
        Label timeLabel = new Label(time);
        timeLabel.getStyleClass().add("time-label");
        userInfo.getChildren().addAll(usernameLabel, timeLabel);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button acceptButton = new Button("Accept");
        acceptButton.getStyleClass().add("accept-button");
        
        Button declineButton = new Button("Decline");
        declineButton.getStyleClass().add("decline-button");
        
        item.getChildren().addAll(avatar, userInfo, spacer, acceptButton, declineButton);
        return item;
    }

    private HBox createSearchResultItem(String username) {
        HBox item = new HBox(10);
        item.setPadding(new Insets(8, 15, 8, 15));
        item.setAlignment(Pos.CENTER_LEFT);
        
        ImageView avatar = createCircularAvatar(defaultAvatar);
        
        Label usernameLabel = new Label(username);
        usernameLabel.setStyle("-fx-text-fill: black;");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button addButton = new Button("Add friend");
        addButton.getStyleClass().add("add-button");
        
        item.getChildren().addAll(avatar, usernameLabel, spacer, addButton);
        return item;
    }

    private HBox createBlockedUserItem(String username) {
        HBox item = new HBox(10);
        item.setPadding(new Insets(8, 15, 8, 15));
        item.setAlignment(Pos.CENTER_LEFT);
        
        ImageView avatar = createCircularAvatar(defaultAvatar);
        
        Label usernameLabel = new Label(username);
        usernameLabel.setStyle("-fx-text-fill: black;");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button removeButton = new Button("Remove block");
        removeButton.getStyleClass().add("remove-block-button");
        
        item.getChildren().addAll(avatar, usernameLabel, spacer, removeButton);
        return item;
    }

    private HBox createReportedUserItem(String username, String status) {
        HBox item = new HBox(10);
        item.setPadding(new Insets(8, 15, 8, 15));
        item.setAlignment(Pos.CENTER_LEFT);
        ImageView avatar = createCircularAvatar(defaultAvatar);
        
        Label usernameLabel = new Label(username);
        usernameLabel.setStyle("-fx-text-fill: black;");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label statusLabel = new Label("Status: ");

        Label statusValue = new Label(status);
        statusValue.getStyleClass().add(status.toLowerCase() + "-status");
        statusLabel.setStyle("-fx-text-fill: black;");
        statusValue.setStyle("-fx-text-fill: black;");
        item.getChildren().addAll(avatar, usernameLabel, spacer, statusLabel, statusValue);
        return item;
    }

    private ImageView createCircularAvatar(Image image) {
        ImageView avatar = new ImageView(image);
        avatar.setFitHeight(40);
        avatar.setFitWidth(40);

        Circle clip = new Circle(20, 20, 20);
        avatar.setClip(clip);

        return avatar;
    }
}