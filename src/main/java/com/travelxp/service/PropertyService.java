package com.travelxp.service;

import com.travelxp.model.Property;
import com.travelxp.model.User;
import com.travelxp.repository.PropertyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class PropertyService {

    @Autowired
    private PropertyRepository propertyRepository;

    public List<Property> getAllProperties() {
        return propertyRepository.findByIsActiveTrue();
    }

    public Optional<Property> getPropertyById(Long id) {
        return propertyRepository.findById(id);
    }

    public List<Property> getPropertiesByOwner(User owner) {
        return propertyRepository.findByOwner(owner);
    }

    public List<Property> getPropertiesByCity(String city) {
        return propertyRepository.findByCityAndIsActiveTrue(city);
    }

    public List<Property> searchProperties(String city, BigDecimal minPrice, BigDecimal maxPrice) {
        if (city != null && !city.isEmpty()) {
            return propertyRepository.findByCityAndIsActiveTrue(city);
        } else if (minPrice != null && maxPrice != null) {
            return propertyRepository.findByPricePerNightBetween(minPrice, maxPrice);
        }
        return getAllProperties();
    }

    @Transactional
    public Property createProperty(Property property) {
        return propertyRepository.save(property);
    }

    @Transactional
    public Property updateProperty(Property property) {
        return propertyRepository.save(property);
    }

    @Transactional
    public void deleteProperty(Long id) {
        propertyRepository.deleteById(id);
    }
}
