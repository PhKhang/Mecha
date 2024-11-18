package com.example.mechaadmin;

public class Report {
    private String reportId;
    private String reportReporter;
    private String reportReported;
    private String reportReason;
    private String reportDate;
    private String reportStatus;
    
    public Report(String reportId, String reportReporter, String reportReported, String reportReason, String reportDate, String reportStatus) {
        this.reportId = reportId;
        this.reportReporter = reportReporter;
        this.reportReported = reportReported;
        this.reportReason = reportReason;
        this.reportDate = reportDate;
        this.reportStatus = reportStatus;
    }
    
    public String getReportId() {
        return reportId;
    }
    
    public String getReportReporter() {
        return reportReporter;
    }
    
    public String getReportReported() {
        return reportReported;
    }
    
    public String getReportReason() {
        return reportReason;
    }
    
    public String getReportDate() {
        return reportDate;
    }
    
    public String getReportStatus() {
        return reportStatus;
    }
}
