package com.example.mechaclient.models;

public class ChatBox {
    public String name;
    public ChatType type;
    public String status;
    public String lastMessage;
    
    public int chatId;
    public int adminId;

    public enum ChatType {
        PRIVATE, GROUP
    }

    public ChatBox(String name, ChatType type, String status, String lastMessage, int chatId, int adminId) {
        this.name = name;
        this.type = type;
        this.status = status;
        this.lastMessage = lastMessage;
        this.chatId = chatId;
        this.adminId = adminId;
    }
}

