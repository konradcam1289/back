package com.workshopapp.workshopservice.service;

import com.workshopapp.workshopservice.model.WorkshopService;
import com.workshopapp.workshopservice.repository.ServiceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceService {

    private final ServiceRepository serviceRepository;

    public ServiceService(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    public List<WorkshopService> getAllServices() {
        return serviceRepository.findAll();
    }

    // 💡 NOWA METODA DO ZAPISYWANIA USŁUG
    public WorkshopService saveService(WorkshopService service) {
        return serviceRepository.save(service);
    }

    // 💡 NOWA METODA DO USUWANIA USŁUG
    public void deleteService(Long id) {
        serviceRepository.deleteById(id);
    }
}
