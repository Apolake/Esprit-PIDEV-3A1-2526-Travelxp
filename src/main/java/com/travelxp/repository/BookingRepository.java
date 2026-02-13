package com.travelxp.repository;

import com.travelxp.model.Booking;
import com.travelxp.model.Property;
import com.travelxp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    @EntityGraph(attributePaths = {"property"})
    List<Booking> findByGuest(User guest);

    List<Booking> findByProperty(Property property);
    List<Booking> findByStatus(String status);

    @EntityGraph(attributePaths = {"property"})
    List<Booking> findByGuestOrderByCreatedAtDesc(User guest);
    long countByGuest(User guest);
}
