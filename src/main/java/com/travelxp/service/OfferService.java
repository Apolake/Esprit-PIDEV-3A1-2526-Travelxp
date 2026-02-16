package com.travelxp.service;

import com.travelxp.model.Offer;
import com.travelxp.repository.OfferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class OfferService {

    @Autowired
    private OfferRepository offerRepository;

    public List<Offer> getAll() {
        return offerRepository.findAll();
    }

    @Transactional
    public Offer save(Offer offer) {
        validate(offer);
        return offerRepository.save(offer);
    }

    @Transactional
    public void delete(Long id) {
        offerRepository.deleteById(id);
    }

    private void validate(Offer offer) {
        if (offer.getProperty() == null) {
            throw new IllegalArgumentException("Property is required.");
        }
        if (offer.getTitle() == null || offer.getTitle().isBlank()) {
            throw new IllegalArgumentException("Title is required.");
        }
        BigDecimal discount = offer.getDiscountPercentage();
        if (discount == null || discount.compareTo(BigDecimal.ZERO) < 0 || discount.compareTo(new BigDecimal("100")) > 0) {
            throw new IllegalArgumentException("Discount must be between 0 and 100.");
        }
        LocalDate start = offer.getStartDate();
        LocalDate end = offer.getEndDate();
        if (start == null || end == null) {
            throw new IllegalArgumentException("Start and end dates are required.");
        }
        if (end.isBefore(start)) {
            throw new IllegalArgumentException("End date must be on or after start date.");
        }
    }
}
