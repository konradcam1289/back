package com.workshopapp.workshopservice.controller;

import com.workshopapp.workshopservice.dto.*;
import com.workshopapp.workshopservice.model.Order;
import com.workshopapp.workshopservice.security.JwtService;
import com.workshopapp.workshopservice.service.OrderService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final JwtService jwtService;

    public OrderController(OrderService orderService, JwtService jwtService) {
        this.orderService = orderService;
        this.jwtService = jwtService;
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('ROLE_CLIENT')")
    public ResponseEntity<?> createOrder(@RequestBody OrderCreateRequest request) {
        try {
            String username = request.getUsername();
            List<Long> serviceIds = request.getServiceIds();
            Long availableDateId = request.getAvailableDateId();
            String paymentMethod = request.getPaymentMethod();

            if (username == null || serviceIds == null || serviceIds.isEmpty() || availableDateId == null || paymentMethod == null) {
                return ResponseEntity.badRequest().body("Brak wymaganych danych");
            }

            Order savedOrder = orderService.createOrder(username, serviceIds, availableDateId, paymentMethod);
            return ResponseEntity.ok(savedOrder);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(400).body("Błąd przetwarzania zamówienia: " + e.getMessage());
        }
    }

    @GetMapping("/user")
    @PreAuthorize("hasAuthority('ROLE_CLIENT')")
    public ResponseEntity<List<Order>> getUserOrders(@RequestHeader("Authorization") String token) {
        try {
            String username = jwtService.extractUsername(token.replace("Bearer ", ""));
            if (username == null) {
                return ResponseEntity.status(401).body(null);
            }

            List<Order> orders = orderService.getUserOrders(username);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/all-for-worker")
    @PreAuthorize("hasAuthority('ROLE_WORKER')")
    public ResponseEntity<List<Order>> getAllOrdersForWorker() {
        List<Order> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/status-view")
    @PreAuthorize("hasAuthority('ROLE_WORKER')")
    public ResponseEntity<List<OrderStatusView>> getOrdersForWorkerView() {
        return ResponseEntity.ok(orderService.getOrdersForWorkerView());
    }

    @PutMapping("/{id}/update-status")
    @PreAuthorize("hasAuthority('ROLE_WORKER')")
    public ResponseEntity<Void> updateRepairStatus(
            @PathVariable Long id,
            @RequestBody UpdateRepairStatusRequest request
    ) {
        orderService.updateRepairStatus(id, request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/update-payment")
    @PreAuthorize("hasAuthority('ROLE_WORKER')")
    public ResponseEntity<Void> updatePaymentStatus(
            @PathVariable Long id,
            @RequestBody UpdatePaymentStatusRequest request
    ) {
        orderService.updatePaymentStatus(id, request);
        return ResponseEntity.ok().build();
    }
}
