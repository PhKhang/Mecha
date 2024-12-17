package com.example.mechaclient.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.application.Platform;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.example.mechaclient.ChatApplication;
import com.example.mechaclient.models.ChatBox;
import com.example.mechaclient.models.UserSession;
import com.example.mechaclient.models.UserSession.ServerMessageListener;
import com.example.mechaclient.utils.NotificationUtil;
import com.example.mechaclient.models.ChatBox.ChatType;

public class HomeScreenController implements ServerMessageListener{

    @FXML private HBox chatHeader;
    @FXML private Label curChatName;
    @FXML private Label chatOption;
    @FXML private ListView<HBox> chatListView;
    @FXML private TextField messageField;
    @FXML private ImageView settings;
    @FXML private VBox friendListVBox;
    @FXML private TextField searchField;

    @FXML private Button allButton;
    @FXML private Button privateButton;
    @FXML private Button groupsButton;
    @FXML private HBox messageFieldFrame;

    @FXML private Label fullnameLabel;
    private ObservableList<ChatBox> allChats = FXCollections.observableArrayList();
    private FilteredList<ChatBox> filteredChats;
    private HBox selectedFriendEntry;
    private ChatBox currentChat;

    // for create group feature
    ListView<HBox> friendListView = new ListView<>();
    ObservableList<Friend> friendList = FXCollections.observableArrayList();
    List<Friend> addedFriends = new ArrayList<>();
    // for add members feature
    ListView<HBox> possibleMemberView = new ListView<>();
    ObservableList<Friend> possibleMemberList = FXCollections.observableArrayList();
    List<Friend> addedPossibleMember = new ArrayList<>();
    // for remove member feature
    ListView<HBox> memberListView = new ListView<>();
    ObservableList<Friend> memberList = FXCollections.observableArrayList();
    List<Friend> removedMembers = new ArrayList<>();

