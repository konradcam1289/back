package com.workshopapp.workshopservice.repository;

import com.workshopapp.workshopservice.model.AvailableDate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;

public interface AvailableDateRepository extends JpaRepository<AvailableDate, Long> {
    List<AvailableDate> findByReservedFalse();
    boolean existsByDateTime(OffsetDateTime dateTime);
    void deleteByDateTime(OffsetDateTime dateTime);
}
