package com.example.mechaadmin.dto;

public class UsageDTO {
    Integer month;
    Integer opened;
    Integer year;

    public UsageDTO() {
        this.month = 0;
        this.opened = 0;
    }

    public UsageDTO(Integer month, Integer opened) {
        this.month = month;
        this.opened = opened;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getOpened() {
        return opened;
    }

    public void setOpened(Integer opened) {
        this.opened = opened;
    }
    
    public Integer getYear() {
        return year;
    }
    
    public void setYear(Integer year) {
        this.year = year;
    }
}
