package com.example.mechaclient;

import java.io.IOException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

public class HomeScreenController {

    @FXML
    private Label chatHeader;

    @FXML
    private ListView<HBox> chatListView;

    @FXML
    private TextField messageField;

    private ObservableList<String> friends = FXCollections.observableArrayList(
            "Alice Doe",
            "Bob Smith",
            "Charlie Brown",   
            "Charlie C"  
    );
    
    private HBox selectedFriendEntry = null;
    @FXML
    private ImageView settings;  // ImageView for settings icon

    @FXML
    private Button option1;
    @FXML
    private Button option2; 
    @FXML
    private Button option3;  

    @FXML
    private VBox friendListVBox; // VBox to hold friend entries
    @FXML
    private TextField searchField; // Search bar
    private FilteredList<String> filteredFriends;

    public void initialize() {
        filteredFriends = new FilteredList<>(friends, p -> true);

        displayFriends(filteredFriends);

        // Add a listener to the search bar to filter the list based on the text input
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredFriends.setPredicate(friend -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true; // Show all items if search bar is empty
                }
                return friend.toLowerCase().contains(newValue.toLowerCase());
            });
            displayFriends(filteredFriends);
        });

        ContextMenu contextMenu = new ContextMenu();
        
        MenuItem option1Item = new MenuItem("Option 1");
        MenuItem option2Item = new MenuItem("Option 2");
        MenuItem option3Item = new MenuItem("Log out");

        option1Item.setOnAction(this::handleOption1);
        option2Item.setOnAction(this::handleOption2);
        option3Item.setOnAction(this::handleLogout);

        contextMenu.getItems().addAll(option1Item, option2Item, option3Item);

        // Show the context menu when the settings icon is clicked
        settings.setOnMouseClicked(event -> {
            contextMenu.show(settings, settings.getScene().getWindow().getX() + settings.getLayoutX() + settings.getFitWidth(), settings.getScene().getWindow().getY() + settings.getLayoutY() + settings.getFitHeight());
        });
    

        chatListView.setCellFactory(param -> {
            ListCell<HBox> cell = new ListCell<>() {
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
            };
        
            // Remove cell focus and selection highlighting
            cell.setStyle("-fx-background-color: transparent;"); 
            cell.setFocusTraversable(false);
            cell.setMouseTransparent(false);
        
            return cell;
        });
        
    }

    private void displayFriends(ObservableList<String> friendsList) {
        friendListVBox.getChildren().clear(); // Clear existing friend list
        for (String friend : friendsList) {
            String name = friend;

            Label nameLabel = new Label(name);

            HBox friendEntry = new HBox(5, nameLabel);
            friendEntry.setPadding(new Insets(5));
            friendEntry.setAlignment(Pos.CENTER_LEFT);

            friendEntry.setOnMouseClicked(event -> {
                updateChat(name);
                searchField.clear();
                if (selectedFriendEntry != null) {
                    selectedFriendEntry.setStyle("-fx-background-color: transparent;"); // Reset previous
                }
                friendEntry.setStyle("-fx-background-color: lightgray;"); // Highlight new selection
                selectedFriendEntry = friendEntry; // Update selected entry 
            });
            
            friendListVBox.getChildren().add(friendEntry);
        }
    }
    private void handleOption1(ActionEvent event) {
        System.out.println("Option 1 selected");
    }

    private void handleOption2(ActionEvent event) {
        System.out.println("Option 2 selected");
    }

    private void handleLogout(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(ChatApplication.class.getResource("LoginScreen.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 600, 400);
    
            // Get the current stage (window)
            Stage stage = (Stage) ((MenuItem) event.getSource()).getParentPopup().getOwnerWindow();

            stage.setScene(scene);  
            stage.show();  
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void updateChat(String friend) {
        String friendName = friend.split(" \\(")[0]; 
        chatHeader.setText("Chat with " + friendName);
        chatListView.getItems().clear();
        // Simulating chat history
        addMessage("Hello", false);
        addMessage("Hi there", true);
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
}