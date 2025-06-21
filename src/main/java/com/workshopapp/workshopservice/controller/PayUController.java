package com.workshopapp.workshopservice.controller;

import com.workshopapp.workshopservice.service.PayUService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PayUController {

    private final PayUService payUService;

    @Autowired
    public PayUController(PayUService payUService) {
        this.payUService = payUService;
    }

    @PostMapping("/create")
    public ResponseEntity<Map<String, String>> createPayment(@RequestParam Long orderId) {
        String redirectUri = payUService.createOrder(orderId);
        Map<String, String> response = new HashMap<>();
        response.put("redirectUri", redirectUri);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/notify")
    public ResponseEntity<String> handleNotification(@RequestBody Map<String, Object> notification) {
        // (opcjonalnie: logika do obsługi webhooka od PayU)
        return ResponseEntity.ok("Notification received");
    }
}
