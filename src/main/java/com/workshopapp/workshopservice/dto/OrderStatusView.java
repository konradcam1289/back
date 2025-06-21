package com.workshopapp.workshopservice.dto;

import java.util.List;

public class OrderStatusView {
    private Long orderId;
    private String clientFirstName;
    private String clientLastName;
    private List<String> serviceNames;
    private String repairStatus;
    private String paymentStatus;

    // Gettery i settery
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public String getClientFirstName() { return clientFirstName; }
    public void setClientFirstName(String clientFirstName) { this.clientFirstName = clientFirstName; }

    public String getClientLastName() { return clientLastName; }
    public void setClientLastName(String clientLastName) { this.clientLastName = clientLastName; }

    public List<String> getServiceNames() { return serviceNames; }
    public void setServiceNames(List<String> serviceNames) { this.serviceNames = serviceNames; }

    public String getRepairStatus() { return repairStatus; }
    public void setRepairStatus(String repairStatus) { this.repairStatus = repairStatus; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
}