    public void initialize() {
        messageField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent ke) {
                if (ke.getCode().equals(KeyCode.ENTER)){
                    handleSendMessage();
                }      
            } 
        });
        
        messageFieldFrame.setVisible(false);
        chatOption.setVisible(false);
        fullnameLabel.setText(UserSession.getInstance().getFullname());
        UserSession.getInstance().addMessageListener(this);
        // startResponseListener();
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
        blockUserItem.setOnAction(e -> {
            try {
                boolean isConfirmn = NotificationUtil.showConfirmationBox("Block User", "Do you want to block this user?\nYou will unfriend this person.\nThey will not see you in the add friend session.");
                if (isConfirmn){
                    if (currentChat.type == ChatType.PRIVATE) {
                        UserSession.out.writeObject("BLOCK_USER");
                        UserSession.out.writeObject(UserSession.getInstance().getUserId());
                        UserSession.out.writeObject(currentChat.chatId);
                        UserSession.getInstance().getUserId();
                    }
                    initializeChatData();
                    chatListView.getItems().clear();
                    messageFieldFrame.setVisible(false);
                }
            } catch (IOException ex){
                ex.printStackTrace();
            }
        });

        MenuItem reportUserItem = new MenuItem("Report User");
        reportUserItem.setOnAction(e -> {
            Stage reportDialog = new Stage();
            reportDialog.setTitle("Report User");

            Label reasonLabel = new Label("Report Reason:");
            reasonLabel.setAlignment(Pos.CENTER_LEFT); 
            reasonLabel.setMaxWidth(Double.MAX_VALUE); 
            reasonLabel.getStyleClass().add("dialog-label");

            TextArea reasonField = new TextArea();
            reasonField.setPromptText("Enter your reason for reporting...");
            reasonField.setPrefHeight(200);
            reasonField.setWrapText(true); 
            // reasonField.getStyleClass().add("dialog-textarea");

            // Confirm and Cancel buttons
            Button confirmButton = new Button("Confirm");
            confirmButton.getStyleClass().add("dialog-button-confirm");

            Button cancelButton = new Button("Cancel");
            cancelButton.getStyleClass().add("dialog-button-cancel");

            // HBox for buttons
            HBox buttonBox = new HBox(10, confirmButton, cancelButton);
            buttonBox.setAlignment(Pos.CENTER);

            // VBox to arrange components vertically
            VBox layout = new VBox(10, reasonLabel, reasonField, buttonBox);
            layout.setPadding(new Insets(15));
            layout.setAlignment(Pos.CENTER);
            layout.getStyleClass().add("dialog-layout");


            Scene dialogScene = new Scene(layout, 300, 250); // Adjusted height for TextArea
            dialogScene.getStylesheets().add(ChatApplication.class.getResource("styles/HomeScreenStyle.css").toExternalForm());
            reportDialog.setScene(dialogScene);
            reportDialog.initModality(Modality.APPLICATION_MODAL);
            reportDialog.show();

            // Handle Confirm button click
            confirmButton.setOnAction(ev -> {
                if (!reasonField.getText().trim().isEmpty()) {
                    // Show confirmation alert
                    Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
                    confirmationAlert.setTitle("Confirm Report");
                    confirmationAlert.setHeaderText("Do you want to report this user?");
                    confirmationAlert.setContentText("This will be sent to the administrator for review");

                    // Wait for user response
                    Optional<ButtonType> result = confirmationAlert.showAndWait();
                    if (result.isPresent() && result.get() == ButtonType.OK) {
                        reportUser(currentChat.chatId, reasonField.getText().trim());
                        System.out.println("Report sent: " + reasonField.getText().trim());
                        reportDialog.close();
                    }
                } else {
                    NotificationUtil.showAlert(Alert.AlertType.ERROR, "Error", "Please enter a reason for reporting.");
                }
            });

            // Handle Cancel button click
            cancelButton.setOnAction(ev -> {
                reportDialog.close(); // Keep the dialog open
            });
        });

        MenuItem changeChatNameItem = new MenuItem("Change Chat Name");
        changeChatNameItem.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog(currentChat.name);
            dialog.setTitle("Change Chat Name");
            dialog.setHeaderText("Update the chat name");
            dialog.setContentText("Enter the new chat name:");

            Optional<String> result = dialog.showAndWait();

            result.ifPresent(newName -> {
                if (newName.trim().isEmpty()) {
                    NotificationUtil.showAlert(Alert.AlertType.INFORMATION, "Empty chat name", "Chat name cannot be empty.");
                } else {
                    try {
                        UserSession.out.writeObject("CHANGE_CHAT_NAME");
                        UserSession.out.writeObject(currentChat.chatId);
                        UserSession.out.writeObject(newName);
                        // currentChat.name = newName;
                        // updateChat(currentChat);
                        // displayChats(filteredChats);
                    } catch (IOException ex){
                        ex.printStackTrace();
                    }
                    NotificationUtil.showAlert(Alert.AlertType.INFORMATION, "Success", "Change chat name successfully");
                }
            });
        });

        MenuItem addMemberItem = new MenuItem("Add Member");
        addMemberItem.setOnAction(e -> {
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.NONE);
            popupStage.setTitle("Add Members");
        
            // UI Components
            Label friendSearchLabel = new Label("Search for friends:");
            TextField friendSearchField = new TextField();
            friendSearchField.setPromptText("Search...");
        
            possibleMemberView = new ListView<>();
            possibleMemberView.setMaxHeight(150); 

            VBox addedFriendsBox = new VBox(5);
            ScrollPane addedFriendsScrollPane = new ScrollPane(addedFriendsBox);
            addedFriendsScrollPane.setFitToWidth(true);
            addedFriendsScrollPane.setPrefHeight(150);
        
            // Fetching friends from the server
            try {
                UserSession.out.writeObject("GET_POSSIBLE_FRIEND_FOR_ADDING_TO_CHAT");
                UserSession.out.writeObject(UserSession.getInstance().getUserId());
                UserSession.out.writeObject(currentChat.chatId);
                // Replace with logic to receive the friend list and populate `friendList`
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        
            possibleMemberView.setItems(possibleMemberList.stream()
                    .map(this::createFriendListItem)
                    .collect(Collectors.toCollection(FXCollections::observableArrayList)));
        
            // Filtering friend list
            friendSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
                String filter = newValue.toLowerCase();
                possibleMemberView.setItems(possibleMemberList.stream()
                        .filter(friend -> friend.fullName.toLowerCase().contains(filter))
                        .map(this::createFriendListItem)
                        .collect(Collectors.toCollection(FXCollections::observableArrayList)));
            });
        
            // Handle friend selection
            possibleMemberView.setOnMouseClicked(event -> {
                HBox selectedItem = possibleMemberView.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    Friend selectedFriend = (Friend) selectedItem.getUserData();
                    addedPossibleMember.add(selectedFriend);
        
                    HBox addedFriendItem = createAddedFriendItem(possibleMemberView, possibleMemberList, selectedFriend, addedPossibleMember);
                    addedFriendsBox.getChildren().add(addedFriendItem);
                    
                    friendSearchField.clear();
                    possibleMemberList.remove(selectedFriend);
                    possibleMemberView.setItems(possibleMemberList.stream()
                            .map(this::createFriendListItem)
                            .collect(Collectors.toCollection(FXCollections::observableArrayList)));
                }
            });
        
            // Confirm and Cancel buttons
            Button confirmButton = new Button("Confirm");
            confirmButton.setOnAction(event -> {
                // Pass the added friends list for server processing
                System.out.println("Added Friends: " + addedPossibleMember);
                if (addedPossibleMember.size() < 1){
                    NotificationUtil.showAlert(Alert.AlertType.INFORMATION,"", "Please add at least 1 person.");
                } else {
                    addMemberToChat(currentChat.chatId, addedPossibleMember);
                    popupStage.close();
                }
                
            });
        
            Button cancelButton = new Button("Cancel");
            cancelButton.setOnAction(event -> popupStage.close());
        
            // Layout
            HBox buttonBox = new HBox(10, confirmButton, cancelButton);
            buttonBox.setAlignment(Pos.CENTER);
        
            VBox layout = new VBox(10,
                    friendSearchLabel, friendSearchField,
                    possibleMemberView,
                    new Label("Added Members:"), addedFriendsScrollPane,
                    buttonBox);
            layout.setPadding(new Insets(20));
        
            Scene scene = new Scene(layout, 300, 500);
            popupStage.setScene(scene);
            popupStage.showAndWait();
        });

        MenuItem removeMemberItem = new MenuItem("Remove Member");
        removeMemberItem.setOnAction(e -> {
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.NONE);
            popupStage.setTitle("Remove Member");

            // UI Elements
            Label memberSearchLabel = new Label("Search Members:");
            TextField memberSearchField = new TextField();
            memberSearchField.setPromptText("Search current members");
            memberListView.setMaxHeight(150);
            // Fetching current members from the server
            try {
                UserSession.out.writeObject("GET_CHAT_MEMBERS");
                UserSession.out.writeObject(UserSession.getInstance().getUserId());
                UserSession.out.writeObject(currentChat.chatId);
                // Server response logic will populate 'memberList' (handled by you)
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            memberListView.setItems(memberList.stream()
                    .map(this::createFriendListItem)
                    .collect(Collectors.toCollection(FXCollections::observableArrayList)));

            // Filtering current members based on search
            memberSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
                String lowerCaseFilter = newValue.toLowerCase();
                memberListView.setItems(memberList.stream()
                        .filter(member -> member.fullName.toLowerCase().contains(lowerCaseFilter))
                        .map(this::createFriendListItem)
                        .collect(Collectors.toCollection(FXCollections::observableArrayList)));
            });

            // Handle member removal
            VBox removedMembersBox = new VBox(5);
            removedMembersBox.setStyle("-fx-padding: 10;");
            ScrollPane scrollPane = new ScrollPane(removedMembersBox);
            scrollPane.setFitToWidth(true);
            scrollPane.setPrefHeight(150);

            memberListView.setOnMouseClicked(event -> {
                HBox selectedItem = memberListView.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    Friend selectedMember = (Friend) selectedItem.getUserData();
                    removedMembers.add(selectedMember);

                    HBox removedMemberItem = createAddedFriendItem(memberListView, memberList, selectedMember, removedMembers);
                    removedMembersBox.getChildren().add(removedMemberItem);

                    memberList.remove(selectedMember);
                    memberListView.setItems(memberList.stream()
                            .map(this::createFriendListItem)
                            .collect(Collectors.toCollection(FXCollections::observableArrayList)));
                }
            });

            // Confirm and Cancel buttons
            Button confirmButton = new Button("Confirm");
            confirmButton.setStyle("-fx-background-color: red; -fx-text-fill: white;");
            confirmButton.setOnAction(event -> {
                // Send the removed members back to the server
                if (removedMembers.isEmpty()) {
                    NotificationUtil.showAlert(Alert.AlertType.INFORMATION, "No Members Selected", "Please select members to remove.");
                } else {
                    // Pass removedMembers list for handling
                    // handleRemovedMembers(removedMembers);
                    System.out.println("removed member " + removedMembers);
                    removeMemberFromChat(currentChat.chatId, removedMembers);
                    popupStage.close();
                }
            });

            Button cancelButton = new Button("Cancel");
            cancelButton.setStyle("-fx-background-color: grey; -fx-text-fill: white;");
            cancelButton.setOnAction(event -> popupStage.close());

            HBox buttonBox = new HBox(10, confirmButton, cancelButton);
            buttonBox.setAlignment(Pos.CENTER);

            // Main layout
            VBox layout = new VBox(10,
                    memberSearchLabel, memberSearchField,
                    memberListView,
                    new Label("Removed Members:"), scrollPane,
                    buttonBox
            );
            layout.setPadding(new Insets(20));
            layout.setAlignment(Pos.TOP_CENTER);

            Scene scene = new Scene(layout, 300, 500);
            popupStage.setScene(scene);
            popupStage.showAndWait();
        });

        MenuItem assignAdminItem = new MenuItem("Assign Admin");
        assignAdminItem.setOnAction(e -> {
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setTitle("Assign Admin");
        
            // UI Elements
            Label memberLabel = new Label("Select a Member to assign as Admin:");
            ComboBox<Friend> memberComboBox = new ComboBox<>();
            memberComboBox.setPromptText("Select a member");
        
            Button confirmButton = new Button("Confirm");
            confirmButton.setStyle("-fx-background-color: green; -fx-text-fill: white;");
        
            Button cancelButton = new Button("Cancel");
            cancelButton.setStyle("-fx-background-color: grey; -fx-text-fill: white;");
        

            ObservableList<Friend> memberToAssignAdminList = FXCollections.observableArrayList();
            try {
                UserSession.out.writeObject("GET_CHAT_MEMBERS");
                UserSession.out.writeObject(UserSession.getInstance().getUserId());
                UserSession.out.writeObject(currentChat.chatId);
        
                memberToAssignAdminList = memberList; // reuse from memberList in remove member function
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        
            memberComboBox.setItems(memberToAssignAdminList);
            memberComboBox.setCellFactory(param -> new ListCell<Friend>() {
                @Override
                protected void updateItem(Friend friend, boolean empty) {
                    super.updateItem(friend, empty);
                    setText((friend == null || empty) ? "" : friend.fullName);
                }
            });
            memberComboBox.setButtonCell(new ListCell<Friend>() {
                @Override
                protected void updateItem(Friend friend, boolean empty) {
                    super.updateItem(friend, empty);
                    setText((friend == null || empty) ? "" : friend.fullName);
                }
            });
        
            // Confirmation logic
            confirmButton.setOnAction(event -> {
                Friend selectedMember = memberComboBox.getSelectionModel().getSelectedItem();
                if (selectedMember == null) {
                    NotificationUtil.showAlert(Alert.AlertType.WARNING, "No Member Selected", "Please select a member to assign as admin.");
                } else {
                    // Send selected member to the server for admin assignment
                    try {
                        UserSession.out.writeObject("ASSIGN_ADMIN");
                        UserSession.out.writeObject(currentChat.chatId);
                        UserSession.out.writeObject(selectedMember.userId);
        
                        NotificationUtil.showAlert(Alert.AlertType.INFORMATION, "Admin Assigned", selectedMember.fullName + " is now an admin.");
                        popupStage.close();
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                        NotificationUtil.showAlert(Alert.AlertType.ERROR, "Error", "Failed to assign admin role.");
                    }
                }
            });
        
            cancelButton.setOnAction(event -> popupStage.close());
        
            // Layout
            HBox buttonBox = new HBox(10, confirmButton, cancelButton);
            buttonBox.setAlignment(Pos.CENTER);
        
            VBox layout = new VBox(15, memberLabel, memberComboBox, buttonBox);
            layout.setPadding(new Insets(20));
            layout.setAlignment(Pos.CENTER);
        
            Scene scene = new Scene(layout, 300, 200);
            popupStage.setScene(scene);
            popupStage.showAndWait();
        });
        chatOption.setOnMouseClicked(event -> {
            chatOptionMenu.getItems().clear();
            if (currentChat != null) {
                if (currentChat.type == ChatBox.ChatType.PRIVATE) {
                    chatOptionMenu.getItems().addAll(blockUserItem, reportUserItem);
                } else if (currentChat.type == ChatBox.ChatType.GROUP) {
                    chatOptionMenu.getItems().addAll(changeChatNameItem, addMemberItem);
                    if (isUserAdmin()) {
                        chatOptionMenu.getItems().addAll(removeMemberItem, assignAdminItem);
                    }
                }
            }
            chatOptionMenu.show(chatOption, event.getScreenX(), event.getScreenY());
        });

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

    private void removeMemberFromChat(int chatId, List<Friend> removedUser){
        try {
            UserSession.out.writeObject("REMOVE_CHAT_MEMBER");
            UserSession.out.writeObject(chatId);
            List<Integer> userIds = removedUser.stream()
                                  .map(friend -> friend.userId)
                                  .collect(Collectors.toList());
            UserSession.out.writeObject(userIds);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private void addMemberToChat(int chatId, List <Friend> addedUser){
        try {
            UserSession.out.writeObject("ADD_CHAT_MEMBER");
            UserSession.out.writeObject(chatId);
            List<Integer> userIds = addedUser.stream()
                                  .map(friend -> friend.userId)
                                  .collect(Collectors.toList());
            // System.out.println("chat " + groupName +" created, member num: " + userIds.size());
            UserSession.out.writeObject(userIds);
        } catch (IOException e){
            e.printStackTrace();
        }
    }
    
    private boolean isUserAdmin() {
        return currentChat.adminId == UserSession.getInstance().getUserId();
    }

    private void reportUser(int chatId, String reason){
        try{
            UserSession.out.writeObject("REPORT_USER");
            UserSession.out.writeObject(UserSession.getInstance().getUserId());
            UserSession.out.writeObject(chatId);
            UserSession.out.writeObject(reason);
        } catch (IOException e){
            e.printStackTrace();
        }
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
        allChats.clear();
        try {
            UserSession.out.writeObject("GET_FRIEND_LIST");
            UserSession.out.writeObject(UserSession.getInstance().getUserId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void displayChats(ObservableList<ChatBox> chats) {
        friendListVBox.getChildren().clear();
        for (ChatBox chat : chats) {
            HBox chatEntry = createChatEntry(chat);
            if (currentChat != null && chat.chatId == currentChat.chatId){
                chatEntry.getStyleClass().add("selected");
                curChatName.setText(chat.name);
                selectedFriendEntry = chatEntry;
            }
            friendListVBox.getChildren().add(chatEntry);
        }
    }

    private HBox createChatEntry(ChatBox chat) {
        // Create name label
        Label nameLabel = new Label(chat.name);
        nameLabel.setStyle("-fx-font-weight: bold;");

        // Create status label
        Label statusLabel = new Label(chat.status);
        statusLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #666666;");

        // Create status indicator (green or grey dot)
        Circle statusIndicator = new Circle(5); // 5 is the radius of the circle
        if (chat.status.equalsIgnoreCase("online")) {
            statusIndicator.setFill(Paint.valueOf("green"));
        } else {
            statusIndicator.setFill(Paint.valueOf("grey"));
        }

        // Create horizontal box for name and status
        HBox nameStatusBox = new HBox(5, nameLabel, statusIndicator, statusLabel); // 5px spacing
        nameStatusBox.setAlignment(Pos.CENTER_LEFT);

        // Create last message label
        Label lastMessageLabel = new Label(chat.lastMessage);
        lastMessageLabel.setStyle("-fx-font-size: 12px;");

        // Vertical box to combine name/status and last message
        VBox infoBox = new VBox(2, nameStatusBox, lastMessageLabel);

        // Main horizontal box for the chat entry
        HBox chatEntry = new HBox(10, infoBox);
        chatEntry.setPadding(new Insets(5));
        chatEntry.setAlignment(Pos.CENTER_LEFT);
        chatEntry.getStyleClass().add("friend-entry");
        chatEntry.setStyle("-fx-background-radius: 10");

        // Add click event for selecting the chat
        chatEntry.setOnMouseClicked(event -> {
            messageFieldFrame.setVisible(true);
            this.currentChat = chat;
            updateChat(chat);
            updateSelectedFriend(chatEntry);
        });

        return chatEntry;
    }

    private void updateChat(ChatBox chat) {
        curChatName.setText(chat.name);
        chatListView.getItems().clear();
        try {
            UserSession.out.writeObject("GET_CHAT_MESSAGES");
            UserSession.out.writeObject(String.valueOf(chat.chatId));
        } catch (Exception e) {
            e.printStackTrace();
        }
        messageField.requestFocus();
        chatOption.setVisible(true);
    }

    @FXML
    private void handleSendMessage() {
        String message = messageField.getText();
        if (!message.isEmpty()) {
            try {
                UserSession.out.writeObject("SEND_MESSAGE");
                UserSession.out.writeObject(UserSession.getInstance().getUserId());
                UserSession.out.writeObject(UserSession.getInstance().getFullname());
                UserSession.out.writeObject(currentChat.chatId);
                UserSession.out.writeObject(message);
    
                addMessage(message, UserSession.getInstance().getUserId(), UserSession.getInstance().getFullname(), Timestamp.from(Instant.now()));
                messageField.clear();
    
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void addMessage(String message, int senderId, String senderFullname, Timestamp timeSent) {
        HBox messageBox = new HBox();
        if (currentChat.type == ChatType.PRIVATE)
            System.out.println("this message is for private chat");
        else 
            System.out.println("this message is for group chat");
        messageBox.setPadding(new Insets(5, 10, 5, 10));
        messageBox.setMaxWidth(Double.MAX_VALUE);

        TextFlow textFlow = new TextFlow(new Text(message));
        textFlow.setPadding(new Insets(5, 10, 5, 10));
        textFlow.setStyle("-fx-background-color: " + (senderId == UserSession.getInstance().getUserId() ? "#d1e7ff" : "#f0f0f0") + "; " +
                        "-fx-background-radius: 10px;");

        Label timeLabel = new Label(formatTime(timeSent));
        timeLabel.setStyle("-fx-text-fill: #888888; -fx-font-size: 10px;");

        VBox fullMessageContainer = new VBox(2);
        HBox messageContainer = new HBox(5);

        if (senderId == UserSession.getInstance().getUserId()) {
            messageBox.setAlignment(Pos.CENTER_RIGHT);
            textFlow.setMaxWidth(chatListView.getWidth() * 0.75);
            messageContainer.setAlignment(Pos.CENTER_RIGHT);
            messageContainer.getChildren().addAll(timeLabel, textFlow);
        } else {
            if (currentChat.type == ChatType.GROUP){
                Text senderNameText = new Text(senderFullname);
                senderNameText.setStyle("-fx-fill: #888888;"); // Set sender's name to grey
                fullMessageContainer.getChildren().add(senderNameText);
            }
            messageBox.setAlignment(Pos.CENTER_LEFT);
            textFlow.setMaxWidth(chatListView.getWidth() * 0.75);
            messageContainer.setAlignment(Pos.CENTER_LEFT);
            messageContainer.getChildren().addAll(textFlow, timeLabel);
        }
        fullMessageContainer.getChildren().add(messageContainer);
        messageBox.getChildren().add(fullMessageContainer);
        messageBox.setOnMouseClicked(e -> {
            System.out.println("message clicked");
        });
        chatListView.getItems().add(messageBox);
        if (senderId == UserSession.getInstance().getUserId()) // if the user is sending this then redirect to the last message
            chatListView.scrollTo(chatListView.getItems().size() - 1);
    }

    private String formatTime(Timestamp timeSent) {
        LocalDateTime messageTime = timeSent.toLocalDateTime(); // Convert Timestamp to LocalDateTime
        LocalDateTime now = LocalDateTime.now();
    
        if (messageTime.toLocalDate().equals(now.toLocalDate())) {
            // Same day, show only time
            return messageTime.format(DateTimeFormatter.ofPattern("HH:mm"));
        } else if (messageTime.getYear() == now.getYear()) {
            // Same year, show time and date without year
            return messageTime.format(DateTimeFormatter.ofPattern("HH:mm, dd/MM"));
        } else {
            // Different year, show full date and time
            return messageTime.format(DateTimeFormatter.ofPattern("HH:mm, dd/MM/yyyy"));
        }
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

        Label groupNameLabel = new Label("Group Name:");
        TextField groupNameField = new TextField();
        groupNameField.setPromptText("Enter group name");

        Label friendSearchLabel = new Label("Add Friends:");
        TextField friendSearchField = new TextField();
        friendSearchField.setPromptText("Search for friends");

        friendListView = new ListView<>();
        friendListView.setMaxHeight(150);
        // getting friend for display in the server
        try {
            UserSession.out.writeObject("GET_FRIENDS");
            UserSession.out.writeObject(UserSession.getInstance().getUserId());

        } catch (IOException e) {
            e.printStackTrace();
        }
        friendListView.setItems(friendList.stream()
            .map(this::createFriendListItem)
            .collect(Collectors.toCollection(FXCollections::observableArrayList)));

        Label addedFriendsLabel = new Label("Added Friends:");
        VBox addedFriendsBox = new VBox(5); 
        addedFriendsBox.setStyle("-fx-padding: 10;");
        ScrollPane scrollPane = new ScrollPane(addedFriendsBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(150);

        friendSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
            String lowerCaseFilter = newValue.toLowerCase();
            friendListView.setItems(friendList.stream()
                .filter(friend -> friend.fullName.toLowerCase().contains(lowerCaseFilter))
                .map(this::createFriendListItem)
                .collect(Collectors.toCollection(FXCollections::observableArrayList)));
        });

        // Handle friend selection from the dropdown
        friendListView.setOnMouseClicked(event -> {
            HBox selectedItem = friendListView.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                Friend selectedFriend = (Friend) selectedItem.getUserData();
                addedFriends.add(selectedFriend);
                HBox addedFriendItem = createAddedFriendItem(friendListView, friendList, selectedFriend, addedFriends);
                addedFriendsBox.getChildren().add(addedFriendItem);

                friendSearchField.clear();
                friendList.remove(selectedFriend);
                friendListView.setItems(friendList.stream()
                    .map(this::createFriendListItem)
                    .collect(Collectors.toCollection(FXCollections::observableArrayList)));
            }
        });

        // Confirm and Cancel buttons
        Button confirmButton = new Button("Confirm");
        confirmButton.setStyle("-fx-background-color: blue; -fx-text-fill: white;");
        confirmButton.setOnAction(e -> {
            String groupName = groupNameField.getText().trim();
            // System.out.println("addedFriend size before the function: " + addedFriends + ", friendList: " + friendList.size());
            if (groupName.isEmpty()) {
                NotificationUtil.showAlert(Alert.AlertType.INFORMATION,"Empty group name", "Please enter a group name.");
            } else if (addedFriends.size() < 2){
                NotificationUtil.showAlert(Alert.AlertType.INFORMATION,"Not enough people", "Please add at least 2 friend.");
            } else {
                System.out.println("created group, member: " + addedFriends);
                createGroupChat(groupName, addedFriends);
                popupStage.close();
            }
            // popupStage.close();
        });

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
                friendListView,
                addedFriendsLabel, scrollPane,
                buttonBox
        );
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.TOP_CENTER);

        Scene scene = new Scene(layout, 300, 500);
        popupStage.setScene(scene);
        popupStage.showAndWait();
    }

    private HBox createFriendListItem(Friend friend) {
        if (friend == null)
            System.out.println("This Friend is null!");
        Label nameLabel = new Label(friend.fullName);
        HBox item = new HBox(10, nameLabel);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setUserData(friend);
        return item;
    }
    
    private HBox createAddedFriendItem(ListView<HBox> _friendListView, ObservableList<Friend> _friendList, Friend friend, List<Friend> AddedList) {
        Label friendLabel = new Label(friend.fullName);
        Button removeButton = new Button("×");
        removeButton.setStyle("-fx-background-radius: 50%; -fx-min-width: 20px; -fx-min-height: 20px; -fx-max-width: 20px; -fx-max-height: 20px; -fx-font-size: 14px; -fx-padding: 0;");
    

        HBox addedFriendItem = new HBox(5, friendLabel, removeButton);
        addedFriendItem.setAlignment(Pos.CENTER_LEFT);
        
        removeButton.setOnAction(e -> {
            AddedList.remove(friend);
            ((VBox) addedFriendItem.getParent()).getChildren().remove(addedFriendItem);
            _friendList.add(friend);
            _friendListView.setItems(_friendList.stream()
                .map(this::createFriendListItem)
                .collect(Collectors.toCollection(FXCollections::observableArrayList)));
        });
        
        return addedFriendItem;
    }

    private class Friend {
        public String fullName;
        public int userId;
    
        Friend(String fullName, int userId) {
            this.fullName = fullName;
            this.userId = userId;
        }
    }

    private void createGroupChat(String groupName, List<Friend> friendList){
        try {
            UserSession.out.writeObject("CREATE_CHAT_GROUP");
            UserSession.out.writeObject(groupName);
            UserSession.out.writeObject(UserSession.getInstance().getUserId());
            List<Integer> userIds = friendList.stream()
                                  .map(friend -> friend.userId)
                                  .collect(Collectors.toList());
                                  userIds.add(UserSession.getInstance().getUserId());
            // System.out.println("chat " + groupName +" created, member num: " + userIds.size());
            UserSession.out.writeObject(userIds);
        } catch (IOException e){
            e.printStackTrace();
        }
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
            stage.setOnCloseRequest(e -> {
                UserSession.getInstance().Logout();
            });
            stage.show();
            UserSession.getInstance().removeMessageListener(this);
            
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
            stage.setOnCloseRequest(e -> {
                UserSession.getInstance().Logout();
            });
            stage.show();
            UserSession.getInstance().removeMessageListener(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleLogout(javafx.event.ActionEvent event) {
        try {
            UserSession.getInstance().Logout();

            FXMLLoader fxmlLoader = new FXMLLoader(ChatApplication.class.getResource("views/LoginScreen.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 800, 600);
            Stage stage = (Stage) ((MenuItem) event.getSource()).getParentPopup().getOwnerWindow();
            stage.setScene(scene);
            stage.setOnCloseRequest(e -> {
                UserSession.getInstance().Logout();
            });
            stage.show();
            UserSession.getInstance().removeMessageListener(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessageReceived(String serverMessage) {
        try {
            if ("respond_GET_FRIEND_LIST".equals(serverMessage)) {
                List<String[]> friendList = (List<String[]>) UserSession.in.readObject();
                Platform.runLater(() -> {
                    for (String[] friend : friendList) {
                        int chatId = Integer.parseInt(friend[0]);
                        String fullname = friend[1];
                        String status = friend[2];
                        String chatType = friend[3];
                        int adminId = Integer.parseInt(friend[4]);
                        if (chatType.equals("private"))
                            allChats.add(new ChatBox(fullname, ChatType.PRIVATE, status, "none", chatId, adminId));
                        else
                            allChats.add(new ChatBox(fullname, ChatType.GROUP, status, "none", chatId, adminId));
                    }
                    Platform.runLater(() -> {
                        displayChats(filteredChats);
                    });
                });
                
            } 
            if ("NEW_MESSAGE".equals(serverMessage)) {
                int chatId = (int) UserSession.in.readObject();
                String message = (String) UserSession.in.readObject();
                int senderId = (int) UserSession.in.readObject();
                String senderFullname = (String) UserSession.in.readObject();
                Timestamp timeSent = (Timestamp) UserSession.in.readObject();
                Platform.runLater(() -> {
                    if (chatId == currentChat.chatId) {
                        addMessage(message, senderId, senderFullname, timeSent);
                    }
                });
            }
            if ("respond_GET_CHAT_MESSAGES".equals(serverMessage)) {
                List<String[]> messages = (List<String[]>) UserSession.in.readObject();
                Platform.runLater(() -> {
                    for (String[] msg : messages) {
                        int senderId = Integer.parseInt(msg[0]);
                        String senderFullname = msg[1];
                        String message = msg[2];
                        String timeSent = msg[3];
                        
                        Timestamp timestamp = Timestamp.valueOf(timeSent);
                        addMessage(message, senderId, senderFullname, timestamp);
                    }
                });
            }
            if ("respond_GET_FRIENDS".equals(serverMessage)){
                List<String[]> receivedFriendList = (List<String[]>) UserSession.in.readObject();
                
                Platform.runLater(() -> {
                    friendList.clear();
                    for (String[] friend : receivedFriendList) {
                        friendList.add(new Friend(friend[1], Integer.parseInt(friend[0])));
                    }
                    
                    friendListView.setItems(friendList.stream()
                        .map(this::createFriendListItem)
                        .collect(Collectors.toCollection(FXCollections::observableArrayList)));
                });
            }
            if ("respond_GET_POSSIBLE_FRIEND_FOR_ADDING_TO_CHAT".equals(serverMessage)){
                List<String[]> possibleFriend = (List<String[]>) UserSession.in.readObject();

                Platform.runLater(() -> {
                    possibleMemberList.clear();
                    for (String[] friend : possibleFriend) {
                        possibleMemberList.add(new Friend(friend[1], Integer.parseInt(friend[0])));
                    }
                    
                    possibleMemberView.setItems(possibleMemberList.stream()
                        .map(this::createFriendListItem)
                        .collect(Collectors.toCollection(FXCollections::observableArrayList)));
                });
            }
            if ("respond_GET_CHAT_MEMBERS".equals(serverMessage)){
                List<String[]> receivedMemberList = (List<String[]>) UserSession.in.readObject(); 

                Platform.runLater(() -> {
                    memberList.clear();
                    for (String[] friend : receivedMemberList) {
                        memberList.add(new Friend(friend[1], Integer.parseInt(friend[0])));
                    }
                    
                    memberListView.setItems(memberList.stream()
                        .map(this::createFriendListItem)
                        .collect(Collectors.toCollection(FXCollections::observableArrayList)));
                });
            }
            if ("CHAT_NAME_CHANGED".equals(serverMessage)){
                int chatId = (int) UserSession.in.readObject();
                String chatName = (String) UserSession.in.readObject();

                initializeChatData();
            }
            if ("NEW_CHAT".equals(serverMessage)){
                initializeChatData();
            }
            if ("REMOVED_MEMBER".equals(serverMessage)){
                int chatId = (int) UserSession.in.readObject();
                if (chatId == currentChat.chatId)
                    messageFieldFrame.setVisible(false);
                    
                initializeChatData();
            }
            if ("ADDED_MEMBER".equals(serverMessage)){
                initializeChatData();
            } else if ("CHANGE_ADMIN".equals(serverMessage)){
                int chatId = (int) UserSession.in.readObject();
                int newAdminId = (int) UserSession.in.readObject();
                initializeChatData();
                if (chatId == currentChat.chatId)
                    currentChat.adminId = newAdminId;
            } 
        } catch (Exception e){
            System.out.println("error handling response from server in home controller: ");
            e.printStackTrace();
        }
    }

}