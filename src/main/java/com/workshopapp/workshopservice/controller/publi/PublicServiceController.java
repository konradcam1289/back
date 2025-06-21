package com.workshopapp.workshopservice.controller.publi;

import com.workshopapp.workshopservice.model.WorkshopService;
import com.workshopapp.workshopservice.repository.ServiceRepository;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;



@RestController
@RequestMapping("/api/services")
@CrossOrigin(origins = "http://localhost:5173")
public class PublicServiceController {

    private final ServiceRepository serviceRepository;

    public PublicServiceController(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    @GetMapping
    public List<WorkshopService> getAllServices() {
        return serviceRepository.findAll();
    }
}
