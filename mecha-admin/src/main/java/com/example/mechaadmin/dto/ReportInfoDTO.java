package com.example.mechaadmin.dto;

import java.time.LocalDate;

public class ReportInfoDTO {
    private int reportId;
    private int reporterId;
    private String repoter;
    private int reportedId;
    private String reported;
    private String reason;
    private String status;
    private LocalDate createdAt;
    
    public ReportInfoDTO() {
        this.reportId = 0;
        this.reporterId = 0;
        this.repoter = "";
        this.reportedId = 0;
        this.reported = "";
        this.reason = "";
        this.status = "";
        this.createdAt = LocalDate.now();
    }
    
    public ReportInfoDTO(int reportId, int reporterId, String repoter, int reportedId, String reported, String reason, String status, LocalDate createdAt) {
        this.reportId = reportId;
        this.reporterId = reporterId;
        this.repoter = repoter;
        this.reportedId = reportedId;
        this.reported = reported;
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

    public String getReporter() {
        return repoter;
    }

    public void setRepoter(String repoter) {
        this.repoter = repoter;
    }

    public int getReportedId() {
        return reportedId;
    }

    public void setReportedId(int reportedId) {
        this.reportedId = reportedId;
    }

    public String getReported() {
        return reported;
    }

    public void setReported(String reported) {
        this.reported = reported;
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