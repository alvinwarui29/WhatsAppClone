package com.example.whatsappclone;

public class Contacts {
    private String UserName,Status,Profile;

    public Contacts() {
    }

    public Contacts(String userName, String status, String profile) {
        UserName = userName;
        Status = status;
        Profile = profile;

    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getProfile() {
        return Profile;
    }

    public void setProfile(String profile) {
        Profile = profile;
    }
}
