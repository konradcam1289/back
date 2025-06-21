package com.workshopapp.workshopservice.service;

import com.workshopapp.workshopservice.model.AvailableDate;
import com.workshopapp.workshopservice.repository.AvailableDateRepository;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
public class AvailableDateService {

    private final AvailableDateRepository availableDateRepository;

    public AvailableDateService(AvailableDateRepository availableDateRepository) {
        this.availableDateRepository = availableDateRepository;
    }

    public List<AvailableDate> getAvailableDates() {
        List<AvailableDate> dates = availableDateRepository.findByReservedFalse();
        System.out.println("=== DEBUG AvailableDateService.getAvailableDates() ===");
        for (AvailableDate d : dates) {
            System.out.println("Available date: " + d.getDateTime());
        }
        return dates;
    }

    public void addAvailableDate(OffsetDateTime dateTime) {
        System.out.println("=== DEBUG AvailableDateService.addAvailableDate() ===");
        System.out.println("Original date: " + dateTime);
        OffsetDateTime normalizedDate = dateTime.withOffsetSameInstant(ZoneOffset.UTC);
        System.out.println("Normalized date: " + normalizedDate);

        if (!availableDateRepository.existsByDateTime(normalizedDate)) {
            availableDateRepository.save(new AvailableDate(null, normalizedDate, false));
            System.out.println("Date saved successfully.");
        } else {
            System.out.println("Date already exists, not saving.");
        }
    }


    public boolean isDateAvailable(OffsetDateTime dateTime) {
        boolean exists = availableDateRepository.existsByDateTime(dateTime);
        System.out.println("=== DEBUG AvailableDateService.isDateAvailable() ===");
        System.out.println("Checking if date exists: " + dateTime + " -> " + exists);
        return exists;
    }

    public void markDateAsReserved(OffsetDateTime dateTime) {
        System.out.println("=== DEBUG AvailableDateService.markDateAsReserved() ===");
        System.out.println("Marking date as reserved: " + dateTime);

        AvailableDate availableDate = availableDateRepository
                .findAll()
                .stream()
                .filter(date -> date.getDateTime().equals(dateTime))
                .findFirst()
                .orElseThrow();

        availableDate.setReserved(true);
        availableDateRepository.save(availableDate);
        System.out.println("Date marked as reserved.");
    }

    public void deleteAvailableDate(Long id) {
        System.out.println("=== DEBUG AvailableDateService.deleteAvailableDate() ===");
        System.out.println("Deleting date with ID: " + id);
        availableDateRepository.deleteById(id);
    }

    public void markDateAsReservedById(Long id) {
        System.out.println("=== DEBUG AvailableDateService.markDateAsReservedById() ===");
        System.out.println("Reserving by ID: " + id);

        AvailableDate availableDate = availableDateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Termin nie istnieje"));

        availableDate.setReserved(true);
        availableDateRepository.save(availableDate);
        System.out.println("Date reserved successfully.");
    }
}
