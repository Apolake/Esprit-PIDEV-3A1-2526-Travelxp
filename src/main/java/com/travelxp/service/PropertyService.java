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

    public List<Property> getAllPropertiesAdmin() {
        return propertyRepository.findAll();
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
        validateProperty(property);
        if (propertyRepository.existsByTitleIgnoreCaseAndAddressIgnoreCase(property.getTitle().trim(), property.getAddress().trim())) {
            throw new IllegalArgumentException("A property with the same title and address already exists.");
        }
        return propertyRepository.save(property);
    }

    @Transactional
    public Property updateProperty(Property property) {
        validateProperty(property);
        if (property.getId() != null && propertyRepository.existsByTitleIgnoreCaseAndAddressIgnoreCaseAndIdNot(
                property.getTitle().trim(), property.getAddress().trim(), property.getId())) {
            throw new IllegalArgumentException("A property with the same title and address already exists.");
        }
        return propertyRepository.save(property);
    }

    @Transactional
    public void deleteProperty(Long id) {
        propertyRepository.deleteById(id);
    }

    private void validateProperty(Property property) {
        if (property.getOwner() == null) {
            throw new IllegalArgumentException("Owner is required.");
        }
        if (isBlank(property.getTitle()) || isBlank(property.getCity()) || isBlank(property.getCountry()) ||
            isBlank(property.getAddress()) || isBlank(property.getPropertyType())) {
            throw new IllegalArgumentException("Title, city, country, address, and type are required.");
        }
        if (property.getPricePerNight() == null || property.getPricePerNight().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price per night must be positive.");
        }
        if (property.getMaxGuests() == null || property.getMaxGuests() <= 0) {
            throw new IllegalArgumentException("Max guests must be positive.");
        }
        if (property.getBedrooms() != null && property.getBedrooms() < 0) {
            throw new IllegalArgumentException("Bedrooms cannot be negative.");
        }
        if (property.getBathrooms() != null && property.getBathrooms() < 0) {
            throw new IllegalArgumentException("Bathrooms cannot be negative.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
