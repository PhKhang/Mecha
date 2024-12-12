package com.example.mechaadmin.dao;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_members")
public class MemberDAO {
    @Id
    @Column(name = "chat_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int chatId;
    @Column(name = "user_id")
    int userId;
    @Column(name = "joined_at")
    LocalDateTime joinedAt;

    public MemberDAO() {
        this.chatId = 0;
        this.userId = 0;
        this.joinedAt = LocalDateTime.now();
    }
    
    public MemberDAO(int chatId, int userId, LocalDateTime joinedAt) {
        this.chatId = chatId;
        this.userId = userId;
        this.joinedAt = joinedAt;
    }

    public int getChatId() {
        return chatId;
    }

    public void setChatId(int chatId) {
        this.chatId = chatId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(LocalDateTime joinedAt) {
        this.joinedAt = joinedAt;
    }
}