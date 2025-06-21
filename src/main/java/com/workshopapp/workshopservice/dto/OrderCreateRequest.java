package com.workshopapp.workshopservice.dto;

import java.util.List;

public class OrderCreateRequest {
    private String username;
    private List<Long> serviceIds;
    private Long availableDateId;  // <- poprawione
    private String paymentMethod;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public List<Long> getServiceIds() { return serviceIds; }
    public void setServiceIds(List<Long> serviceIds) { this.serviceIds = serviceIds; }

    public Long getAvailableDateId() { return availableDateId; }
    public void setAvailableDateId(Long availableDateId) { this.availableDateId = availableDateId; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
}
