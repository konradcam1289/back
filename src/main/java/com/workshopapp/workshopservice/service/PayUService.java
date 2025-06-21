package com.workshopapp.workshopservice.service;

import com.workshopapp.workshopservice.config.PayUConfig;
import com.workshopapp.workshopservice.model.Order;
import com.workshopapp.workshopservice.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.Map;

@Service
public class PayUService {

    private final PayUConfig payUConfig;
    private final OrderRepository orderRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    public PayUService(PayUConfig payUConfig, OrderRepository orderRepository) {
        this.payUConfig = payUConfig;
        this.orderRepository = orderRepository;
    }

    public String createOrder(Long orderId) {
        // Pobranie zamówienia z bazy
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Pobranie tokenu dostępowego z PayU
        String accessToken = getAccessToken();

        // Przygotowanie danych do wysyłki zamówienia
        Map<String, Object> body = new HashMap<>();
        body.put("notifyUrl", "http://localhost:8080/api/payments/notify");
        body.put("continueUrl", payUConfig.getContinueUrl());  // <-- kluczowy element
        body.put("customerIp", "127.0.0.1");
        body.put("merchantPosId", payUConfig.getPosId());
        body.put("description", "Zamówienie warsztatowe");
        body.put("currencyCode", "PLN");
        body.put("totalAmount", String.valueOf(calculateOrderAmount(order)));
        body.put("extOrderId", orderId.toString());

        Map<String, String> buyer = new HashMap<>();
        buyer.put("email", order.getUser().getEmail());
        body.put("buyer", buyer);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity("https://secure.snd.payu.com/api/v2_1/orders", request, Map.class);

        Map<String, Object> responseBody = response.getBody();
        if (responseBody != null && responseBody.containsKey("redirectUri")) {
            return responseBody.get("redirectUri").toString();
        }
        throw new RuntimeException("Brak redirectUri w odpowiedzi PayU");
    }

    private String getAccessToken() {
        String url = "https://secure.snd.payu.com/pl/standard/user/oauth/authorize";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "client_credentials");
        map.add("client_id", payUConfig.getClientId());
        map.add("client_secret", payUConfig.getClientSecret());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
        Map<String, Object> responseBody = response.getBody();

        if (responseBody != null && responseBody.containsKey("access_token")) {
            return responseBody.get("access_token").toString();
        }
        throw new RuntimeException("Brak access_token w odpowiedzi PayU");
    }

    private int calculateOrderAmount(Order order) {
        return order.getServices().stream()
                .mapToInt(service -> (int) (service.getPrice() * 100)) // PayU wymaga wartości w groszach!
                .sum();
    }
}
