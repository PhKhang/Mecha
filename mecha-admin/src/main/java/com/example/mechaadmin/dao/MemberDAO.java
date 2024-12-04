package com.example.mechaadmin.dao;

import jakarta.persistence.*;

import java.time.LocalDate;

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
    LocalDate joinedAt;

    public MemberDAO() {
        this.chatId = 0;
        this.userId = 0;
        this.joinedAt = LocalDate.now();
    }
    
    public MemberDAO(int chatId, int userId, LocalDate joinedAt) {
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

    public LocalDate getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(LocalDate joinedAt) {
        this.joinedAt = joinedAt;
    }
}