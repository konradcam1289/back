package com.workshopapp.workshopservice.dto;

public class DiagnoseResponse {
    private String suggestedService;

    public DiagnoseResponse(final String suggestedService) {
        this.suggestedService = suggestedService;
    }

    public String getSuggestedService() {
        return suggestedService;
    }

    public void setSuggestedService(final String suggestedService) {
        this.suggestedService = suggestedService;
    }
}

