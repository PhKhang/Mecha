package com.example.mechaadmin.dao;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "friendships")
public class FriendshipDAO {
    @Id
    @Column(name = "friendship_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int friendshipId;
    @Column(name = "user1_id")
    int user1;
    @Column(name = "user2_id")
    int user2;
    @Column(name = "created_at")
    LocalDateTime createdAt;
    
    public FriendshipDAO() {
        
    }
    
    // public FriendshipDAO() {
    // }

    public int getFriendshipId() {
        return friendshipId;
    }

    public void setFriendshipId(int friendshipId) {
        this.friendshipId = friendshipId;
    }

    public int getUser1() {
        return user1;
    }

    public void setUser1(int user1) {
        this.user1 = user1;
    }

    public int getUser2() {
        return user2;
    }

    public void setUser2(int user2) {
        this.user2 = user2;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    
}