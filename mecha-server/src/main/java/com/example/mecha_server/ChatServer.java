package com.example.mecha_server;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.io.*;
import java.net.*;

public class ChatServer {
    private static final String DB_URL = "jdbc:mysql://localhost:4321/chat_application";
    private static final String DB_USERNAME = "user";
    private static final String DB_PASSWORD = "1234";

    private static final int PORT = 12345;
    private static Map<Integer, ClientHandler> connectedClients = new ConcurrentHashMap<>(); // to keep of all connected client
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is running on port " + PORT);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket);
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class ClientHandler implements Runnable {
        private Socket clientSocket;
        private ObjectInputStream in;
        private ObjectOutputStream out;
        private int userId;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try {
                out = new ObjectOutputStream(clientSocket.getOutputStream());
                out.flush();
                in = new ObjectInputStream(clientSocket.getInputStream());

                while (true) {
                    String action = (String) in.readObject();                    
                    handleAction(action);
                }
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Error handling client: " + e.getMessage());
            }
        }

        private void disconnect() {
            connectedClients.remove(userId);
            try {
                if (out != null) out.close();
                if (in != null) in.close();
                if (clientSocket != null) clientSocket.close();
            } catch (IOException e) {
                System.out.println("Error closing connection: " + e.getMessage());
            }
        }
        private void handleAction(String action) throws IOException, ClassNotFoundException {
            if ("LOGIN".equals(action)) {
                String username = (String) in.readObject();
                String password = (String) in.readObject();
                int userId = authenticateUser(username, password);
                if (userId != 0) {
                    connectedClients.put(userId, this);
                    System.out.println("login client " + username + " successfully: " );
                    out.writeObject("SUCCESS");
                    out.writeObject(userId);
                    out.writeObject(username);
                    System.out.println("all current active user: " + connectedClients.size());
                } else {
                    out.writeObject("FAILURE");
                    
                }
            } else if ("SEND_MESSAGE".equals(action)) {
                int senderId = (int) in.readObject();
                int chatId = (int) in.readObject();
                String message = (String) in.readObject();

                insertMessageIntoDatabase(chatId, senderId, message);
                notifyChatParticipants(chatId, senderId, message);
            } else if ("GET_FRIEND_LIST".equals(action)) {
                int requestId = (int) in.readObject();
                List<String[]> friendList = getFriendForUser(requestId);

                out.writeObject("respond_GET_FRIEND_LIST");
                out.writeObject(friendList);
                System.out.println("friendlist sent");
            } else if ("LOGOUT".equals(action)) {
                int userId = (int) in.readObject();
                connectedClients.remove(userId);
                System.out.println("userId: " + userId + "logged out. Current num of active user: " + connectedClients.size());
            } else if ("GET_CHAT_MESSAGES".equals(action)) {
                int chatId = Integer.parseInt((String)in.readObject());
                List<String[]> messages = getChatMessages(chatId);
                out.writeObject("respond_GET_CHAT_MESSAGES");
                out.writeObject(messages);
            } else if ("SIGNUP".equals(action)) {
                String username = (String) in.readObject();
                String email = (String) in.readObject();
                String passwordHash = (String) in.readObject();
                String address = (String) in.readObject();
                boolean success = registerUser(username, email, passwordHash, address);
                out.writeObject(success ? "SUCCESS" : "FAILURE");
            } else if ("GET_POTENTIAL_FRIENDS".equals(action)) {
                int userId = (int) in.readObject();
                List<String[]> potentialFriends = getPotentialFriends(userId);
                out.writeObject("respond_GET_POTENTIAL_FRIENDS");
                out.writeObject(potentialFriends);
                System.out.println("sent potential users" + potentialFriends);
            } else if ("ADD_FRIEND_REQUEST".equals(action)){
                int senderId = (int)in.readObject();
                int receiverId =  (int)in.readObject();
                insertFriendRequest(senderId, receiverId);
            } else if ("GET_USER_FRIEND_REQUEST".equals(action)){
                int userId = (int) in.readObject();
                List<String[]> requestSent = getRequestSent(userId);
                out.writeObject("respond_GET_USER_FRIEND_REQUEST");
                out.writeObject(requestSent);
            } else if ("CANCEL_FRIEND_REQUEST".equals(action)){
                int userId = (int) in.readObject();
                int friendId = (int) in.readObject();
                deleteFriendRequest(userId, friendId);
            } else if ("GET_FRIEND_REQUEST".equals(action)){
                int userId = (int) in.readObject();
                List<String[]> requestList = getFriendRequest(userId);
                out.writeObject("respond_GET_FRIEND_REQUEST");
                out.writeObject(requestList);
            }
        }

