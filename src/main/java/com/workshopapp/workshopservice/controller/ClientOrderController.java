package com.workshopapp.workshopservice.controller;

import com.workshopapp.workshopservice.dto.UpdateOrderRequest;
import com.workshopapp.workshopservice.model.AvailableDate;
import com.workshopapp.workshopservice.model.Order;
import com.workshopapp.workshopservice.model.User;
import com.workshopapp.workshopservice.model.WorkshopService;
import com.workshopapp.workshopservice.repository.AvailableDateRepository;
import com.workshopapp.workshopservice.repository.OrderRepository;
import com.workshopapp.workshopservice.repository.ServiceRepository;
import com.workshopapp.workshopservice.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/client/orders")
@PreAuthorize("hasAuthority('ROLE_CLIENT')")
public class ClientOrderController {

    private final OrderRepository orderRepository;
    private final AvailableDateRepository availableDateRepository;
    private final ServiceRepository serviceRepository;
    private final UserRepository userRepository;

    public ClientOrderController(OrderRepository orderRepository,
                                 AvailableDateRepository availableDateRepository,
                                 ServiceRepository serviceRepository,
                                 UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.availableDateRepository = availableDateRepository;
        this.serviceRepository = serviceRepository;
        this.userRepository = userRepository;
    }

    @PutMapping("/{id}/edit")
    public ResponseEntity<?> editOwnOrder(@PathVariable Long id,
                                          @RequestBody UpdateOrderRequest request,
                                          Authentication authentication) {
        String username = authentication.getName();

        return orderRepository.findById(id).map(order -> {
            if (!order.getUser().getUsername().equals(username)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Nie masz dostępu do tej rezerwacji.");
            }

            AvailableDate newDate = availableDateRepository.findById(request.getAvailableDateId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nieprawidłowy termin"));

            List<WorkshopService> newServices = serviceRepository.findAllById(request.getServiceIds());

            order.setAvailableDate(newDate);
            order.setServices(newServices);

            orderRepository.save(order);
            return ResponseEntity.ok().build();
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<Order> getClientOrders(Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Użytkownik nie znaleziony"));
        return orderRepository.findByUser(user);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOwnOrderById(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();
        return orderRepository.findById(id).map(order -> {
            if (!order.getUser().getUsername().equals(username)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Brak dostępu do tej rezerwacji.");
            }
            return ResponseEntity.ok(order);
        }).orElse(ResponseEntity.notFound().build());
    }
}
