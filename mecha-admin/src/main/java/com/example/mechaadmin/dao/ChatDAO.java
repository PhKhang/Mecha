package com.example.mechaadmin.dao;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "log_history")
public class ChatDAO {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    int logId;
    @Column(name = "user_id")
    int userId;
    @Column(name = "section_start")
    LocalDate sectionStart;
    @Column(name = "section_end")
    LocalDate sectionEnd;

    public ChatDAO() {
    }

    public ChatDAO(int logId, int userId, LocalDate sectionStart, LocalDate sectionEnd) {
        this.logId = logId;
        this.userId = userId;
        this.sectionStart = sectionStart;
        this.sectionEnd = sectionEnd;
    }




}