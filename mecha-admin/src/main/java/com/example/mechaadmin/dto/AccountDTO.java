package com.example.mechaadmin.dto;

import java.time.LocalDateTime;

public class AccountDTO {
    Integer id;
    String fullName;
    String username;
    String status;
    LocalDateTime createdAt;
    LocalDateTime recentLogin;
    String address;
    String email;
    LocalDateTime dob;
    String gender;
    Integer directFriends;
    Integer indirectFriends;
    String profileUrl = "https://pub-b0a9bdcea1cd4f6ca28d98f878366466.r2.dev/1.png";
    String adminAction;
    
    public AccountDTO() {
        this.fullName = "";
        this.username = "";
        this.status = "";
        this.createdAt = LocalDateTime.now();
        this.recentLogin = LocalDateTime.now();
        this.address = "";
        this.email = "";
    }
    
    public AccountDTO(int id, String fullName, String username, String status, LocalDateTime createdAt, LocalDateTime recentLogin, String address, String email) {
        this.id = id;
        this.fullName = fullName;
        this.username = username;
        this.status = status;
        this.createdAt = createdAt;
        this.recentLogin = recentLogin;
        this.address = address;
        this.email = email;
    }

    public Integer getUserId() {
        return id;
    }
    
    public void setAccountId(int id) {
        this.id = id;
    }
    
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getRecentLogin() {
        return recentLogin;
    }

    public void setRecentLogin(LocalDateTime recentLogin) {
        this.recentLogin = recentLogin;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

	public LocalDateTime getDob() {
		return dob;
	}

	public void setDob(LocalDateTime dob) {
		this.dob = dob;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public Integer getDirectFriends() {
		return directFriends;
	}

	public void setDirectFriends(Integer directFriends) {
		this.directFriends = directFriends;
	}

	public Integer getIndirectFriends() {
		return indirectFriends;
	}

	public void setIndirectFriends(Integer indirectFriends) {
		this.indirectFriends = indirectFriends;
	}
    
    public String getProfileUrl() {
        return profileUrl;
    }
    
    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAdminAction() {
        return adminAction;
    }

    public void setAdminAction(String adminAction) {
        this.adminAction = adminAction;
    }
    
    
}
