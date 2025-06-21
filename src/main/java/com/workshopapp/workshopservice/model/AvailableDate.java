package com.workshopapp.workshopservice.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "available_dates")
public class AvailableDate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private OffsetDateTime dateTime;

    private boolean reserved = false;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public AvailableDate() {}

    public AvailableDate(Long id, OffsetDateTime dateTime, boolean reserved) {
        this.id = id;
        this.dateTime = dateTime;
        this.reserved = reserved;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public OffsetDateTime getDateTime() { return dateTime; }
    public void setDateTime(OffsetDateTime dateTime) { this.dateTime = dateTime; }
    public boolean isReserved() { return reserved; }
    public void setReserved(boolean reserved) { this.reserved = reserved; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
