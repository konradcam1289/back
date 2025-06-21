package com.workshopapp.workshopservice.dto;

public class DiagnoseRequest {
    private String description;

    public DiagnoseRequest(final String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }
}

