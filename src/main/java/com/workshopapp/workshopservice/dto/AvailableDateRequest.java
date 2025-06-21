package com.workshopapp.workshopservice.dto;

public class AvailableDateRequest {
    private String dateTime;  // ISO 8601 z offsetem, np. "2025-06-16T12:00:00+02:00"

    public String getDateTime() { return dateTime; }
    public void setDateTime(String dateTime) { this.dateTime = dateTime; }
}
