package com.example.mechaadmin.dao;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
public class MessageDAO {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    Integer messageId;
    @Column(name = "chat_id")
    Integer chatId;
    @Column(name = "sender_id")
    Integer senderId;
    String content;
    @Column(name = "created_at")
    LocalDateTime createAt;
    @Column(name = "is_read")
    Boolean isRead;
    

    public MessageDAO(){};

    public MessageDAO(Integer messageId, Integer chatId, Integer senderId, String content, LocalDateTime createAt, Boolean isRead) {
        this.messageId = messageId;
        this.chatId = chatId;
        this.senderId = senderId;
        this.content = content;
        this.createAt = createAt;
        this.isRead = isRead;
    }

    public Integer getMessageId() {
        return messageId;
    }

    public void setMessageId(Integer messageId) {
        this.messageId = messageId;
    }

    public Integer getChatId() {
        return chatId;
    }

    public void setChatId(Integer chatId) {
        this.chatId = chatId;
    }

    public Integer getSenderId() {
        return senderId;
    }

    public void setSenderId(Integer senderId) {
        this.senderId = senderId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }
    
    
}
