package com.example.luventsuser.Models;

public class UserInfoModel {

    String userName,email,userId,status,profileUri,universityName,contactNumber;
    Long create_at;

    public UserInfoModel() {
    }

    public UserInfoModel(String userName, String email,String universityName,String contactNumber, String userId, String status, Long create_at,String profileUri) {
        this.userName = userName;
        this.email = email;
        this.userId = userId;
        this.status = status;
        this.create_at = create_at;
        this.profileUri=profileUri;
        this.universityName=universityName;
        this.contactNumber=contactNumber;
    }

    public String getUserName() {
        return userName;
    }

    public String getEmail() {
        return email;
    }

    public String getUserId() {
        return userId;
    }

    public String getStatus() {
        return status;
    }

    public Long getCreate_at() {
        return create_at;
    }

    public String getProfileUri() {
        return profileUri;
    }

    public String getUniversityName() {
        return universityName;
    }

    public String getContactNumber() {
        return contactNumber;
    }
}
