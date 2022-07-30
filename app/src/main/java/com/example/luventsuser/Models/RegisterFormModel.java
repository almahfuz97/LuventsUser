package com.example.luventsuser.Models;

public class RegisterFormModel {

    String userId,eventId,scheduleId,userRegId,userName,userEmail,universityName,contactNumber,bkashTrxId,status;
    long create_at;


    public RegisterFormModel() {
    }

    public RegisterFormModel(String userId, String eventId, String scheduleId, String userRegId, String userName, String userEmail, String universityName, String contactNumber, String bkashTrxId, String status, long create_at) {
        this.userId = userId;
        this.eventId = eventId;
        this.scheduleId = scheduleId;
        this.userRegId = userRegId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.universityName = universityName;
        this.contactNumber = contactNumber;
        this.bkashTrxId = bkashTrxId;
        this.status = status;
        this.create_at = create_at;
    }

    public String getUserId() {
        return userId;
    }

    public String getEventId() {
        return eventId;
    }

    public String getScheduleId() {
        return scheduleId;
    }

    public String getUserRegId() {
        return userRegId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUniversityName() {
        return universityName;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public String getBkashTrxId() {
        return bkashTrxId;
    }

    public String getStatus() {
        return status;
    }

    public long getCreate_at() {
        return create_at;
    }
}
