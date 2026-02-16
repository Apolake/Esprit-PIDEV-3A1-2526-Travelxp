package com.travelxp.repository;

import com.travelxp.model.Property;
import com.travelxp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {
    List<Property> findByOwner(User owner);
    List<Property> findByCity(String city);
    List<Property> findByCountry(String country);

    @Override
    @EntityGraph(attributePaths = {"owner", "owner.level", "reviews"})
    List<Property> findAll();

    @EntityGraph(attributePaths = {"reviews"})
    List<Property> findByIsActiveTrue();

    List<Property> findByPricePerNightBetween(BigDecimal minPrice, BigDecimal maxPrice);

    @EntityGraph(attributePaths = {"reviews"})
    List<Property> findByCityAndIsActiveTrue(String city);

    boolean existsByTitleIgnoreCaseAndAddressIgnoreCase(String title, String address);

    boolean existsByTitleIgnoreCaseAndAddressIgnoreCaseAndIdNot(String title, String address, Long id);
}