        // Get all of the users that request to be friend with this user(userId)
        private List<String[]> getFriendRequest(int userId){
            List<String[]> messages = new ArrayList<>();
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            PreparedStatement stmt = conn.prepareStatement("""
                SELECT fr.user_id, u.full_name
                FROM friend_request fr
                JOIN users u ON (u.user_id = fr.user_id)
                WHERE fr.friend_id = ?
            """)){  
                stmt.setInt(1, userId);

                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    String friendId = rs.getString("fr.user_id");
                    String friendUsername = rs.getString("u.full_name");
                    messages.add(new String[]{friendId, friendUsername});
                }
                System.out.println("remove friend request complete");
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return messages;
        }


        private void deleteFriendRequest(int senderId, int receiverId){
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            PreparedStatement stmt = conn.prepareStatement("""
                DELETE FROM friend_request
                WHERE user_id = ? AND friend_id = ?
            """)){  
                stmt.setInt(1, senderId);
                stmt.setInt(2, receiverId);

                stmt.executeUpdate();
                System.out.println("remove friend request complete");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        

        private List<String[]> getRequestSent(int userId){
            List<String[]> messages = new ArrayList<>();
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            PreparedStatement stmt = conn.prepareStatement("""
                SELECT fr.friend_id, u.full_name
                FROM friend_request fr 
                JOIN users u ON (fr.friend_id = u.user_id)
                WHERE fr.user_id = ?
                """)){  
                stmt.setInt(1, userId);

                ResultSet rs = stmt.executeQuery();
                while (rs.next()){
                    String friendId = rs.getString("fr.friend_id");
                    String friendUsername = rs.getString("u.full_name");
                    messages.add(new String[]{friendId, friendUsername});
                }
                System.out.println("get friend request to user complete");
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return messages;
        }

        private void insertFriendRequest(int senderId, int receiverId){
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            PreparedStatement stmt = conn.prepareStatement("""
                INSERT INTO friend_request (user_id, friend_id) VALUES (?, ?);
                """)){  
                stmt.setInt(1, senderId);
                stmt.setInt(2, receiverId);

                stmt.executeUpdate();
                System.out.println("insert friend request complete");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


        private static List<String[]> getPotentialFriends(int userId){
            List<String[]> messages = new ArrayList<>();
            String query = """
                    SELECT user_id, full_name 
                    FROM users 
                    WHERE user_id NOT IN (
                        SELECT user1_id FROM friendships WHERE user2_id = ?
                        UNION
                        SELECT user2_id FROM friendships WHERE user1_id = ?
                        UNION   
                        SELECT friend_id FROM friend_request WHERE user_id = ?
                        UNION   
                        SELECT user_id FROM friend_request WHERE friend_id = ?
                    )
                    AND user_id != ?;
                    """;
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
                PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, userId);
                stmt.setInt(2, userId);
                stmt.setInt(3, userId);
                stmt.setInt(4, userId);
                stmt.setInt(5, userId);

                ResultSet rs = stmt.executeQuery();
                while (rs.next()){
                    String potentialUserId = rs.getString("user_id");
                    String potentialUsername = rs.getString("full_name");
                    messages.add(new String[]{potentialUserId, potentialUsername});
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return messages;
        } 

        private static void insertMessageIntoDatabase(int chatId, int senderId, String message) {
            String query = "INSERT INTO messages (chat_id, sender_id, content, created_at) VALUES (?, ?, ?, NOW())";
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
                PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, chatId);
                stmt.setInt(2, senderId);
                stmt.setString(3, message);
                stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        private void notifyChatParticipants(int chatId, int senderId, String message) {
            String query = "SELECT user_id FROM chat_members WHERE chat_id = ?";
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
                PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, chatId);
                ResultSet rs = stmt.executeQuery();
                
                while (rs.next()) {
                    int participantId = rs.getInt("user_id");
                    if (participantId != senderId && connectedClients.containsKey(participantId)) {
                        // System.out.println("userid: " + participantId + ": " + connectedClients.get(participantId).clientSocket);
                        ClientHandler recipient = connectedClients.get(participantId);
                        try {
                            recipient.out.writeObject("NEW_MESSAGE");
                            recipient.out.writeObject(chatId);
                            recipient.out.writeObject(message);
                            recipient.out.writeObject(senderId);
                            // System.out.println("send signal to chatid " + chatId + "from userid " + senderId);
                        } catch (Exception e){  
                            e.printStackTrace();
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        private static List<String[]> getChatMessages(int chatId) {
            List<String[]> messages = new ArrayList<>();
        
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
                PreparedStatement stmt = conn.prepareStatement("""
                    SELECT sender_id, content, created_at
                    FROM messages
                    WHERE chat_id = ? 
                    ORDER BY created_at;
                """)) { 
                stmt.setInt(1, chatId);
                ResultSet rs = stmt.executeQuery();
        
                while (rs.next()) {
                    String sender_id = rs.getString("sender_id");
                    String content = rs.getString("content");
                    String created_at = rs.getString("created_at");
                    messages.add(new String[]{sender_id, content, created_at});
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        
            return messages;
        }

        private static int authenticateUser(String username, String password) {
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
                PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE username = ? AND password_hash = ?")) {
                stmt.setString(1, username);
                stmt.setString(2, password);
                ResultSet rs = stmt.executeQuery();
                if (rs.next())
                    return rs.getInt("user_id");
                else
                    return 0;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return 0;
        }

        private static boolean registerUser(String username, String email, String passwordHash, String address) {
            System.out.println("Registering user: " + username);
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
                PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO users (username, email, address, password_hash, full_name, status) " +
                    "VALUES (?, ?, ?, ?, NULL, 'offline')")) {

                stmt.setString(1, username);
                stmt.setString(2, email);
                stmt.setString(3, address);
                stmt.setString(4, passwordHash);

                int rowsInserted = stmt.executeUpdate();
                return rowsInserted > 0;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        }

        private static List<String[]> getFriendForUser(int userId) {
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
                PreparedStatement stmt = conn.prepareStatement("""
                    SELECT c.chat_id, u.full_name, u.status
                    FROM users u
                    JOIN friendships f ON (u.user_id = f.user1_id OR u.user_id = f.user2_id)
                    JOIN chat_members c ON (c.user_id IN(f.user1_id, f.user2_id))
                    WHERE (f.user1_id = ? OR f.user2_id = ?) AND u.user_id != ?
                    GROUP BY c.chat_id, u.user_id 
                    HAVING COUNT(c.chat_id) = 2;
                """)) {

                stmt.setInt(1, userId);
                stmt.setInt(2, userId);
                stmt.setInt(3, userId);
                List<String[]> friendList = new ArrayList<>();
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    friendList.add(new String[]{rs.getString("c.chat_id"), rs.getString("u.full_name"), rs.getString("u.status")});
                }
                return friendList;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
