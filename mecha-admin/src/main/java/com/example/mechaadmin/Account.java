package com.example.mechaadmin;

public class Account {
    private String accountFull;
    private String accountUser;
    private String accountStatus;
    private String accountCreation;
    private String accountLog;
    private String email;
    private String address;
    private String dob;
    private String gender;
    private Integer directFriends;
    private Integer indirectFriends;

    public Account(String accountFull, String accountUser, String accountStatus, String accountCreation,
            String accountLog, String email, String address, String dob, String gender, Integer directFriends, Integer indirectFriends) {
        this.accountFull = accountFull;
        this.accountUser = accountUser;
        this.accountStatus = accountStatus;
        this.accountCreation = accountCreation;
        this.accountLog = accountLog;
        this.email = email;
        this.address = address;
        this.dob = dob;
        this.gender = gender;
        this.directFriends = directFriends;
        this.indirectFriends = indirectFriends;
    }

    public String getAccountFull() {
        return accountFull;
    }

    public String getAccountUser() {
        return accountUser;
    }

    public String getAccountStatus() {
        return accountStatus;
    }

    public String getAccountCreation() {
        return accountCreation;
    }

    public String getAccountLog() {
        return accountLog;
    }

    public String getAccountEmail() {
        return email;
    }

    public String getAccountAddress() {
        return address;
    }

    public String getAccountDob() {
        return dob;
    }

    public String getAccountGender() {
        return gender;
    }
    
    public Integer getDirectFriends() {
        return directFriends;
    }
    
    public Integer getIndirectFriends() {
        return indirectFriends;
    }
}
