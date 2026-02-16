package com.travelxp.repository;

import com.travelxp.model.Offer;
import com.travelxp.model.Property;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OfferRepository extends JpaRepository<Offer, Long> {
    @Override
    @EntityGraph(attributePaths = {"property", "property.owner", "property.owner.level"})
    List<Offer> findAll();

    List<Offer> findByPropertyAndIsActiveTrue(Property property);
    List<Offer> findByIsActiveTrueAndStartDateLessThanEqualAndEndDateGreaterThanEqual(LocalDate start, LocalDate end);
}
