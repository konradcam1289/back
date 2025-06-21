package com.workshopapp.workshopservice.controller;

import com.workshopapp.workshopservice.dto.AvailableDateRequest;
import com.workshopapp.workshopservice.model.AvailableDate;
import com.workshopapp.workshopservice.service.AvailableDateService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/available-dates")
public class AvailableDateController {

    private final AvailableDateService availableDateService;

    public AvailableDateController(AvailableDateService availableDateService) {
        this.availableDateService = availableDateService;
    }

    @GetMapping
    public ResponseEntity<List<AvailableDate>> getAvailableDates() {
        return ResponseEntity.ok(availableDateService.getAvailableDates());
    }

    @PostMapping("/add")
    public ResponseEntity<Void> addDate(@RequestBody AvailableDateRequest request) {
        OffsetDateTime dateTime = OffsetDateTime.parse(request.getDateTime());
        availableDateService.addAvailableDate(dateTime);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/reserve")
    public ResponseEntity<Void> reserveDate(@PathVariable Long id) {
        availableDateService.markDateAsReservedById(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDate(@PathVariable Long id) {
        availableDateService.deleteAvailableDate(id);
        return ResponseEntity.ok().build();
    }
}
