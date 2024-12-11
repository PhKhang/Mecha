package com.example.mechaclient.models;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class UserSession {
    
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;
    private static UserSession instance;

    public static Socket socket;
    public static ObjectInputStream in;
    public static ObjectOutputStream out;

    private String username;
    private int userId;

    private Thread listenerThread;
    private boolean isListening;
    private final List<ServerMessageListener> listeners = new ArrayList<>();
    
    private UserSession() {}

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public void connectToServer() throws IOException {
        socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
        out.flush();
        System.out.println("Connected to server successfully: " + socket);
    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int id) {
        this.userId = id;
    }

    public void startListening() {
        if (listenerThread == null || !listenerThread.isAlive()) {
            System.out.println("new thread created");
            isListening = true;
            listenerThread = new Thread(() -> {
                try {
                    while (isListening) {
                        System.out.println("UserSession getting more respone...");
                        String response = (String) in.readObject();
                        notifyListeners(response);
                        System.out.println("notify complete, listening more...");
                    }
                } catch (IOException | ClassNotFoundException e) {
                    System.err.println("Error listening to server: " + e.getMessage());
                    e.printStackTrace();
                }
            });
            listenerThread.start();
        }
    }

    public void stopListening() {
        isListening = false;
        if (listenerThread != null) {
            listenerThread.interrupt();
        }
    }

    public void addMessageListener(ServerMessageListener listener) {
        listeners.add(listener);

        System.out.println("new listener added, current listener size: " + listeners.size());
    }

    public void removeMessageListener(ServerMessageListener listener) {
        listeners.remove(listener);
        System.out.println("a listener removed, current listener size: " + listeners.size());
    }

    private void notifyListeners(String message) {
        for (ServerMessageListener listener : listeners) {
            listener.onMessageReceived(message);
        }
    }

    public interface ServerMessageListener {
        void onMessageReceived(String message);
    }
}