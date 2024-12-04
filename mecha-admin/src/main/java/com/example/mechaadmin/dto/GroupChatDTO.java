package com.example.mechaadmin.dto;

import java.time.LocalDate;
import java.util.List;

public class GroupChatDTO {
    private Integer chatId;
    private String groupName;
    private String chatType;
    private Integer adminId;
    private LocalDate createdAt;
    private List<String> members;
    private Integer totalMembers;
    
    public GroupChatDTO() {
        this.chatId = 0;
        this.groupName = "";
        this.chatType = "";
        this.adminId = -1;
        this.createdAt = LocalDate.now();
        this.members = null;
        this.totalMembers = 0;
    }
    
    public GroupChatDTO(int chatId, String groupName, String chatType, Integer adminId, LocalDate createdAt, List<String> members, int totalMembers) {
        this.chatId = chatId;
        this.groupName = groupName;
        this.chatType = chatType;
        this.adminId = adminId;
        this.createdAt = createdAt;
        this.members = members;
        this.totalMembers = totalMembers;
    }

    public Integer getChatId() {
        return chatId;
    }

    public void setChatId(Integer chatId) {
        this.chatId = chatId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getChatType() {
        return chatType;
    }

    public void setChatType(String chatType) {
        this.chatType = chatType;
    }

    public Integer getAdminId() {
        return adminId;
    }

    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    public Integer getTotalMembers() {
        return totalMembers;
    }

    public void setTotalMembers(Integer totalMembers) {
        this.totalMembers = totalMembers;
    }
    
    
}
