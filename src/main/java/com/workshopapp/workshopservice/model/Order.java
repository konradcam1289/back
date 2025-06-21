package com.workshopapp.workshopservice.model;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinTable(
            name = "order_services",
            joinColumns = @JoinColumn(name = "order_id"),
            inverseJoinColumns = @JoinColumn(name = "service_id")
    )
    private List<WorkshopService> services;

    // ZAMIENILIŚMY DATE NA ID dostępnej daty
    @ManyToOne
    @JoinColumn(name = "available_date_id", nullable = false)
    private AvailableDate availableDate;

    @Column(nullable = false)
    private String paymentMethod;

    @Enumerated(EnumType.STRING)
    private RepairStatus repairStatus = RepairStatus.PENDING;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus = PaymentStatus.UNPAID;

    // ===== GETTERY I SETTERY =====

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public List<WorkshopService> getServices() { return services; }
    public void setServices(List<WorkshopService> services) { this.services = services; }

    public AvailableDate getAvailableDate() { return availableDate; }
    public void setAvailableDate(AvailableDate availableDate) { this.availableDate = availableDate; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public RepairStatus getRepairStatus() { return repairStatus; }
    public void setRepairStatus(RepairStatus repairStatus) { this.repairStatus = repairStatus; }

    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }
}
