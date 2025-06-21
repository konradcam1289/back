package com.workshopapp.workshopservice.controller.admin;

import com.workshopapp.workshopservice.dto.UpdateUserRequest;
import com.workshopapp.workshopservice.dto.CreateUserRequest;
import com.workshopapp.workshopservice.model.*;
import com.workshopapp.workshopservice.repository.OrderRepository;
import com.workshopapp.workshopservice.repository.ServiceRepository;
import com.workshopapp.workshopservice.repository.UserRepository;
import com.workshopapp.workshopservice.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
@CrossOrigin(origins = "http://localhost:5173")
public class AdminController {

    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;
    private final OrderRepository orderRepository;
    private final UserService userService;

    public AdminController(UserRepository userRepository,
                           ServiceRepository serviceRepository,
                           OrderRepository orderRepository,
                           UserService userService) {
        this.userRepository = userRepository;
        this.serviceRepository = serviceRepository;
        this.orderRepository = orderRepository;
        this.userService = userService;
    }

    // DTO do zamówień z usługami
    public record WorkshopServiceDTO(Long id, String name, Double price) {}
    public record OrderDTO(Long id, String repairStatus, String paymentStatus,
                           LocalDateTime date, List<WorkshopServiceDTO> services) {}

    private List<WorkshopServiceDTO> mapServices(List<WorkshopService> services) {
        return services.stream().map(service ->
                new WorkshopServiceDTO(service.getId(), service.getName(), service.getPrice())
        ).toList();
    }

    // 🔹 Użytkownicy
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findByActiveTrue();
    }

    @GetMapping("/users/inactive")
    public List<User> getInactiveUsers() {
        return userRepository.findByActiveFalse();
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return ResponseEntity.of(userRepository.findById(id));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deactivateUser(@PathVariable Long id) {
        userService.deactivateUser(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/users/{id}/reactivate")
    public ResponseEntity<?> reactivateUser(@PathVariable Long id) {
        userService.reactivateUser(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody UpdateUserRequest request) {
        User updatedUser = userService.updateUser(id, request);
        return ResponseEntity.ok(updatedUser);
    }

    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody CreateUserRequest request) {
        User createdUser = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    // 🔹 Przegląd rezerwacji konkretnego użytkownika
    @GetMapping("/users/{userId}/orders")
    public List<OrderDTO> getUserOrders(@PathVariable Long userId) {
        return orderRepository.findByUserId(userId).stream().map(order ->
                new OrderDTO(
                        order.getId(),
                        order.getRepairStatus().name(),
                        order.getPaymentStatus().name(),
                        order.getAvailableDate().getDateTime().toLocalDateTime(),
                        mapServices(order.getServices())
                )).toList();
    }

    // 🔹 Usługi
    @GetMapping("/services")
    public List<WorkshopService> getAllServices() {
        return serviceRepository.findAll();
    }

    @PostMapping("/services")
    public WorkshopService addService(@RequestBody WorkshopService service) {
        return serviceRepository.save(service);
    }

    @PutMapping("/services/{id}")
    public ResponseEntity<WorkshopService> updateService(@PathVariable Long id, @RequestBody WorkshopService updatedService) {
        WorkshopService existingService = serviceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Service not found"));

        existingService.setName(updatedService.getName());
        existingService.setPrice(updatedService.getPrice());
        existingService.setDescription(updatedService.getDescription());
        existingService.setAvailable(updatedService.isAvailable());

        WorkshopService savedService = serviceRepository.save(existingService);
        return ResponseEntity.ok(savedService);
    }

    @DeleteMapping("/services/{id}")
    public ResponseEntity<?> deleteService(@PathVariable Long id) {
        serviceRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
