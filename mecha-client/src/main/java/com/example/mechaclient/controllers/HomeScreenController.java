package com.example.mechaclient.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.stage.Modality;

import java.io.IOException;

import com.example.mechaclient.ChatApplication;

public class HomeScreenController {

    @FXML private HBox chatHeader;
    @FXML private ImageView curUserAva;
    @FXML private Label curUserName;
    @FXML private Label chatOption;
    @FXML private ListView<HBox> chatListView;
    @FXML private TextField messageField;
    @FXML private ImageView settings;
    @FXML private VBox friendListVBox;
    @FXML private TextField searchField;

    @FXML private Button allButton;
    @FXML private Button privateButton;
    @FXML private Button groupsButton;

    private ObservableList<Chat> allChats = FXCollections.observableArrayList();
    private FilteredList<Chat> filteredChats;
    private HBox selectedFriendEntry;

    public void initialize() {
        chatOption.setVisible(false);
        setupContextMenus();
        setupChatListView();
        setupSearchField();
        initializeChatData();
        updateSelectedButton(allButton);
        displayChats(filteredChats);
    }

    private void setupContextMenus() {
        ContextMenu chatOptionMenu = new ContextMenu();
        MenuItem blockUserItem = new MenuItem("Block User");
        MenuItem reportUserItem = new MenuItem("Report User");
        chatOptionMenu.getItems().addAll(blockUserItem, reportUserItem);

        chatOption.setOnMouseClicked(event -> chatOptionMenu.show(chatOption, event.getScreenX(), event.getScreenY()));

        ContextMenu settingsMenu = new ContextMenu();
        MenuItem friendManagementItem = new MenuItem("Friend Management");
        MenuItem profileItem = new MenuItem("My Profile");
        MenuItem logoutItem = new MenuItem("Log out");
        settingsMenu.getItems().addAll(friendManagementItem, profileItem, logoutItem);

        settings.setOnMouseClicked(event -> settingsMenu.show(settings, event.getScreenX(), event.getScreenY()));

        friendManagementItem.setOnAction(this::handleFriendManagement);
        profileItem.setOnAction(this::handleProfile);
        logoutItem.setOnAction(this::handleLogout);
    }

