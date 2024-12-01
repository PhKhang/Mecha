package com.example.mecha_server;

import java.sql.*;
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

                boolean success = authenticateUser(username, password);
                out.writeObject(success ? "SUCCESS" : "FAILURE");
            }
            
            if ("SIGNUP".equals(action)) {
                String username = (String) in.readObject();
                String email = (String) in.readObject();
                String passwordHash = (String) in.readObject();
                String address = (String) in.readObject();

                boolean success = registerUser(username, email, passwordHash, address);
                out.writeObject(success ? "SUCCESS" : "FAILURE");
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static boolean authenticateUser(String username, String password) {
        System.out.println("authen");
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE username = ? AND password_hash = ?")) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            System.out.println(username +  " " + password);
            ResultSet rs = stmt.executeQuery();
            return rs.next(); // Returns true if user exists
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
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
}
