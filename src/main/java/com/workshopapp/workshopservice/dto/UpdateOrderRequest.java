package com.workshopapp.workshopservice.dto;

import java.util.List;

public class UpdateOrderRequest {
    private Long availableDateId;
    private List<Long> serviceIds;

    public Long getAvailableDateId() { return availableDateId; }
    public void setAvailableDateId(Long availableDateId) { this.availableDateId = availableDateId; }

    public List<Long> getServiceIds() { return serviceIds; }
    public void setServiceIds(List<Long> serviceIds) { this.serviceIds = serviceIds; }
}
