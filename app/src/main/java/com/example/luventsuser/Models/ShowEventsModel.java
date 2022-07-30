package com.example.luventsuser.Models;


public class ShowEventsModel {

    private String eventName,address,description,eventId,imageUri,hostedBy;
    private int year,month,date,eYear,eMonth,eDate,hour,min,eHour,eMin;
    long create_at,interested;


    public ShowEventsModel() {
    }

    public ShowEventsModel(String hostedBy, String eventName, String address, String description, String imgUri, String eventId, int year, int month, int date, int eYear, int eMonth, int eDate, int hour, int min, int eHour, int eMin, long create_at, long interested) {
        this.eventName = eventName;
        this.address = address;
        this.description = description;
        this.year = year;
        this.month = month;
        this.date = date;
        this.eYear = eYear;
        this.eMonth = eMonth;
        this.eDate = eDate;
        this.hour = hour;
        this.min = min;
        this.eHour = eHour;
        this.eMin = eMin;
        this.eventId=eventId;
        this.imageUri=imgUri;
        this.create_at=create_at;
        this.hostedBy=hostedBy;
        this.interested=interested;
    }



    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public int geteYear() {
        return eYear;
    }

    public void seteYear(int eYear) {
        this.eYear = eYear;
    }

    public int geteMonth() {
        return eMonth;
    }

    public void seteMonth(int eMonth) {
        this.eMonth = eMonth;
    }

    public int geteDate() {
        return eDate;
    }

    public void seteDate(int eDate) {
        this.eDate = eDate;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int geteHour() {
        return eHour;
    }

    public void seteHour(int eHour) {
        this.eHour = eHour;
    }

    public int geteMin() {
        return eMin;
    }

    public void seteMin(int eMin) {
        this.eMin = eMin;
    }

    public long getCreate_at() {
        return create_at;
    }

    public void setCreate_at(long create_at) {
        this.create_at = create_at;
    }


    public long getInterested() {
        return interested;
    }

    public void setInterested(long interested) {
        this.interested = interested;
    }

    public String getHostedBy() {
        return hostedBy;
    }

    public void setHostedBy(String hostedBy) {
        this.hostedBy = hostedBy;
    }
}
