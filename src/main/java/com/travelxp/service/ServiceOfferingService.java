package com.travelxp.service;

import com.travelxp.model.ServiceOffering;
import com.travelxp.repository.ServiceOfferingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ServiceOfferingService {

    @Autowired
    private ServiceOfferingRepository serviceOfferingRepository;

    public List<ServiceOffering> getAll() {
        return serviceOfferingRepository.findAll();
    }

    @Transactional
    public ServiceOffering save(ServiceOffering serviceOffering) {
        validate(serviceOffering);
        return serviceOfferingRepository.save(serviceOffering);
    }

    @Transactional
    public void delete(Long id) {
        serviceOfferingRepository.deleteById(id);
    }

    private void validate(ServiceOffering service) {
        if (service.getName() == null || service.getName().isBlank()) {
            throw new IllegalArgumentException("Service name is required.");
        }
        if (service.getPrice() == null || service.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Service price must be zero or greater.");
        }
    }
}
