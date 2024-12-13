package com.example.mechaadmin.dto;

import java.time.LocalDateTime;

public class ActivityDTO {
    String username;
    String fullName;
    LocalDateTime creationDate;
    Integer timeOpened;
    Integer privateChat;
    Integer groupChat;

    public ActivityDTO() {
        this.username = "";
        this.fullName = "";
        this.creationDate = LocalDateTime.now();
        this.timeOpened = 0;
        this.privateChat = 0;
        this.groupChat = 0;
    }

    public ActivityDTO(String username, String fullName, LocalDateTime creationDate, Integer timeOpened, Integer privateChat, Integer groupChat) {
        this.username = username;
        this.fullName = fullName;
        this.creationDate = creationDate;
        this.timeOpened = timeOpened;
        this.privateChat = privateChat;
        this.groupChat = groupChat;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public Integer getTimeOpened() {
        return timeOpened;
    }

    public void setTimeOpened(Integer timeOpened) {
        this.timeOpened = timeOpened;
    }

    public Integer getPrivateChat() {
        return privateChat;
    }

    public void setPrivateChat(Integer privateChat) {
        this.privateChat = privateChat;
    }

    public Integer getGroupChat() {
        return groupChat;
    }

    public void setGroupChat(Integer groupChat) {
        this.groupChat = groupChat;
    }
}
