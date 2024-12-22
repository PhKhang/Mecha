package com.example.mechaadmin.dao;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "admin_account")
public class AdminDAO {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column(name = "admin_id")
    int adminId;
    String email;
    String password_hash;
    @Column(name = "created_at")
    LocalDateTime createdAt;
    
    public AdminDAO() {
    }
    
    public AdminDAO(int adminId, String email, String password_hash, LocalDateTime createdAt) {
        this.adminId = adminId;
        this.email = email;
        this.password_hash = password_hash;
        this.createdAt = createdAt;
    }

    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword_hash() {
        return password_hash;
    }

    public void setPassword_hash(String password_hash) {
        this.password_hash = password_hash;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    
}
