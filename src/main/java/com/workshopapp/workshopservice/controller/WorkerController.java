package com.workshopapp.workshopservice.controller.worker;

import com.workshopapp.workshopservice.dto.OrderDetailsDTO;
import com.workshopapp.workshopservice.dto.OrderDetailsDTO.ServiceDTO;
import com.workshopapp.workshopservice.dto.UpdateOrderRequest;
import com.workshopapp.workshopservice.model.*;
import com.workshopapp.workshopservice.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/worker")
@PreAuthorize("hasAuthority('ROLE_WORKER')")
@CrossOrigin(origins = "http://localhost:5173")
public class WorkerController {

    private final OrderRepository orderRepository;
    private final AvailableDateRepository availableDateRepository;
    private final ServiceRepository serviceRepository;

    public WorkerController(OrderRepository orderRepository,
                            AvailableDateRepository availableDateRepository,
                            ServiceRepository serviceRepository) {
        this.orderRepository = orderRepository;
        this.availableDateRepository = availableDateRepository;
        this.serviceRepository = serviceRepository;
    }

    // 🔹 Lista zamówień z imieniem, nazwiskiem i usługami
    @GetMapping("/orders")
    public List<OrderDetailsDTO> getAllOrders() {
        return orderRepository.findAll().stream().map(order -> {
            User user = order.getUser();
            List<ServiceDTO> services = order.getServices().stream()
                    .map(s -> new ServiceDTO(s.getId(), s.getName(), s.getPrice()))
                    .collect(Collectors.toList());

            return new OrderDetailsDTO(
                    order.getId(),
                    user.getFirstName(),
                    user.getLastName(),
                    order.getAvailableDate().getDateTime().toLocalDateTime(),
                    order.getPaymentMethod(),
                    order.getRepairStatus().name(),
                    order.getPaymentStatus().name(),
                    services
            );
        }).collect(Collectors.toList());
    }

    // 🔹 Pobierz szczegóły zamówienia
    @GetMapping("/orders/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        return orderRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 🔹 Edytuj zamówienie: zmiana terminu i usług
    @PutMapping("/orders/{id}/edit")
    public ResponseEntity<?> editOrder(@PathVariable Long id, @RequestBody UpdateOrderRequest request) {
        return orderRepository.findById(id).map(order -> {
            AvailableDate newDate = availableDateRepository.findById(request.getAvailableDateId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nieprawidłowy termin"));

            List<WorkshopService> newServices = serviceRepository.findAllById(request.getServiceIds());

            order.setAvailableDate(newDate);
            order.setServices(newServices);

            orderRepository.save(order);
            return ResponseEntity.ok().build();
        }).orElse(ResponseEntity.notFound().build());
    }

    // 🔹 Usuń zamówienie
    @DeleteMapping("/orders/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable Long id) {
        if (!orderRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        orderRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
