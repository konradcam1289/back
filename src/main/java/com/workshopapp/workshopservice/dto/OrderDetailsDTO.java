package com.workshopapp.workshopservice.dto;

import java.time.LocalDateTime;
import java.util.List;

public class OrderDetailsDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private LocalDateTime date;
    private String paymentMethod;
    private String repairStatus;
    private String paymentStatus;
    private List<ServiceDTO> services;

    public OrderDetailsDTO(Long id, String firstName, String lastName,
                           LocalDateTime date, String paymentMethod,
                           String repairStatus, String paymentStatus,
                           List<ServiceDTO> services) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.date = date;
        this.paymentMethod = paymentMethod;
        this.repairStatus = repairStatus;
        this.paymentStatus = paymentStatus;
        this.services = services;
    }

    public static class ServiceDTO {
        private Long id;
        private String name;
        private Double price;

        public ServiceDTO(Long id, String name, Double price) {
            this.id = id;
            this.name = name;
            this.price = price;
        }

        public Long getId() { return id; }
        public String getName() { return name; }
        public Double getPrice() { return price; }
    }

    public Long getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public LocalDateTime getDate() { return date; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getRepairStatus() { return repairStatus; }
    public String getPaymentStatus() { return paymentStatus; }
    public List<ServiceDTO> getServices() { return services; }
}
