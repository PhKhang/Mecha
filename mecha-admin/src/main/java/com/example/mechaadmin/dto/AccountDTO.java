package com.example.mechaadmin.dto;

import java.time.LocalDate;

public class AccountDTO {
    String fullName;
    String username;
    String status;
    LocalDate createdAt;
    LocalDate recentLogin;
    String address;
    String email;
    String dob;
    String gender;
    Integer directFriends;
    Integer indirectFriends;
    
    public AccountDTO() {
        this.fullName = "";
        this.username = "";
        this.status = "";
        this.createdAt = LocalDate.now();
        this.recentLogin = LocalDate.now();
        this.address = "";
        this.email = "";
    }
    
    public AccountDTO(String fullName, String username, String status, LocalDate createdAt, LocalDate recentLogin, String address, String email) {
        this.fullName = fullName;
        this.username = username;
        this.status = status;
        this.createdAt = createdAt;
        this.recentLogin = recentLogin;
        this.address = address;
        this.email = email;
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

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDate getRecentLogin() {
        return recentLogin;
    }

    public void setRecentLogin(LocalDate recentLogin) {
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

	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
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
    
    
}
