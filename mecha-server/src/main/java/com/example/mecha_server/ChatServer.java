package com.example.mecha_server;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.io.*;
import java.net.*;

public class ChatServer {
    private static final String DB_URL = "jdbc:mysql://localhost:4321/chat_application";
    private static final String DB_USERNAME = "user";
    private static final String DB_PASSWORD = "1234";

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(12345)) {
            System.out.println("Server is running...");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket clientSocket) {
        try (ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
             ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream())) {

            String action = (String) in.readObject();
            if ("LOGIN".equals(action)) {
                String username = (String) in.readObject(); 
                String password = (String) in.readObject();

                int userId = authenticateUser(username, password);
                out.writeObject(userId != 0 ? "SUCCESS" : "FAILURE");
                out.writeObject(userId);
            }
            
            if ("SIGNUP".equals(action)) {
                String username = (String) in.readObject();
                String email = (String) in.readObject();
                String passwordHash = (String) in.readObject();
                String address = (String) in.readObject();

                boolean success = registerUser(username, email, passwordHash, address);
                out.writeObject(success ? "SUCCESS" : "FAILURE");
            }

            if ("GET_FRIEND_LIST".equals(action)) {
                int userId = (int) in.readObject();

                List<String[]> friendList = getFriendForUser(userId);
                out.writeObject(friendList);
            }
            if ("GET_CHAT_MESSAGES".equals(action)) {
                int chatId = Integer.parseInt((String)in.readObject());
        
                List<String[]> messages = getChatMessages(chatId);
                out.writeObject(messages);
            }
        } catch (IOException | ClassNotFoundException e) {
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
