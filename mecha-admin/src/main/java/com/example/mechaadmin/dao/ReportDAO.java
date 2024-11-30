package com.example.mechaadmin.dao;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Report")
public class ReportDAO {
    @Id
    @Column(name = "report_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int reportId;
    @Column(name = "reporter_id")
    private int reporterId;
    // @Column(name = "user_id", table = "user")
    // private UserDAO reporter;
    @Column(name = "reported_id")
    private int reportedId;
    private String reason;
    private String status;
    @Column(name = "created_at")
    private LocalDate createdAt;
    
    public ReportDAO() {
        this.reportId = 0;
        this.reporterId = 0;
        this.reportedId = 0;
        this.reason = "";
        this.status = "";
        this.createdAt = LocalDate.now();
    }
    
    public ReportDAO(int reportId, int reportedId, int reporterId, String reason, String status, LocalDate createdAt) {
        this.reportId = reportId;
        this.reporterId = reporterId;
        this.reportedId = reportedId;
        this.reason = reason;
        this.status = status;
        this.createdAt = createdAt;
    }

    public int getReportId() {
        return reportId;
    }

    public void setReportId(int reportId) {
        this.reportId = reportId;
    }

    public int getReporterId() {
        return reporterId;
    }

    public void setReporterId(int reporterId) {
        this.reporterId = reporterId;
    }

    public int getReportedId() {
        return reportedId;
    }

    public void setReportedId(int reportedId) {
        this.reportedId = reportedId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }
    
    
}