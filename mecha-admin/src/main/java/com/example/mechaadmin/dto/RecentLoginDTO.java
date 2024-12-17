package com.example.mechaadmin.dto;

import java.time.LocalDateTime;

public class RecentLoginDTO {
    LocalDateTime time;
    String username;
    String fullName;

    public RecentLoginDTO() {
        this.time = LocalDateTime.now();
        this.username = "";
        this.fullName = "";
    }

    public RecentLoginDTO(LocalDateTime time, String username, String fullName) {
        this.time = time;
        this.username = username;
        this.fullName = fullName;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
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

    
}
