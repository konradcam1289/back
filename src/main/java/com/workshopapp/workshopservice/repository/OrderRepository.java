package com.workshopapp.workshopservice.repository;

import com.workshopapp.workshopservice.model.AvailableDate;
import com.workshopapp.workshopservice.model.Order;
import com.workshopapp.workshopservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserId(Long userId);

    boolean existsByAvailableDate(AvailableDate availableDate);

    List<Order> findByAvailableDate_DateTimeBetween(OffsetDateTime start, OffsetDateTime end);

    List<Order> findByUser(User user);
}

