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
import java.io.InterruptedIOException;
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
import java.util.Map;
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
    @FXML private HBox searchMessageOption;

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

    // for finding message
    private boolean isDirectingMessage = false;
    private int directedmMessageId = -1;

    // for message option
    private boolean isMessageMenuOpen = false;
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

        searchMessageOption.setVisible(false);
        searchMessageOption.setManaged(false);
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
                    Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
                    confirmationAlert.setTitle("Confirm Report");
                    confirmationAlert.setHeaderText("Do you want to report this user?");
                    confirmationAlert.setContentText("This will be sent to the administrator for review");

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
        
            Label friendSearchLabel = new Label("Search for friends:");
            TextField friendSearchField = new TextField();
            friendSearchField.setPromptText("Search...");
        
            possibleMemberView = new ListView<>();
            possibleMemberView.setMaxHeight(150); 

            VBox addedFriendsBox = new VBox(5);
            ScrollPane addedFriendsScrollPane = new ScrollPane(addedFriendsBox);
            addedFriendsScrollPane.setFitToWidth(true);
            addedFriendsScrollPane.setPrefHeight(150);
        
            try {
                UserSession.out.writeObject("GET_POSSIBLE_FRIEND_FOR_ADDING_TO_CHAT");
                UserSession.out.writeObject(UserSession.getInstance().getUserId());
                UserSession.out.writeObject(currentChat.chatId);
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

        MenuItem deleteAllChatMesssageItem = new MenuItem("Delete all messages");
        deleteAllChatMesssageItem.setOnAction(e -> {
            // handle delete all chat operation here
            boolean isConfirmn = NotificationUtil.showConfirmationBox("Delete all chat messages", "Do you want to delete all messages of this chat");
            if (isConfirmn){
                try {
                    UserSession.out.writeObject("DELETE_ALL_CHAT_MESSAGE");
                    UserSession.out.writeObject(currentChat.chatId);
                    
                    Platform.runLater(() -> {
                        updateChat(currentChat);
                    });
                } catch (IOException ex){
                    ex.printStackTrace();
                }
            }
        });

        MenuItem unfriendItem = new MenuItem("Unfriend");
        unfriendItem.setOnAction(e -> {
            boolean isConfirmn = NotificationUtil.showConfirmationBox("Unfriend", "Do you want to unfriend this person\n");
            if (isConfirmn){
                try {
                    UserSession.out.writeObject("UNFRIEND");
                    UserSession.out.writeObject(UserSession.getInstance().getUserId());
                    UserSession.out.writeObject(currentChat.chatId);
                    
                    Platform.runLater(() -> {
                       initializeChatData();
                       curChatName.setText("");
                       chatListView.getItems().clear();
                       messageFieldFrame.setVisible(false);
                    });
                } catch (IOException ex){
                    ex.printStackTrace();
                } 
            }
        });
        chatOption.setOnMouseClicked(event -> {
            chatOptionMenu.getItems().clear();
            if (currentChat != null) {
                if (currentChat.type == ChatBox.ChatType.PRIVATE) {
                    chatOptionMenu.getItems().addAll(blockUserItem, reportUserItem, deleteAllChatMesssageItem, unfriendItem);
                } else if (currentChat.type == ChatBox.ChatType.GROUP) {
                    chatOptionMenu.getItems().addAll(changeChatNameItem, addMemberItem, deleteAllChatMesssageItem);
                    if (isUserAdmin()) {
                        chatOptionMenu.getItems().addAll(removeMemberItem, assignAdminItem, deleteAllChatMesssageItem);
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

            // Add or update the "Send message" option
            if (!newValue.isEmpty()) {
                searchMessageOption.getChildren().clear();
                Label optionLabel = new Label("Search message for \"" + newValue + "\"");
                searchMessageOption.getChildren().add(optionLabel);
                searchMessageOption.setVisible(true);
                searchMessageOption.setManaged(true);

                searchMessageOption.setOnMouseClicked(event -> {
                    System.out.println("search message for: " + newValue);
                    // TODO: implement search message
                    try {
                        UserSession.out.writeObject("SEARCH_MESSAGE");
                        UserSession.out.writeObject(newValue);
                        UserSession.out.writeObject(UserSession.getInstance().getUserId());
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                });

            } else {
                searchMessageOption.setVisible(false);
                searchMessageOption.setManaged(false);
            }
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
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #333;");
    
        // Create status label
        Label statusLabel = new Label(chat.status);
        statusLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666666;");
    
        // Create status indicator (green or grey dot)
        Circle statusIndicator = new Circle(5);
        if (chat.status.equalsIgnoreCase("online")) {
            statusIndicator.setFill(Paint.valueOf("green"));
        } else {
            statusIndicator.setFill(Paint.valueOf("grey"));
        }
    
        // Create horizontal box for name and status
        HBox nameStatusBox = new HBox(5, nameLabel, statusIndicator, statusLabel);
        nameStatusBox.setAlignment(Pos.CENTER_LEFT);
    
        // Create last message label
        Label lastMessageLabel = new Label(chat.lastMessage);
        lastMessageLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #555;");
    
        // Vertical box to combine name/status and last message
        VBox infoBox = new VBox(5, nameStatusBox, lastMessageLabel);
    
        // Main horizontal box for the chat entry
        HBox chatEntry = new HBox(10, infoBox);
        chatEntry.setPadding(new Insets(10));
        chatEntry.setAlignment(Pos.CENTER_LEFT);
        chatEntry.getStyleClass().add("friend-entry");
        //chatEntry.setStyle("-fx-background-color: #f0f0f0; -fx-background-radius: 10px; -fx-border-color: #ddd; -fx-border-width: 1px; -fx-border-radius: 10px;");

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
                
                updateChat(currentChat);
                // addMessage(message, UserSession.getInstance().getUserId(), UserSession.getInstance().getFullname(), Timestamp.from(Instant.now()));
                messageField.clear();
    
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void addMessage(int messageId, String message, int senderId, String senderFullname, Timestamp timeSent) {
        HBox messageBox = new HBox();
        messageBox.setUserData(messageId);
        messageBox.setPadding(new Insets(5, 10, 5, 10));
        messageBox.setMaxWidth(Double.MAX_VALUE);
    
        // Create a TextFlow with selectable text
        TextFlow messageTextFlow = new TextFlow();
        Text messageText = new Text(message);
        messageText.setStyle("-fx-fill: black;");
        messageTextFlow.getChildren().add(messageText);
        

        messageText.setStyle("-fx-fill: black; -fx-font-size: 14px;");
    
        Label messageLabel = new Label();
        messageLabel.getStyleClass().add("message-label");
        messageLabel.setGraphic(messageTextFlow); // Set the TextFlow as the graphic content
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(chatListView.getWidth() * 0.68);
        messageLabel.setStyle("-fx-background-color: " +
            (senderId == UserSession.getInstance().getUserId() ? "#d1e7ff" : "#f0f0f0") + "; " +
            "-fx-background-radius: 10px; -fx-padding: 5, 10, 5, 10;");
    
   
        messageLabel.setStyle("-fx-background-color: " +
            (senderId == UserSession.getInstance().getUserId() ? "#d1e7ff" : "#f0f0f0") + "; " +
            "-fx-background-radius: 10px; -fx-padding: 5, 10, 5, 10;" +
            "-fx-text-fill: black;"); // Explicitly set text color
    
        
        Label timeLabel = new Label(formatTime(timeSent));
        timeLabel.setStyle("-fx-text-fill: #888888; -fx-font-size: 10px;");
    
        // Create the option icon (hidden by default)
        Label optionIcon = new Label("\u2022\u2022\u2022"); // Unicode for three dots
        optionIcon.setStyle("-fx-text-fill: #888888; -fx-font-size: 14px; -fx-cursor: hand;");
        optionIcon.setVisible(false);

        // Context menu for options
        ContextMenu messageMenu = new ContextMenu();
        MenuItem deleteOption = new MenuItem("Delete Message");
        // Handle delete option click
        deleteOption.setOnAction(e -> {
            System.out.println("Delete message with ID: " + messageId);
            try {
                UserSession.out.writeObject("DELETE_MESSAGE");
                UserSession.out.writeObject(messageId);
                UserSession.out.writeObject(currentChat.chatId);
                isMessageMenuOpen = false;
                chatListView.getItems().remove(messageBox);
            } catch (IOException ex){
                ex.printStackTrace();
            } 
        });
        messageMenu.getItems().add(deleteOption);

        

        // Show context menu on option icon click
        optionIcon.setOnMouseClicked(e -> {
            System.out.println("Icon clicked");
            isMessageMenuOpen = true; // Mark that the context menu is open
        
            // Position the ContextMenu relative to optionIcon
            double iconX = optionIcon.localToScene(optionIcon.getBoundsInLocal()).getMinX();
            double iconY = optionIcon.localToScene(optionIcon.getBoundsInLocal()).getMinY();
            
            double offsetY = 20;  // Position the context menu below the optionIcon
        
            if (!messageMenu.isShowing()) {
                System.out.println("Showing context menu at " + iconX + ", " + (iconY + offsetY));
                messageMenu.show(optionIcon, iconX, iconY + offsetY);
            } else {
                messageMenu.hide();
                isMessageMenuOpen = false; // Reset the flag when hiding
            }
        });
        VBox fullMessageContainer = new VBox(2);
        HBox messageContainer = new HBox(5);
    
        // Handle sender's message alignment
        if (senderId == UserSession.getInstance().getUserId()) {
            messageBox.setAlignment(Pos.CENTER_RIGHT);
            messageContainer.setAlignment(Pos.CENTER_RIGHT);
            messageContainer.getChildren().addAll(optionIcon, timeLabel, messageLabel);
        } else {
            if (currentChat.type == ChatType.GROUP) {
                Label senderNameLabel = new Label(senderFullname);
                senderNameLabel.setStyle("-fx-text-fill: #888888;");
                fullMessageContainer.getChildren().add(senderNameLabel);
            }
            messageBox.setAlignment(Pos.CENTER_LEFT);
            messageContainer.setAlignment(Pos.CENTER_LEFT);
            messageContainer.getChildren().addAll(messageLabel, timeLabel, optionIcon);
        }
    
        fullMessageContainer.getChildren().add(messageContainer);
        messageBox.getChildren().add(fullMessageContainer);
        
        messageBox.setOnMouseEntered(e -> {
            if (!isMessageMenuOpen) {
                optionIcon.setVisible(true);
            }
        });
        messageBox.setOnMouseExited(e -> {
            if (!isMessageMenuOpen) {
                optionIcon.setVisible(false);
            }
        });
        
        chatListView.getItems().add(messageBox);
        if (senderId == UserSession.getInstance().getUserId()) // Scroll to the last message if sent by the user
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

    private HBox createMessageItem(int senderId, String senderName, String content, String createdAt) {
        Label messageLabel = new Label();
        messageLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #333;");
    
        Label timeLabel = new Label(createdAt);
        timeLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #888;");
    
        // Set message content and sender name
        if (senderId == UserSession.getInstance().getUserId()) {
            messageLabel.setText("You: " + content);
        } else {
            messageLabel.setText(senderName + ": " + content);
        }
    
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(350); 
    
        // Message box styling
        VBox messageContainer = new VBox(5, messageLabel, timeLabel);
        HBox messageBox = new HBox(5, messageContainer);
        messageBox.setAlignment(Pos.CENTER_LEFT);
        messageBox.setPadding(new Insets(10));
    
        messageBox.setStyle("-fx-background-color: #f0f0f0; " +
                            "-fx-background-radius: 5px; " +
                            "-fx-padding: 10px; " +
                            "-fx-border-radius: 5px; " +
                            "-fx-border-color: #ddd; " +
                            "-fx-border-width: 1px;");
    
        // Hover effect 
        messageBox.setOnMouseEntered(e -> {
            messageBox.setStyle("-fx-background-color: #e6f7ff; " +
                                "-fx-background-radius: 5px; " +
                                "-fx-padding: 10px; " +
                                "-fx-border-radius: 5px; " +
                                "-fx-border-color: #0077cc; " +
                                "-fx-border-width: 2px;");
        });
    
        messageBox.setOnMouseExited(e -> {
            messageBox.setStyle("-fx-background-color: #f0f0f0; " +
                                "-fx-background-radius: 5px; " +
                                "-fx-padding: 10px; " +
                                "-fx-border-radius: 5px; " +
                                "-fx-border-color: #ddd; " +
                                "-fx-border-width: 1px;");
        });
        return messageBox;
    }
    
    private void redirectToMessage(int chatId, int messageId, String content, String createdAt) {        
        if (currentChat != null) {     
            // try{Thread.sleep(1000);} catch (Exception e){e.printStackTrace();}       
            System.out.println("curr number of message: " + chatListView.getItems().size());
            Platform.runLater(() -> {
                Optional<HBox> foundItem = chatListView.getItems().stream()
                        .filter(item -> (int) item.getUserData() == messageId) // Filter by messageId stored in userData
                        .findFirst();

                if (foundItem.isPresent()) {
                    chatListView.scrollTo(foundItem.get());
                } else {
                    System.out.println("No message found with the specified messageId.");
                }
            });
            
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
            else if ("NEW_MESSAGE".equals(serverMessage)) {
                int chatId = (int) UserSession.in.readObject();
                int messageId = (int) UserSession.in.readObject();
                String message = (String) UserSession.in.readObject();
                int senderId = (int) UserSession.in.readObject();
                String senderFullname = (String) UserSession.in.readObject();
                Timestamp timeSent = (Timestamp) UserSession.in.readObject();
                Platform.runLater(() -> {
                    if (chatId == currentChat.chatId) {
                        addMessage(messageId, message, senderId, senderFullname, timeSent);
                    }
                });
            }
            else if ("respond_GET_CHAT_MESSAGES".equals(serverMessage)) {
                List<String[]> messages = (List<String[]>) UserSession.in.readObject();
                Platform.runLater(() -> {
                    for (String[] msg : messages) {
                        int messageId = Integer.parseInt(msg[0]);
                        int senderId = Integer.parseInt(msg[1]);
                        String senderFullname = msg[2];
                        String message = msg[3];
                        String timeSent = msg[4];
                        
                        Timestamp timestamp = Timestamp.valueOf(timeSent);
                        addMessage(messageId, message, senderId, senderFullname, timestamp);
                    }

                    if (isDirectingMessage){
                        Optional<HBox> foundItem = chatListView.getItems().stream()
                        .filter(item -> (int) item.getUserData() == directedmMessageId) // Filter by messageId stored in userData
                        .findFirst();

                        if (foundItem.isPresent()) {
                            chatListView.scrollTo(foundItem.get());
                        } else {
                            System.out.println("No message found with the specified messageId.");
                        }
                        isDirectingMessage = false;
                        directedmMessageId = -1;
                    }
                });
            }
            else if ("respond_GET_FRIENDS".equals(serverMessage)){
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
            else if ("respond_GET_POSSIBLE_FRIEND_FOR_ADDING_TO_CHAT".equals(serverMessage)){
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
            else if ("respond_GET_CHAT_MEMBERS".equals(serverMessage)){
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
            else if ("CHAT_NAME_CHANGED".equals(serverMessage)){
                int chatId = (int) UserSession.in.readObject();
                String chatName = (String) UserSession.in.readObject();

                initializeChatData();
            }
            else if ("NEW_CHAT".equals(serverMessage)){
                initializeChatData();
            }
            else if ("REMOVED_MEMBER".equals(serverMessage)){
                int chatId = (int) UserSession.in.readObject();
                if (chatId == currentChat.chatId)
                    messageFieldFrame.setVisible(false);
                    
                initializeChatData();
            }
            else if ("ADDED_MEMBER".equals(serverMessage)){
                initializeChatData();
            } else if ("CHANGE_ADMIN".equals(serverMessage)){
                int chatId = (int) UserSession.in.readObject();
                int newAdminId = (int) UserSession.in.readObject();
                initializeChatData();
                if (chatId == currentChat.chatId)
                    currentChat.adminId = newAdminId;
            } else if ("respond_SEARCH_MESSAGE".equals(serverMessage)){
                Map<Integer, List<String[]>> chatData = (Map<Integer, List<String[]>>) UserSession.in.readObject();
                Platform.runLater(() -> {
                    friendListVBox.getChildren().clear();
                    for (Map.Entry<Integer, List<String[]>> entry : chatData.entrySet()) {
                        // Create chat entry displaying chats that contain the search content
                        int chatId = entry.getKey();
                        List<String[]> messages = entry.getValue();

                        System.out.println("Chat ID: " + chatId);

                        // Get the chat name from the first message (all messages have the same chat name)
                        String chatName = messages.get(0)[0];
                        int messageCount = messages.size();

                        // Create VBox for the chat entry
                        VBox chatItem = new VBox();
                        chatItem.setSpacing(5);
                        chatItem.setPadding(new Insets(10));
                        chatItem.setStyle("-fx-background-color: #f0f0f0; -fx-background-radius: 10px; -fx-border-color: #ddd; -fx-border-width: 1px; -fx-border-radius: 10px;");

                        // Chat Name Label
                        Label chatNameLabel = new Label(chatName);
                        chatNameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #333;");

                        // Message Count Label
                        Label messageCountLabel = new Label(messageCount + " messages matched");
                        messageCountLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: gray;");

                        // Add labels to chat item
                        chatItem.getChildren().addAll(chatNameLabel, messageCountLabel);

                        // Add hover effect
                        chatItem.setOnMouseEntered(event -> {
                            chatItem.setStyle("-fx-background-color: #e6f7ff; -fx-background-radius: 10px; -fx-border-color: #0077cc; -fx-border-width: 1px; -fx-border-radius: 10px;");
                        });

                        chatItem.setOnMouseExited(event -> {
                            chatItem.setStyle("-fx-background-color: #f0f0f0; -fx-background-radius: 10px; -fx-border-color: #ddd; -fx-border-width: 1px; -fx-border-radius: 10px;");
                        });

                        // Optional: Add click event to visually indicate selection
                        
                        // add click event to select the chat
                        chatItem.setOnMouseClicked(event -> {
                            System.out.println("Selected Chat ID: " + chatId);
                            friendListVBox.getChildren().clear();
                            
                            ListView<HBox> messagesListView = new ListView<>();
                            messagesListView.setStyle("-fx-background-color: transparent; -fx-border-width: 0;");

                            // Populate messages
                            for (String[] message : messages) {
                                int messageId = Integer.parseInt(message[1]);
                                int senderId = Integer.parseInt(message[2]);
                                String senderName = message[3];
                                String content = message[4];
                                String createdAt = message[5];

                                HBox messageItem = createMessageItem(senderId, senderName, content, createdAt);
                                messageItem.setOnMouseClicked(msgEvent -> {
                                    isDirectingMessage = true;
                                    directedmMessageId = messageId;
                                    currentChat = allChats.stream().filter(chat -> chat.chatId == chatId).findFirst().orElse(null);
                                    updateChat(currentChat);
                                    System.out.println("Message clicked: " + content);
                                    // redirectToMessage(chatId, messageId, content, createdAt);
                                });

                                messagesListView.getItems().add(messageItem);
                            }

                            friendListVBox.getChildren().add(messagesListView);
                        });
                        friendListVBox.getChildren().add(chatItem);
                    }
                });
            } else if ("respond_DELETE_MESSAGE".equals(serverMessage)){
                int chatId = (int) UserSession.in.readObject();
                // Platform.runLater(() -> {
                //     updateChat(currentChat);
                //     chatListView.scrollTo(chatId);
                // });
                
            }
        } catch (Exception e){
            System.out.println("error handling response from server in home controller: ");
            e.printStackTrace();
        }
    }

}