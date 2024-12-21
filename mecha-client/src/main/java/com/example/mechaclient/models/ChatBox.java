package com.example.mechaclient.models;

public class ChatBox {
    public int chatId;
    public int adminId;
    public String name;
    public ChatType type;
    public String status;
    public String lastMessage;
    public int lastSenderId;
    public String lastSenderFullname;


    public enum ChatType {
        PRIVATE, GROUP
    }

    public ChatBox(String name, ChatType type, String status, String lastMessage, int lastSenderId, String lastSenderFullname,int chatId, int adminId) {
        this.name = name;
        this.type = type;
        this.status = status;
        this.lastMessage = lastMessage;
        this.lastSenderId = lastSenderId;
        this.lastSenderFullname = lastSenderFullname;
        this.chatId = chatId;
        this.adminId = adminId;
    }
}

