package com.example.mechaadmin.dao;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "log_history")
public class LogDAO {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    int logId;
    @Column(name = "user_id")
    int userId;
    @Column(name = "section_start")
    LocalDateTime sectionStart;
    @Column(name = "section_end")
    LocalDateTime sectionEnd;

    public LogDAO() {
    }

    public LogDAO(int logId, int userId, LocalDateTime sectionStart, LocalDateTime sectionEnd) {
        this.logId = logId;
        this.userId = userId;
        this.sectionStart = sectionStart;
        this.sectionEnd = sectionEnd;
    }
    
    public int getLogId() {
        return logId;
    }
    
    public void setLogId(int logId) {
        this.logId = logId;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public LocalDateTime getSectionStart() {
        return sectionStart;
    }
    
    public void setSectionStart(LocalDateTime sectionStart) {
        this.sectionStart = sectionStart;
    }
    
    public LocalDateTime getSectionEnd() {
        return sectionEnd;
    }
    
    public void setSectionEnd(LocalDateTime sectionEnd) {
        this.sectionEnd = sectionEnd;
    }
}
