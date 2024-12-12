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




}
