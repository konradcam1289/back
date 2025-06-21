package com.workshopapp.workshopservice.service;

import com.workshopapp.workshopservice.dto.OrderStatusView;
import com.workshopapp.workshopservice.dto.UpdateRepairStatusRequest;
import com.workshopapp.workshopservice.dto.UpdatePaymentStatusRequest;
import com.workshopapp.workshopservice.model.*;
import com.workshopapp.workshopservice.repository.*;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;
    private final AvailableDateRepository availableDateRepository;

    public OrderService(OrderRepository orderRepository, UserRepository userRepository,
                        ServiceRepository serviceRepository, AvailableDateRepository availableDateRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.serviceRepository = serviceRepository;
        this.availableDateRepository = availableDateRepository;
    }

    public Order createOrder(String username, List<Long> serviceIds, Long appointmentId, String paymentMethod) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        AvailableDate availableDate = availableDateRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Termin nie istnieje"));

        if (availableDate.isReserved()) {
            throw new IllegalArgumentException("Termin już zajęty");
        }

        availableDate.setReserved(true);
        availableDate.setUser(user);
        availableDateRepository.save(availableDate);

        List<WorkshopService> services = serviceRepository.findAllById(serviceIds);

        Order order = new Order();
        order.setUser(user);
        order.setAvailableDate(availableDate);
        order.setServices(services);
        order.setPaymentMethod(paymentMethod);
        order.setRepairStatus(RepairStatus.PENDING);

        if ("cash".equalsIgnoreCase(paymentMethod)) {
            order.setPaymentStatus(PaymentStatus.UNPAID);
        } else {
            order.setPaymentStatus(PaymentStatus.PAID);
        }

        return orderRepository.save(order);
    }

    public List<Order> getUserOrders(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Użytkownik nie znaleziony"));
        return orderRepository.findByUser(user);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId).orElse(null);
    }

    public List<OrderStatusView> getOrdersForWorkerView() {
        List<Order> orders = orderRepository.findAll();

        return orders.stream().map(order -> {
            OrderStatusView dto = new OrderStatusView();
            dto.setOrderId(order.getId());
            dto.setClientFirstName(order.getUser().getFirstName());
            dto.setClientLastName(order.getUser().getLastName());
            dto.setRepairStatus(order.getRepairStatus().name());
            dto.setPaymentStatus(order.getPaymentStatus().name());
            dto.setServiceNames(order.getServices().stream().map(WorkshopService::getName).toList());
            return dto;
        }).toList();
    }

    public void updateRepairStatus(Long orderId, UpdateRepairStatusRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        RepairStatus status = RepairStatus.valueOf(request.getRepairStatus().toUpperCase());
        order.setRepairStatus(status);
        orderRepository.save(order);
    }

    public void updatePaymentStatus(Long orderId, UpdatePaymentStatusRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        PaymentStatus status = PaymentStatus.valueOf(request.getPaymentStatus().toUpperCase());
        order.setPaymentStatus(status);
        orderRepository.save(order);
    }
}
