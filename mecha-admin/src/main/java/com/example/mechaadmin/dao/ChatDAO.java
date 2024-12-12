package com.example.mechaadmin.dao;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "chats")
public class ChatDAO {
    @Id
    @Column(name = "chat_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer chatId;
    @Column(name = "group_name")
    private String groupName;
    @Column(name = "chat_type")
    private String chatType;
    @Column(name = "admin_id")
    private Integer adminId;
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public ChatDAO() {
        this.chatId = 0;
        this.groupName = "";
        this.chatType = "";
        this.adminId = 0;
        this.createdAt = LocalDateTime.now();
    }

    public ChatDAO(int chatId, String groupName, String chatType, int admindId, LocalDateTime createdAt) {
        this.chatId = chatId;
        this.groupName = groupName;
        this.chatType = chatType;
        this.adminId = admindId;
        this.createdAt = createdAt;
    }

    public Integer getChatId() {
        return chatId;
    }

    public void setChatId(int chatId) {
        this.chatId = chatId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getChatType() {
        return chatType;
    }

    public void setChatType(String chatType) {
        this.chatType = chatType;
    }

    public Integer getAdminId() {
        return adminId;
    }

    public void setAdminId(int admindId) {
        this.adminId = admindId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}