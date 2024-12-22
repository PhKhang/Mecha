package com.example.mecha_server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.RandomStringUtils;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

class EmailSender {

    public static void sendEmail(String email, String subject, String content) {
        Properties prop = new Properties();
        prop.put("mail.smtp.auth", true);
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        Dotenv dotenv = Dotenv.load();
        System.out.println(dotenv.get("EMAIL_USERNAME") + " " + dotenv.get("EMAIL_PASSWORD"));
        Session session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(dotenv.get("EMAIL_USERNAME"), dotenv.get("EMAIL_PASSWORD"));
            }
        });

        MimeMessage message = new MimeMessage(session);
        try {
            message.setFrom(new InternetAddress(dotenv.get("EMAIL_USERNAME")));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject(subject);
            // message.setText(content);
            message.setContent(content, "text/html; charset=utf-8");

            Transport.send(message);
            System.out.println("Email sent successfully");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

public class ChatServer {
    private static final String DB_URL = "jdbc:mysql://localhost:4321/chat_application";
    private static final String DB_USERNAME = "user";
    private static final String DB_PASSWORD = "1234";

    private static final int PORT = 12345;
    private static Map<Integer, ClientHandler> connectedClients = new ConcurrentHashMap<>(); // to keep track of all
                                                                                             // connected client

    public static void main(String[] args) {
        // EmailSender.sendEmail(
        // "tnpkhang22@clc.fitus.edu.vn",
        // "Test email",
        // "<!doctype html>\r\n" + //
        // "<html>\r\n" + //
        // " <head>\r\n" + //
        // " <title>This is the title of the webpage!</title>\r\n" + //
        // " </head>\r\n" + //
        // " <body>\r\n" + //
        // " <p>This is an example paragraph. Anything in the <strong>body</strong> tag
        // will appear on the page, just like this <strong>p</strong> tag and its
        // contents.</p>\r\n"
        // + //
        // " </body>\r\n" + //
        // "</html>");

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
                    System.out.println("request: " + action);
                    try {
                        handleAction(action);
                    } catch (IOException | ClassNotFoundException | SQLException | ParseException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Error handling client: " + e.getMessage());
            }
        }

        private void disconnect() {
            connectedClients.remove(userId);
            try {
                if (out != null)
                    out.close();
                if (in != null)
                    in.close();
                if (clientSocket != null)
                    clientSocket.close();
            } catch (IOException e) {
                System.out.println("Error closing connection: " + e.getMessage());
            }
        }

        private void handleAction(String action) throws IOException, ClassNotFoundException, SQLException, ParseException {
            if ("LOGIN".equals(action)) {
                String username = (String) in.readObject();
                String password = (String) in.readObject();
                List<String> userInfo = new ArrayList<>(); 
                // User info is user_id and fullname
                String loginStatus = authenticateUser(username, password, userInfo);
                out.writeObject("respond_LOGIN");
                if (loginStatus.equals("success")) {
                    int userId = Integer.parseInt(userInfo.get(0));
                    String fullname = userInfo.get(1);
                    connectedClients.put(userId, this);
                    System.out.println("login client " + userInfo.get(1) + " successfully: ");
                    int logId = createNewLog(userId);
                    out.writeObject("SUCCESS");
                    out.writeObject(userId);
                    out.writeObject(username);
                    out.writeObject(fullname);
                    out.writeObject(logId);
                    System.out.println("all current active user: " + connectedClients.size());
                } else if (loginStatus.equals("locked")){
                    out.writeObject("LOCKED");
                } else {
                    out.writeObject("FAILURE");

                }
            } else if ("SEND_MESSAGE".equals(action)) {
                int senderId = (int) in.readObject();
                String senderFullname = (String) in.readObject();
                int chatId = (int) in.readObject();
                String message = (String) in.readObject();

                String[] data = insertMessageIntoDatabase(chatId, senderId, message);
                int messageId = Integer.parseInt(data[0]);
                String timestamp = data[1];
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                java.util.Date date = sdf.parse(timestamp);
             
                notifyChatParticipants(chatId, messageId, senderId, senderFullname, message, new Timestamp(date.getTime()));
            } else if ("GET_CHAT_LIST".equals(action)) {
                int requestId = (int) in.readObject();
                List<String[]> privateChat = new ArrayList<>();
                privateChat = getPrivateChat(requestId);
                List<String[]> groupChat = getGroupChat(requestId);
                privateChat.addAll(groupChat);
                out.writeObject("respond_GET_CHAT_LIST");
                out.writeObject(privateChat);
            } else if ("LOGOUT".equals(action)) {
                int userId = (int) in.readObject();
                int logId = (int) in.readObject();
                connectedClients.remove(userId);
                updateLogSectionEnd(logId, userId);
                System.out.println(
                        "userId: " + userId + " logged out. Current num of active user: " + connectedClients.size());
            } else if ("GET_CHAT_MESSAGES".equals(action)) {
                int chatId = Integer.parseInt((String) in.readObject());

                List<String[]> messages = getChatMessages(chatId);
                out.writeObject("respond_GET_CHAT_MESSAGES");
                out.writeObject(messages);
            } else if ("SIGNUP".equals(action)) {
                String username = (String) in.readObject();
                String fullname = (String) in.readObject();
                String gender = (String) in.readObject();
                String dob = (String) in.readObject();
                Timestamp dobTimestamp = Timestamp.valueOf(dob + " 00:00:00");
                
                String address = (String) in.readObject();
                String email = (String) in.readObject();
                String passwordHash = (String) in.readObject();

                boolean success = registerUser(username, fullname, gender, dobTimestamp, email, address, passwordHash);
                out.writeObject(success ? "SUCCESS" : "FAILURE");
            } else if ("GET_POTENTIAL_FRIENDS".equals(action)) {
                int userId = (int) in.readObject();

                List<String[]> potentialFriends = getPotentialFriends(userId);
                out.writeObject("respond_GET_POTENTIAL_FRIENDS");
                out.writeObject(potentialFriends);
            } else if ("ADD_FRIEND_REQUEST".equals(action)) {
                int senderId = (int) in.readObject();
                int receiverId = (int) in.readObject();

                insertFriendRequest(senderId, receiverId);
            } else if ("GET_USER_FRIEND_REQUEST".equals(action)) {
                int userId = (int) in.readObject();
                List<String[]> requestSent = getRequestSent(userId);
                out.writeObject("respond_GET_USER_FRIEND_REQUEST");
                out.writeObject(requestSent);
            } else if ("CANCEL_FRIEND_REQUEST".equals(action)) {
                int userId = (int) in.readObject();
                int friendId = (int) in.readObject();
                deleteFriendRequest(userId, friendId);
            } else if ("GET_FRIEND_REQUEST".equals(action)) {
                int userId = (int) in.readObject();
                List<String[]> requestList = getFriendRequest(userId);
                out.writeObject("respond_GET_FRIEND_REQUEST");
                out.writeObject(requestList);
            } else if ("ACCEPT_FRIEND_REQUEST".equals(action)) {
                int receiverId = (int) in.readObject();
                int senderId = (int) in.readObject();
                removeFriendRequest(senderId, receiverId);
                addNewFriendships(senderId, receiverId);
                List<Integer> userIdList = Arrays.asList(senderId, receiverId);
                addNewChat("", true, senderId, userIdList);
            } else if ("DECLINE_FRIEND_REQUEST".equals(action)) {
                int receiverId = (int) in.readObject();
                int senderId = (int) in.readObject();
                removeFriendRequest(senderId, receiverId);
            } else if ("BLOCK_USER".equals(action)) {
                int blockerID = (int) in.readObject();
                int chatId = (int) in.readObject();
                blockUser(blockerID, chatId);
            } else if ("REMOVE_BLOCK".equals(action)) {
                int blockerId = (int) in.readObject();
                int blockedId = (int) in.readObject();

                removeBlockList(blockerId, blockedId);
                System.out.println("remove Block complete");
            } else if ("GET_BLOCKED_LIST".equals(action)) {
                int userId = (int) in.readObject();
                List<String[]> blockedList = getBlockedList(userId);
                out.writeObject("respond_GET_BLOCKED_LIST");
                out.writeObject(blockedList);
            } else if ("SEND_EMAIL".equals(action)) {
                String email = (String) in.readObject();
                String subject = (String) in.readObject();
                String content = (String) in.readObject();
                sendEmail(email, subject, content);
            } else if ("GET_USER_PROFILE".equals(action)) {
                int userId = (int) in.readObject();

                List<String> userInfo = getUserInfo(userId);
                out.writeObject("respond_GET_USER_PROFILE");
                out.writeObject(userInfo);
            } else if ("UPDATE_USER_PROFILE".equals(action)) {
                // info order: user_id, fullname, gender, address, email, date_of_birth
                int userId = (int) in.readObject();
                String fullname = (String) in.readObject();
                String gender = (String) in.readObject();
                String address = (String) in.readObject();
                String email = (String) in.readObject();
                String dob = (String) in.readObject();

                LocalDate localDate = LocalDate.parse(dob);
                Date sqlDate = Date.valueOf(localDate);

                updateUserInfo(userId, fullname, gender, address, email, sqlDate);
            } else if ("CHECK_PASSWORD".equals(action)) {
                int userId = (int) in.readObject();
                String password = (String) in.readObject();
                out.writeObject("respond_CHECK_PASSWORD");
                if (password.equals(getPassword(userId))) {
                    out.writeObject("PASSWORD_CORRECT");
                } else {
                    out.writeObject("PASSWORD_WRONG");  
                }    
            } else if ("UPDATE_PASSWORD".equals(action)){
                int userId = (int) in.readObject();
                String oldPassword = (String) in.readObject();
                String newPassword = (String) in.readObject();
                out.writeObject("respond_UPDATE_PASSWORD");
                if (oldPassword.equals(getPassword(userId))) {
                    updatePassword(userId, newPassword);
                    out.writeObject("SUCCESS");
                } else {
                    out.writeObject("FAILURE");
                }
            } else if ("FORGOT_PASSWORD".equals(action)) {
                String email = (String) in.readObject();
                out.writeObject("respond_FORGOT_PASSWORD");
                try {
                    String reponse = updatePasswordRandomly(email) ? "SUCCESS" : "FAILURE";
                    out.writeObject(reponse);
                    System.out.println("forgot password complete"); 
                } catch (NoSuchAlgorithmException | SQLException e) {
                    e.printStackTrace();
                }
            } else if ("REPORT_USER".equals(action)) {
                int userId = (int) in.readObject();
                int chatId = (int) in.readObject();
                String reason = (String) in.readObject();
                reportUserInChat(userId, chatId, reason);
            } else if ("GET_REPORT_LIST".equals(action)){
                int userId = (int) in.readObject();

                List<String[]> reportedList = getReportedList(userId);
                out.writeObject("respond_GET_REPORT_LIST");
                // order: reportedUserId, reportedUserFullname, reportedUserStatus, reportedTime
                out.writeObject(reportedList);
            } else if ("GET_FRIENDS".equals(action)){
                int userId = (int) in.readObject();
                List <String[]> friendList = getFriends(userId);

                out.writeObject("respond_GET_FRIENDS");
                out.writeObject(friendList);
            } else if ("CREATE_CHAT_GROUP".equals(action)){
                String groupName = (String) in.readObject();
                int adminId = (int) in.readObject();
                List <Integer> userIdList = (List <Integer>) in.readObject();
                addNewChat(groupName, false, adminId, userIdList);
            } else if ("CHANGE_CHAT_NAME".equals(action)){
                int chatId = (int) in.readObject();
                String newName = (String) in.readObject();

                changeChatName(chatId, newName);
            } else if ("GET_POSSIBLE_FRIEND_FOR_ADDING_TO_CHAT".equals(action)){
                int userId = (int) in.readObject();
                int chatId = (int) in.readObject();

                List <String[]> possibleMemberList = getPossibleFriendForAddingMember(userId, chatId);
                out.writeObject("respond_GET_POSSIBLE_FRIEND_FOR_ADDING_TO_CHAT");
                out.writeObject(possibleMemberList);
            } else if ("ADD_CHAT_MEMBER".equals(action)){
                int chatId = (int) in.readObject();
                List <Integer> userIdList = (List <Integer>) in.readObject();

                addMemberToChat(chatId, userIdList);
            } else if ("GET_CHAT_MEMBERS".equals(action)){
                int userId = (int) in.readObject();
                int chatId = (int) in.readObject();

                List <String[]> memberList = getMemberForUser(userId, chatId);
                out.writeObject("respond_GET_CHAT_MEMBERS");
                out.writeObject(memberList);
            } else if ("REMOVE_CHAT_MEMBER".equals(action)){
                int chatId = (int) in.readObject();
                List <Integer> userIdList = (List <Integer>) in.readObject();
                
                removeMemberFromChat(chatId, userIdList);
            } else if ("ASSIGN_ADMIN".equals(action)){
                int chatId = (int) in.readObject();
                int newAdminId = (int) in.readObject();

                assignNewChatAdmin(chatId, newAdminId);
            } else if ("SEARCH_MESSAGE".equals(action)){
                String searchContent = (String) in.readObject();
                int userId = (int) in.readObject();
                Map<Integer, List<String[]>> searchResults = searchMessages(userId, searchContent);
                out.writeObject("respond_SEARCH_MESSAGE");
                out.writeObject(searchResults);

            } else if ("DELETE_MESSAGE".equals(action)){
                int messageId = (int) in.readObject();
                int chatId = (int) in.readObject();
                int userId = (int) in.readObject();
                deleteMessage(messageId, chatId, userId);
                
                out.writeObject("respond_DELETE_MESSAGE");
                out.writeObject(chatId);

                getUserFriend(chatId);
            } else if ("DELETE_ALL_CHAT_MESSAGE".equals(action)){
                int chatId = (int) in.readObject();

                deleteAllChatMessage(chatId);
            } else if ("UNFRIEND".equals(action)){
                int userId = (int) in.readObject();
                int chatID = (int) in.readObject();

                unfriend(userId, chatID);
            }
            else {
                System.out.println("Unknown action: " + action);
            }
        }

        private void unfriend(int userId, int chatId) throws SQLException{
            Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            PreparedStatement stmt = conn.prepareStatement("""
                SELECT * 
                FROM chat_members
                WHERE chat_id = ? AND user_id != ?
            """
            );
            stmt.setInt(1, chatId);
            stmt.setInt(2, userId);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            int friendId = rs.getInt("user_id");
            stmt = conn.prepareStatement("""
                DELETE FROM friendships
                WHERE (user1_id = ? AND user2_id = ?) OR (user1_id = ? AND user2_id = ?)
            """
            );
            stmt.setInt(1, userId);
            stmt.setInt(2, friendId);
            stmt.setInt(3, friendId);
            stmt.setInt(4, userId);

            stmt.executeUpdate();
            System.out.println("unfriend complete");
            // remove chats and chat_members related info
            deleteAllChatMessage(chatId);
            
            stmt = conn.prepareStatement("""
                DELETE FROM chat_members
                WHERE chat_id = ?
            """);
            stmt.setInt(1, chatId);
            stmt.executeUpdate();

            stmt = conn.prepareStatement("""
                DELETE FROM chats
                WHERE chat_id = ?
            """);
            stmt.setInt(1, chatId);
            stmt.executeUpdate();
        }

        private void deleteAllChatMessage(int chatId) throws SQLException {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            PreparedStatement stmt = conn.prepareStatement("""
                DELETE FROM messages WHERE chat_id = ?
            """
            );
            stmt.setInt(1, chatId);

            stmt.executeUpdate();
        }

        private void deleteMessage(int messageId, int chatId, int userId) throws SQLException{
            Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            PreparedStatement stmt = conn.prepareStatement("""
                DELETE FROM messages WHERE message_id = ?
            """
            );

            stmt.setInt(1, messageId);
            
            stmt.executeUpdate();

            // notify related user to update their chat
            List <Integer> friendIdList = getUserFriend(userId);
            for (int friendId : friendIdList){
                // if there is a friend that is online
                if (connectedClients.containsKey(friendId)) {
                    ClientHandler recipient = connectedClients.get(friendId);
                    try { // sending signal for them to update
                        recipient.out.writeObject("FRIEND_DELETE_MESSAGE");
                        recipient.out.writeObject(friendId);
                        recipient.out.writeObject(chatId);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        private Map<Integer, List<String[]>> searchMessages(int userId, String searchContent) throws SQLException {
            Map<Integer, List<String[]>> chatData = new HashMap<>();

            Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            
            String query = """
                SELECT DISTINCT c.chat_id, COALESCE(c.group_name, u.full_name) as chat_name, m.message_id, m.sender_id, sender.full_name as sender_name, m.content, m.created_at 
                FROM chats c 
                JOIN chat_members cm ON cm.chat_id  = c.chat_id 
                JOIN users u ON (u.user_id = cm.user_id) 
                JOIN messages m ON c.chat_id = m.chat_id
                JOIN users sender ON sender.user_id = m.sender_id 
                WHERE  c.chat_id  IN (
                    SELECT chat_id
                    FROM chat_members WHERE user_id = ?
                ) AND m.content LIKE ? AND cm.user_id != ?
                ORDER BY c.chat_id, m.created_at DESC
            """;
            
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, userId);
            stmt.setString(2, "%" + searchContent + "%");
            stmt.setInt(3, userId);
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                int chatId = rs.getInt("c.chat_id");
                String chatName = rs.getString("chat_name");
                String messageId = rs.getString("m.message_id");
                String senderId = rs.getString("m.sender_id");
                String senderName = rs.getString("sender_name");
                String content = rs.getString("m.content");
                String createdAt = rs.getString("m.created_at");
            
                // Combine chatName and message details into one String[]
                String[] message = {chatName, messageId, senderId, senderName, content, createdAt};
            
                // Add the message to the appropriate chatId in the map
                chatData.computeIfAbsent(chatId, k -> new ArrayList<>()).add(message);
            }
            return chatData;
        }

        private int getUserIdByEmail(String email) throws SQLException {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            PreparedStatement stmt = conn.prepareStatement("SELECT user_id FROM users WHERE email = ?");
            stmt.setString(1, email);

            ResultSet rs = stmt.executeQuery();
            rs.next();
            userId = rs.getInt("user_id");
            
            return userId;
        }
        
        private boolean checkIfEmailExist(String email) throws SQLException {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            PreparedStatement stmt = conn.prepareStatement("SELECT user_id FROM users WHERE email = ?");
            stmt.setString(1, email);

            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
        
        private boolean updatePasswordRandomly(String email) throws SQLException, NoSuchAlgorithmException {
            if (!checkIfEmailExist(email)) {
                System.out.println("User not found");
                return false;
            }
            
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[16];
            random.nextBytes(salt);

            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt);

            String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789~`!@#$%^&*()-_=+[{]}\\|;:\'\",<.>/?";
            String newPassword = RandomStringUtils.random(15, characters);
            System.out.println(newPassword);

            byte[] hashedPassword = md.digest(newPassword.getBytes(StandardCharsets.UTF_8));

            int userId = getUserIdByEmail(email);
            if (userId == 0) {
                System.out.println("User not found");
                return false;
            }
            updatePassword(userId, hashedPassword.toString());
            sendEmail(email, "New Password", "Your new password is: " + newPassword);
            System.out.println("update password complete");
            
            return true;
        }
        
        private void assignNewChatAdmin(int chatId, int newAdminID) throws SQLException {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            PreparedStatement stmt = conn.prepareStatement("""
                UPDATE chats
                SET admin_id = ?
                WHERE chat_id = ?
            """);

            stmt.setInt(1, newAdminID);
            stmt.setInt(2, chatId);

            stmt.executeUpdate();

            System.out.println("change admin complete");

            stmt = conn.prepareStatement("""
                SELECT * FROM chat_members WHERE chat_id = ?
            """);
            stmt.setInt(1, chatId);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()){
                int userId = rs.getInt("user_id");
                if (connectedClients.containsKey(userId)) {
                    ClientHandler recipient = connectedClients.get(userId);
                    try {
                        recipient.out.writeObject("CHANGE_ADMIN");
                        recipient.out.writeObject(chatId);
                        recipient.out.writeObject(newAdminID);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        private void removeMemberFromChat(int chatId, List<Integer> userIdList) throws SQLException {
            List <String[]> members = new ArrayList();
            Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            PreparedStatement stmt;
            for (int userId : userIdList) {
                stmt = conn.prepareStatement("""
                            DELETE FROM chat_members WHERE chat_id = ? AND user_id = ?
                        """);
                stmt.setInt(1, chatId);
                stmt.setInt(2, userId);

                stmt.executeUpdate();

                if (connectedClients.containsKey(userId)) {
                    ClientHandler recipient = connectedClients.get(userId);
                    try {
                        recipient.out.writeObject("REMOVED_MEMBER");
                        // recipient.out.writeObject(chatId);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            System.out.println("remove member complete");
        }

        private List <String[]> getMemberForUser(int userId, int chatId) throws SQLException {
            List <String[]> members = new ArrayList();
            Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            PreparedStatement stmt = conn.prepareStatement("""
                SELECT u.user_id, u.full_name
                FROM chat_members cm JOIN users u ON cm.user_id = u.user_id 
                WHERE cm.chat_id = ? and u.user_id != ?
            """);
            stmt.setInt(1, chatId);
            stmt.setInt(2, userId);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String memberId = rs.getString("u.user_id");
                String memberFullname = rs.getString("u.full_name");

                members.add(new String[]{memberId, memberFullname});
            }
            return members;
        }

        private void addMemberToChat(int chatId, List <Integer> userIdList) throws SQLException {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            PreparedStatement stmt;

            for (int userId : userIdList) {
                System.out.println("about to add userid: " + userId);
                stmt = conn.prepareStatement("""
                            INSERT INTO chat_members (chat_id, user_id) VALUES (?, ?)
                        """);
                stmt.setInt(1, chatId);
                stmt.setInt(2, userId);

                stmt.executeUpdate();

                if (connectedClients.containsKey(userId)) {
                    ClientHandler recipient = connectedClients.get(userId);
                    try {
                        recipient.out.writeObject("ADDED_MEMBER");
                        // recipient.out.writeObject(chatId);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        private List <String[]> getPossibleFriendForAddingMember(int userId, int chatId) throws SQLException{
            List <String[]> possibleFriend = new ArrayList();
            Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            PreparedStatement stmt = conn.prepareStatement("""
                SELECT friend.friendId, u.full_name 
                FROM 
                (
                    SELECT user1_id as friendId
                    FROM friendships
                    WHERE user2_id = ?
                    UNION
                    SELECT user2_id 
                    FROM friendships
                    WHERE user1_id = ?
                ) as friend JOIN users u ON friend.friendId = u.user_id
                WHERE friendId NOT IN 
                (
                    SELECT user_id
                    FROM chat_members 
                    WHERE chat_id = ?
                )
            """);
            stmt.setInt(1, userId);
            stmt.setInt(2, userId);
            stmt.setInt(3, chatId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()){
                String friendId = rs.getString("friend.friendId");
                String frieldName = rs.getString("u.full_name");

                possibleFriend.add(new String[]{friendId, frieldName});
            }
            System.out.println("friendsize: " + possibleFriend.size());
            return possibleFriend;
        }

        private void changeChatName(int chatId, String chatName) throws SQLException {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            PreparedStatement stmt = conn.prepareStatement("""
                UPDATE chats 
                SET group_name = ?
                WHERE chat_id = ?
            """);

            stmt.setString(1, chatName);
            stmt.setInt(2, chatId);

            stmt.executeUpdate();

            // notify relatedUser
            stmt = conn.prepareStatement("""
                SELECT user_id FROM chat_members
                WHERE chat_id = ?
            """);
            stmt.setInt(1, chatId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int participantId = rs.getInt("user_id");
                if (connectedClients.containsKey(participantId)) {
                    ClientHandler recipient = connectedClients.get(participantId);
                    try {
                        recipient.out.writeObject("CHAT_NAME_CHANGED");
                        recipient.out.writeObject(chatId);
                        recipient.out.writeObject(chatName);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        }

        private List<String[]> getGroupChat(int userId) throws SQLException {
            List<String[]> chatList = new ArrayList<>(); 
            Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            PreparedStatement stmt = conn.prepareStatement("""
                       SELECT chat.chat_id, 
                            chat.group_name, 
                            chat.admin_id, 
                            COALESCE(m.content, NULL) as last_message, 
                            COALESCE(m.sender_id, NULL) as sender_id, 
                            COALESCE(u.full_name, NULL) as sender_fullname
                    FROM chats chat
                    JOIN chat_members cm ON cm.chat_id = chat.chat_id
                    LEFT JOIN messages m ON m.chat_id = chat.chat_id 
                                        AND m.created_at = (
                                            SELECT MAX(created_at)
                                            FROM messages
                                            WHERE chat_id = chat.chat_id
                                        )
                    LEFT JOIN users u ON u.user_id = m.sender_id
                    WHERE chat.chat_type = 'group' 
                    AND cm.user_id = ?
            """);
            stmt.setInt(1, userId);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()){
                String chatId = rs.getString("chat.chat_id");
                String chatName = rs.getString("chat.group_name");
                String adminId = rs.getString("chat.admin_id");
                String lastMessage = rs.getString("last_message");
                String senderId = rs.getString("sender_id");
                String senderFullname = rs.getString("sender_fullname");
                chatList.add(new String[]{chatId, chatName, "", "group", adminId, lastMessage, senderId, senderFullname});
            }
            return chatList;
        }

        private List<String[]> getFriends(int userId) throws SQLException {
            List<String[]> friendList = new ArrayList<>(); 
            Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            PreparedStatement stmt = conn.prepareStatement("""
                select * from 
                (
                    SELECT user2_id as id FROM friendships 
                    WHERE user1_id = ?
                    UNION 
                    SELECT user1_id FROM friendships
                    WHERE user2_id = ?
                ) as friends JOIN users u ON (friends.id = u.user_id)
            """);
            stmt.setInt(1, userId);
            stmt.setInt(2, userId);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()){
                String friendId = rs.getString("id");
                String frieldName = rs.getString("full_name");

                friendList.add(new String[]{friendId, frieldName});
            }
            System.out.println("friendsize: " + friendList.size());
            return friendList;
        }

        private void reportUserInChat(int reporterId, int chatId, String reason) throws SQLException{
            Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            PreparedStatement stmt = conn.prepareStatement("""
                SELECT user_id
                FROM chat_members
                WHERE chat_id = ? AND user_id != ? 
            """);

            stmt.setInt(1, chatId);
            stmt.setInt(2, reporterId);

            ResultSet rs = stmt.executeQuery();
            rs.next();
            int reportedUserId = rs.getInt("user_id");

            stmt = conn.prepareStatement("INSERT INTO Report (reporter_id, reported_id, reason) VALUES (?, ?, ?)");
            stmt.setInt(1, reporterId);
            stmt.setInt(2, reportedUserId);
            stmt.setString(3, reason);

            stmt.executeUpdate();

            System.out.println("User reported!");
        }

        private void updatePassword(int userId, String newPassword) throws SQLException {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            PreparedStatement stmt = conn.prepareStatement("""
                        UPDATE users
                        SET password = ?
                        WHERE user_id = ?
                    """);
            stmt.setString(1, newPassword);
            stmt.setInt(2, userId);

            stmt.executeUpdate();
        }

        private String getPassword(int userId) throws SQLException {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            PreparedStatement stmt = conn.prepareStatement("""
                        SELECT password FROM users WHERE user_id = ?
                    """);
            stmt.setInt(1, userId);

            ResultSet rs = stmt.executeQuery();
            rs.next();
            return rs.getString("password");
        }

        private void updateUserInfo(int userId, String fullname, String gender, String address, String email, Date dob)
                throws SQLException {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            PreparedStatement stmt = conn.prepareStatement("""
                        UPDATE users
                        SET full_name = ?, gender = ?, address = ?, email = ?, date_of_birth = ?
                        WHERE user_id = ?
                    """);

            stmt.setString(1, fullname);
            stmt.setString(2, gender);
            stmt.setString(3, address);
            stmt.setString(4, email);
            stmt.setDate(5, dob);
            stmt.setInt(6, userId);

            stmt.executeUpdate();
            System.out.println("update info complete");
        }

        private void updateLogSectionEnd(int logID, int userId) throws SQLException, IOException {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            PreparedStatement stmt = conn.prepareStatement("""
                        UPDATE log_history
                        SET section_end = NOW()
                        WHERE log_id = ?
                    """);
            stmt.setInt(1, logID);

            stmt.executeUpdate();
            // also update user status to 'offline'
            stmt = conn.prepareStatement("UPDATE users SET status = \"offline\" WHERE user_id = ?");
            stmt.setInt(1, userId);
            
            stmt.executeUpdate();
            // then notify their online friend
            List <Integer> friendIdList = getUserFriend(userId);
            for (int friendId : friendIdList){
                if (connectedClients.containsKey(friendId)){
                    ClientHandler recipient = connectedClients.get(friendId);
                    recipient.out.writeObject("FRIEND_OFFLINE");
                    recipient.out.writeObject(friendId);
                }
            }
        }

        private int createNewLog(int userId) throws SQLException {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            PreparedStatement stmt = conn.prepareStatement("""
                        INSERT INTO log_history (user_id, section_start) VALUES (?, NOW())
                    """);
            stmt.setInt(1, userId);
            stmt.executeUpdate();

            stmt = conn.prepareStatement("SELECT LAST_INSERT_ID() as newLogId");
            ResultSet rs = stmt.executeQuery();
            rs.next();
            int logId = rs.getInt("newLogId");

            return logId;
        }

        private List<String> getUserInfo(int userId) throws SQLException {
            List<String> userInfo = new ArrayList<>();
            Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            PreparedStatement stmt = conn.prepareStatement("""
                        SELECT * FROM users WHERE user_id = ?
                    """);
            stmt.setInt(1, userId);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String fullname = rs.getString("full_name");
                String email = rs.getString("email");
                String gender = rs.getString("gender");
                Date dob = rs.getDate("date_of_birth");
                String address = rs.getString("address");

                String dobFormatted = dob != null ? new SimpleDateFormat("yyyy-MM-dd").format(dob) : "N/A";

                Collections.addAll(userInfo, fullname, email, gender, dobFormatted, address);
            } else {
                System.out.println("error fetch user data");
            }

            return userInfo;
        }

        private void sendEmail(String email, String subject, String content) {

        }

        private List<String[]> getReportedList(int userId) throws SQLException {
            List <String[]> reportedList = new ArrayList<>();
            Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            PreparedStatement stmt = conn.prepareStatement("""
                SELECT u.*, r.created_at, r.status 
                FROM Report r JOIN users u ON r.reported_id = u.user_id
                WHERE r.reporter_id = ?
            """);
            stmt.setInt(1, userId);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()){
                reportedList.add(new String[] { rs.getString("user_id"), 
                                            rs.getString("full_name"),
                                            rs.getString("username"),
                                            rs.getString("address"),
                                            rs.getString("gender"),
                                            rs.getString("date_of_birth"),
                                            rs.getString("r.created_at"),
                                            rs.getString("r.status")
                                        });
            }
            
            return reportedList;
        }

        private List<String[]> getBlockedList(int userId) throws SQLException {
            List<String[]> blockedList = new ArrayList<>();
            Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            PreparedStatement stmt = conn.prepareStatement("""
                        SELECT u.*, b.created_at
                        FROM Blocked_List b JOIN users u ON b.blocked_id = u.user_id
                        WHERE b.blocker_id = ?
                    """);

            stmt.setInt(1, userId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                blockedList.add(new String[] { rs.getString("user_id"), 
                                            rs.getString("full_name"),
                                            rs.getString("username"),
                                            rs.getString("address"),
                                            rs.getString("gender"),
                                            rs.getString("date_of_birth"),
                                            rs.getString("b.created_at")
                                        });
            }
            return blockedList;
        }

        private boolean removeBlockList(int blockerId, int blockedId) throws SQLException {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            PreparedStatement stmt = conn.prepareStatement("""
                        DELETE FROM Blocked_List WHERE blocker_id = ? AND blocked_id = ?
                    """);

            stmt.setInt(1, blockerId);
            stmt.setInt(2, blockedId);

            return stmt.executeUpdate() == 1 ? true : false;
        }

        private void blockUser(int blockerId, int chatId) throws SQLException {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            // Remove friendships first
            PreparedStatement stmt = conn.prepareStatement("""
                        SELECT user_id FROM chat_members WHERE (chat_id = ? AND user_id != ?)
                    """);

            stmt.setInt(1, chatId);
            stmt.setInt(2, blockerId);

            ResultSet rs = stmt.executeQuery();
            int blockedId = 0;
            if (rs.next()) {
                blockedId = rs.getInt("user_id");
            } else {
                System.out.println("Error blocking user");
            }

            // Add info into Blocked_List table
            stmt = conn.prepareStatement("""
                        INSERT INTO Blocked_List (blocker_id, blocked_id) VALUES (?, ?)
                    """);
            stmt.setInt(1, blockerId);
            stmt.setInt(2, blockedId);
            stmt.executeUpdate();
            removeFriendShip(blockerId, blockedId);

            // remove the chat
            removeChat(chatId);
        }

        private void removeChat(int chatId) throws SQLException {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            // remove chat members first
            PreparedStatement stmt = conn.prepareStatement("""
                        DELETE FROM chat_members WHERE chat_id = ?
                    """);

            stmt.setInt(1, chatId);

            stmt.executeUpdate();
            System.out.println("remove chat mem complete");
            // then remove the chat itself
            stmt = conn.prepareStatement("""
                        DELETE FROM chats WHERE chat_id = ?
                    """);

            stmt.setInt(1, chatId);

            stmt.executeUpdate();
            System.out.println("remove the chat complete");
        }

        private void removeFriendShip(int user1_id, int user2_id) throws SQLException {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            PreparedStatement stmt = conn.prepareStatement("""
                        DELETE FROM friendships
                        WHERE (user1_id = ? AND user2_id = ?)
                        OR (user1_id = ? AND user2_id = ?)
                    """);

            stmt.setInt(1, user1_id);
            stmt.setInt(2, user2_id);
            stmt.setInt(3, user2_id);
            stmt.setInt(4, user1_id);

            int row = stmt.executeUpdate();
            System.out.println(row + " friendship deleted");
        }

        private void removeFriendRequest(int senderId, int receiverId) throws SQLException {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            PreparedStatement stmt = conn.prepareStatement("""
                        DELETE FROM friend_request WHERE user_id = ? and friend_id = ?
                    """);
            stmt.setInt(1, senderId);
            stmt.setInt(2, receiverId);

            stmt.executeUpdate();
        }

        private void addNewFriendships(int senderId, int receiverId) {
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
                    PreparedStatement stmt = conn.prepareStatement("""
                                INSERT INTO friendships (user1_id, user2_id) VALUES (?, ?)
                            """)) {
                stmt.setInt(1, senderId);
                stmt.setInt(2, receiverId);
                stmt.executeUpdate();
                System.out.println("add friendship complete");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        private void addNewChat(String groupName, boolean isPrivate, int adminId, List<Integer> userIdList)
                throws SQLException {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            PreparedStatement stmt = conn.prepareStatement("""
                        INSERT INTO chats (group_name, chat_type, admin_id) VALUES (?, ?, ?)
                    """);
            if (!groupName.isEmpty())
                stmt.setString(1, groupName);
            else
                stmt.setNull(1, Types.NULL);

            if (isPrivate)
                stmt.setString(2, "private");
            else
                stmt.setString(2, "group");

            stmt.setInt(3, adminId);

            stmt.executeUpdate();

            stmt = conn.prepareStatement("SELECT LAST_INSERT_ID() as newChatId");
            ResultSet rs = null;
            rs = stmt.executeQuery();
            int chatId;
            if (rs.next())
                chatId = rs.getInt("newChatId");
            else {
                System.out.println("Error cant found chat id");
                return;
            }

            System.out.println("new inserted chat id: " + chatId);
            for (int userId : userIdList) {
                stmt = conn.prepareStatement("""
                            INSERT INTO chat_members (chat_id, user_id) VALUES (?, ?)
                        """);
                stmt.setInt(1, chatId);
                stmt.setInt(2, userId);
                
                stmt.executeUpdate();
                if (connectedClients.containsKey(userId)) {
                    ClientHandler recipient = connectedClients.get(userId);
                    try {
                        recipient.out.writeObject("NEW_CHAT");
                        // recipient.out.writeObject(chatId);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                
            }
        }

        // Get all of the users that request to be friend with this user(userId)
        private List<String[]> getFriendRequest(int userId) {
            List<String[]> messages = new ArrayList<>();
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
                    PreparedStatement stmt = conn.prepareStatement("""
                                SELECT u.*, fr.created_at
                                FROM friend_request fr
                                JOIN users u ON (u.user_id = fr.user_id)
                                WHERE fr.friend_id = ?
                            """)) {
                stmt.setInt(1, userId);

                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    messages.add(new String[] { rs.getString("user_id"), 
                                            rs.getString("full_name"),
                                            rs.getString("username"),
                                            rs.getString("address"),
                                            rs.getString("gender"),
                                            rs.getString("date_of_birth"),
                                            rs.getString("fr.created_at")
                                        });
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return messages;
        }

        private void deleteFriendRequest(int senderId, int receiverId) {
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
                    PreparedStatement stmt = conn.prepareStatement("""
                                DELETE FROM friend_request
                                WHERE user_id = ? AND friend_id = ?
                            """)) {
                stmt.setInt(1, senderId);
                stmt.setInt(2, receiverId);

                stmt.executeUpdate();
                System.out.println("remove friend request complete");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        private List<String[]> getRequestSent(int userId) {
            List<String[]> messages = new ArrayList<>();
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
                    PreparedStatement stmt = conn.prepareStatement("""
                            SELECT u.*, fr.created_at 
                            FROM friend_request fr
                            JOIN users u ON (fr.friend_id = u.user_id)
                            WHERE fr.user_id = ?
                            """)) {
                stmt.setInt(1, userId);
                
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    messages.add(new String[] { rs.getString("user_id"), 
                                            rs.getString("full_name"),
                                            rs.getString("username"),
                                            rs.getString("address"),
                                            rs.getString("gender"),
                                            rs.getString("date_of_birth"),
                                            rs.getString("fr.created_at")
                                        });
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return messages;
        }

        private void insertFriendRequest(int senderId, int receiverId) {
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
                    PreparedStatement stmt = conn.prepareStatement("""
                            INSERT INTO friend_request (user_id, friend_id) VALUES (?, ?);
                            """)) {
                stmt.setInt(1, senderId);
                stmt.setInt(2, receiverId);

                stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        private static List<String[]> getPotentialFriends(int userId) throws SQLException {
            List<String[]> messages = new ArrayList<>();
            String query = """
                    SELECT *
                    FROM users
                    WHERE user_id NOT IN (
                        SELECT user1_id FROM friendships WHERE user2_id = ?
                        UNION
                        SELECT user2_id FROM friendships WHERE user1_id = ?
                        UNION
                        SELECT friend_id FROM friend_request WHERE user_id = ?
                        UNION
                        SELECT user_id FROM friend_request WHERE friend_id = ?
                        UNION
                        SELECT blocked_id FROM Blocked_List WHERE blocker_id = ?
                    )
                    AND user_id != ?;
                    """;
            Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, userId);
            stmt.setInt(2, userId);
            stmt.setInt(3, userId);
            stmt.setInt(4, userId);
            stmt.setInt(5, userId);
            stmt.setInt(6, userId);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                messages.add(new String[] { rs.getString("user_id"), 
                                            rs.getString("full_name"),
                                            rs.getString("username"),
                                            rs.getString("address"),
                                            rs.getString("gender"),
                                            rs.getString("date_of_birth")
                                        });
            }
            return messages;
        }

        private static String[] insertMessageIntoDatabase(int chatId, int senderId, String message) throws SQLException {
            String query = "INSERT INTO messages (chat_id, sender_id, content, created_at) VALUES (?, ?, ?, ?)";
            Timestamp timestamp = Timestamp.from(Instant.now());
            Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, chatId);
            stmt.setInt(2, senderId);
            stmt.setString(3, message);
            stmt.setTimestamp(4, timestamp);
            stmt.executeUpdate();
            System.out.println("Message stored");
            stmt = conn.prepareStatement(
                """
                SELECT m.*
                FROM messages m
                WHERE m.chat_id = ?
                AND created_at = (SELECT MAX(created_at) FROM messages m2 WHERE m2.chat_id = m.chat_id)
                """
            );
            stmt.setInt(1, chatId);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            String messageId = rs.getString("message_id");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
            return new String[]{messageId, sdf.format(timestamp)};
        }

        private void notifyChatParticipants(int chatId, int messageId, int senderId, String senderFullname, String message, Timestamp timeSent) {
            String query = """
                SELECT u.user_id, u.full_name FROM chat_members cm
                JOIN users u ON u.user_id = cm.user_id 
                WHERE chat_id = ?
            """;
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
                    PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, chatId);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    int participantId = rs.getInt("u.user_id");
                    // if the current chat member(including the user) is online
                    if (connectedClients.containsKey(participantId)) {
                        ClientHandler recipient = connectedClients.get(participantId);
                        try {
                            recipient.out.writeObject("NEW_MESSAGE");
                            recipient.out.writeObject(chatId);
                            recipient.out.writeObject(messageId);
                            recipient.out.writeObject(message);
                            recipient.out.writeObject(senderId);
                            recipient.out.writeObject(senderFullname);
                            recipient.out.writeObject(timeSent);
                            System.out.println("send signal to chatid " + chatId + "from userid " +
                            senderId + " to " + participantId);
                        } catch (Exception e) {
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
                            SELECT m.message_id, m.sender_id, u.full_name, m.content, m.created_at
                            FROM messages m JOIN users u on sender_id = user_id
                            WHERE chat_id = ?
                            ORDER BY created_at
                    """)) {
                stmt.setInt(1, chatId);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    String messageId = rs.getString("m.message_id");
                    String sender_id = rs.getString("m.sender_id");
                    String sender_fullname = rs.getString("u.full_name");
                    String content = rs.getString("m.content");
                    String created_at = rs.getString("m.created_at");
                    messages.add(new String[] {messageId, sender_id, sender_fullname, content, created_at });
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return messages;
        }

        private String authenticateUser(String username, String password, List<String> userInfo) throws SQLException {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE BINARY username = ? AND password = ?");
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String userId = rs.getString("user_id");
                String fullname = rs.getString("full_name");
                String adminAction = rs.getString("admin_action");
                if (!"locked".equals(adminAction)){
                    userInfo.add(userId);
                    userInfo.add(fullname);

                    // update user status to online
                    stmt = conn.prepareStatement("UPDATE users SET status = \"online\" WHERE user_id = ?");
                    stmt.setInt(1, Integer.parseInt(userId));
                    stmt.executeUpdate();

                    // fetch friend list to live update
                    List <Integer> friendIdList = getUserFriend(Integer.parseInt(userId));
                    for (int friendId : friendIdList){
                        // if there is a friend that is online
                        if (connectedClients.containsKey(friendId)) {
                            ClientHandler recipient = connectedClients.get(friendId);
                            try { // sending signal for them to update
                                recipient.out.writeObject("FRIEND_ONLINE");
                                recipient.out.writeObject(friendId);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    return "success";
                } else {
                    return "locked"; 
                }
            } else
                return "fail";
            }

        private List <Integer> getUserFriend(int userId) throws SQLException {
            List <Integer> friendIdList = new ArrayList<>();
            Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            PreparedStatement stmt = conn.prepareStatement("""
                    SELECT user1_id as friend_id FROM friendships WHERE user2_id = ?
                    UNION
                    SELECT user2_id FROM friendships WHERE user1_id = ?
            """);

            stmt.setInt(1, userId);
            stmt.setInt(2, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int friendId = rs.getInt("friend_id");
                friendIdList.add(friendId);
            }

            return friendIdList;
        }

        private static boolean registerUser(String username, String fullname, String gender, Timestamp dob, String email, String address,
                String passwordHash) {
            System.out.println("Registering user: " + username);
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
                    PreparedStatement stmt = conn.prepareStatement(
                            "INSERT INTO users (username, email, address, password, full_name, status, date_of_birth, gender) " +
                                    "VALUES (?, ?, ?, ?, ?, 'offline', ?, ?)")) {

                stmt.setString(1, username);
                stmt.setString(2, email);
                stmt.setString(3, address);
                stmt.setString(4, passwordHash);
                stmt.setString(5, fullname);
                stmt.setTimestamp(6, dob);
                stmt.setString(7, gender);   

                int rowsInserted = stmt.executeUpdate();
                return rowsInserted > 0;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        }

        private static List<String[]> getPrivateChat(int userId) {
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
                    PreparedStatement stmt = conn.prepareStatement("""
                SELECT c.chat_id, 
                    u.full_name, 
                    u.status, 
                    c.admin_id, 
                    COALESCE(m.content, NULL) as last_message, 
                    COALESCE(sender.user_id, NULL) as sender_id, 
                    COALESCE(sender.full_name, NULL) as sender_fullname
                FROM chats c
                JOIN chat_members cm ON c.chat_id = cm.chat_id
                JOIN users u ON u.user_id = cm.user_id
                LEFT JOIN messages m ON m.chat_id = c.chat_id 
                                    AND m.created_at = (
                                        SELECT MAX(created_at)
                                        FROM messages
                                        WHERE chat_id = c.chat_id
                                    )
                LEFT JOIN users sender ON sender.user_id = m.sender_id
                WHERE c.chat_id IN (
                    SELECT c.chat_id
                    FROM chats c
                    JOIN chat_members cm ON c.chat_id = cm.chat_id
                    WHERE cm.user_id = ? AND c.chat_type = 'private'
                ) 
                AND cm.user_id != ?
                    """)) {

                stmt.setInt(1, userId);
                stmt.setInt(2, userId);

                List<String[]> chatList = new ArrayList<>();
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    chatList.add(new String[] { 
                                rs.getString("c.chat_id"), 
                                rs.getString("u.full_name"),
                                rs.getString("u.status"), 
                                "private", 
                                rs.getString("c.admin_id"), 
                                rs.getString("last_message"),
                                rs.getString("sender_id"),
                                rs.getString("sender_fullname")
                            });
                }
                return chatList;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
