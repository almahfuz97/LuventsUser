package com.example.luventsuser.Models;

public class ScheduleModel {

    String eventId,scheduleTitle,scheduleDescription,scheduleItemId;
    int scheduleHour,scheduleMin,scheduleDate,busHour,busMin,scheduleMonth;
    long create_at;

    public ScheduleModel() {
    }

    public ScheduleModel(String eventId, String scheduleTitle, String scheduleDescription, String scheduleItemId, int scheduleHour, int scheduleMin, int scheduleDate, int busHour, int busMin, long create_at, int scheduleMonth) {
        this.eventId = eventId;
        this.scheduleTitle = scheduleTitle;
        this.scheduleDescription = scheduleDescription;
        this.scheduleItemId = scheduleItemId;
        this.scheduleHour = scheduleHour;
        this.scheduleMin = scheduleMin;
        this.scheduleDate = scheduleDate;
        this.busHour = busHour;
        this.busMin = busMin;
        this.create_at = create_at;
        this.scheduleMonth = scheduleMonth;
    }

    public String getEventId() {
        return eventId;
    }

    public String getScheduleTitle() {
        return scheduleTitle;
    }

    public String getScheduleDescription() {
        return scheduleDescription;
    }

    public String getScheduleItemId() {
        return scheduleItemId;
    }

    public int getScheduleHour() {
        return scheduleHour;
    }

    public int getScheduleMin() {
        return scheduleMin;
    }

    public int getScheduleDate() {
        return scheduleDate;
    }

    public int getBusHour() {
        return busHour;
    }

    public int getBusMin() {
        return busMin;
    }

    public long getCreate_at() {
        return create_at;
    }

    public int getScheduleMonth() {
        return scheduleMonth;
    }
}
