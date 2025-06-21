package com.workshopapp.workshopservice.service;

import com.workshopapp.workshopservice.model.WorkshopService;
import com.workshopapp.workshopservice.repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GptService {

    @Value("${openai.api.key}")
    private String openaiApiKey;

    private final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";

    private final RestTemplate restTemplate;
    private final ServiceRepository serviceRepository;

    public GptService(RestTemplate restTemplate, ServiceRepository serviceRepository) {
        this.restTemplate = restTemplate;
        this.serviceRepository = serviceRepository;
    }

    public String getSuggestedService(String description) {
        List<WorkshopService> services = serviceRepository.findByAvailableTrue();

        String serviceList = services.stream()
                .map(s -> "- " + s.getName() + ": " + s.getDescription())
                .collect(Collectors.joining("\n"));

        String prompt = """
                Jesteś doradcą w warsztacie samochodowym. 
                Na podstawie poniższej listy usług i opisu problemu klienta, zaproponuj jedną najlepiej pasującą usługę (tylko nazwę z listy):

                Lista usług:
                %s

                Opis klienta:
                %s

                Odpowiedź sformatuj tylko jako nazwę jednej usługi.
                """.formatted(serviceList, description);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openaiApiKey);

        Map<String, Object> body = new HashMap<>();
        body.put("model", "gpt-3.5-turbo");
        body.put("messages", List.of(
                Map.of("role", "system", "content", "Jesteś doradcą w warsztacie samochodowym."),
                Map.of("role", "user", "content", prompt)
        ));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(OPENAI_URL, request, Map.class);

        List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
        return message.get("content").toString().trim();
    }
}