    private void setupChatListView() {
        chatListView.setCellFactory(param -> new ListCell<HBox>() {
            @Override
            protected void updateItem(HBox item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setGraphic(item);
                    setBackground(null);
                }
            }
        });
    }

    private void setupSearchField() {
        filteredChats = new FilteredList<>(allChats, p -> true);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredChats.setPredicate(chat -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                return chat.name.toLowerCase().contains(newValue.toLowerCase());
            });
            displayChats(filteredChats);
        });
    }

    private void initializeChatData() {
        allChats.addAll(
            new Chat("Alice Doe", ChatType.PRIVATE, "online", "Hello there!"),
            new Chat("Bob Smith", ChatType.PRIVATE, "3 hours ago", "See you tomorrow"),
            new Chat("Project Team", ChatType.GROUP, "5 members", "Meeting at 2 PM"),
            new Chat("Charlie Brown", ChatType.PRIVATE, "1 day ago", "Thanks for your help"),
            new Chat("Family Group", ChatType.GROUP, "8 members", "Happy birthday, mom!")
        );
    }

    private void displayChats(ObservableList<Chat> chats) {
        friendListVBox.getChildren().clear();
        for (Chat chat : chats) {
            HBox chatEntry = createChatEntry(chat);
            friendListVBox.getChildren().add(chatEntry);
        }
    }

    private HBox createChatEntry(Chat chat) {
        ImageView avatar = new ImageView(new Image(getClass().getResourceAsStream("/com/example/mechaclient/images/default-ava.png")));
        avatar.setFitHeight(40);
        avatar.setFitWidth(40);

        VBox infoBox = new VBox(2);
        Label nameLabel = new Label(chat.name);
        nameLabel.setStyle("-fx-font-weight: bold;");
        Label statusLabel = new Label(chat.status);
        statusLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #666666;");
        Label lastMessageLabel = new Label(chat.lastMessage);
        lastMessageLabel.setStyle("-fx-font-size: 12px;");
        infoBox.getChildren().addAll(nameLabel, statusLabel, lastMessageLabel);

        HBox chatEntry = new HBox(10, avatar, infoBox);
        chatEntry.setPadding(new Insets(5));
        chatEntry.setAlignment(Pos.CENTER_LEFT);
        chatEntry.getStyleClass().add("friend-entry");
        chatEntry.setStyle("-fx-background-radius: 10");

        chatEntry.setOnMouseClicked(event -> {
            updateChat(chat);
            updateSelectedFriend(chatEntry);
        });

        return chatEntry;
    }

    private void updateChat(Chat chat) {
        curUserAva.setImage(new Image(getClass().getResourceAsStream("/com/example/mechaclient/images/default-ava.png")));
        curUserName.setText(chat.name);
        chatListView.getItems().clear();
        addMessage("Hello", false);
        addMessage("Hi there", true);
        chatOption.setVisible(true);
    }

    @FXML
    private void handleSendMessage() {
        String message = messageField.getText();
        if (!message.isEmpty()) {
            addMessage(message, true);
            messageField.clear();
        }
    }

    private void addMessage(String message, boolean isUser) {
        HBox messageBox = new HBox();
        messageBox.setPadding(new Insets(5, 10, 5, 10));
        messageBox.setMaxWidth(Double.MAX_VALUE);

        TextFlow textFlow = new TextFlow(new Text(message));
        textFlow.setPadding(new Insets(5, 10, 5, 10));
        textFlow.setStyle("-fx-background-color: " + (isUser ? "#d1e7ff" : "#f0f0f0") + "; " +
                        "-fx-background-radius: 10px;");

        if (isUser) {
            messageBox.setAlignment(Pos.CENTER_RIGHT);
            textFlow.setMaxWidth(chatListView.getWidth() * 0.75);
        } else {
            messageBox.setAlignment(Pos.CENTER_LEFT);
            textFlow.setMaxWidth(chatListView.getWidth() * 0.75);
        }

        messageBox.getChildren().add(textFlow);
        chatListView.getItems().add(messageBox);
        chatListView.scrollTo(chatListView.getItems().size() - 1);
    }

    private void updateSelectedFriend(HBox newSelection) {
        if (selectedFriendEntry != null) {
            selectedFriendEntry.getStyleClass().remove("selected");
        }
        newSelection.getStyleClass().add("selected");
        selectedFriendEntry = newSelection;
    }


    @FXML
    private void showCreateGroupPopup() {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.NONE);
        popupStage.setTitle("Create Group");
    
        // Group name input
        Label groupNameLabel = new Label("Group Name:");
        TextField groupNameField = new TextField();
        groupNameField.setPromptText("Enter group name");
    
        // Friend search bar
        Label friendSearchLabel = new Label("Add Friends:");
        TextField friendSearchField = new TextField();
        friendSearchField.setPromptText("Search for friends");
    
        // Added friends display
        Label addedFriendsLabel = new Label("Added Friends:");
        TextArea addedFriendsField = new TextArea();
        addedFriendsField.setEditable(false);
        addedFriendsField.setPrefHeight(80);
    
        // Confirm and Cancel buttons
        Button confirmButton = new Button("Confirm");
        confirmButton.setStyle("-fx-background-color: blue; -fx-text-fill: white;");
        confirmButton.setOnAction(e -> popupStage.close());
    
        Button cancelButton = new Button("Cancel");
        cancelButton.setStyle("-fx-background-color: grey; -fx-text-fill: white;");
        cancelButton.setOnAction(e -> popupStage.close());
    
        // Layout for buttons
        HBox buttonBox = new HBox(10, confirmButton, cancelButton);
        buttonBox.setAlignment(Pos.CENTER);
    
        // Main layout
        VBox layout = new VBox(10,
                groupNameLabel, groupNameField,
                friendSearchLabel, friendSearchField,
                addedFriendsLabel, addedFriendsField,
                buttonBox
        );
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.TOP_CENTER);
    
        // Scene and show
        Scene scene = new Scene(layout, 300, 400);
        popupStage.setScene(scene);
        popupStage.showAndWait();
    }

    @FXML
    private void getAllChat() {
        filteredChats.setPredicate(chat -> true);
        displayChats(filteredChats);
        updateSelectedButton(allButton);
    }

    @FXML
    private void getPrivateChat() {
        filteredChats.setPredicate(chat -> chat.type == ChatType.PRIVATE);
        displayChats(filteredChats);
        updateSelectedButton(privateButton);
    }

    @FXML
    private void getGroupChat() {
        filteredChats.setPredicate(chat -> chat.type == ChatType.GROUP);
        displayChats(filteredChats);
        updateSelectedButton(groupsButton);
    }

    
    private void updateSelectedButton(Button selectedButton) {
        allButton.getStyleClass().remove("selected");
        privateButton.getStyleClass().remove("selected");
        groupsButton.getStyleClass().remove("selected");
        selectedButton.getStyleClass().add("selected");
    }

    private void handleFriendManagement(javafx.event.ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(ChatApplication.class.getResource("views/FriendManagement.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 800, 600);
            Stage stage = (Stage) settings.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleProfile(javafx.event.ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(ChatApplication.class.getResource("views/ProfileScreen.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 800, 600);
            Stage stage = (Stage) ((MenuItem) event.getSource()).getParentPopup().getOwnerWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleLogout(javafx.event.ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(ChatApplication.class.getResource("views/LoginScreen.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 800, 600);
            Stage stage = (Stage) ((MenuItem) event.getSource()).getParentPopup().getOwnerWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class Chat {
        String name;
        ChatType type;
        String status;
        String lastMessage;

        Chat(String name, ChatType type, String status, String lastMessage) {
            this.name = name;
            this.type = type;
            this.status = status;
            this.lastMessage = lastMessage;
        }
    }

    private enum ChatType {
        PRIVATE, GROUP
    }
}