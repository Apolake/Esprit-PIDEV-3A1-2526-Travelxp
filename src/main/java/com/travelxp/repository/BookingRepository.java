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

    @Override
    @EntityGraph(attributePaths = {"property", "guest", "guest.level"})
    List<Booking> findAll();

    List<Booking> findByProperty(Property property);
    List<Booking> findByStatus(String status);

    @EntityGraph(attributePaths = {"property", "guest"})
    @org.springframework.data.jpa.repository.Query("select distinct b from Booking b join fetch b.property p join fetch b.guest g where b.guest = :guest order by b.createdAt desc")
    List<Booking> findByGuestOrderByCreatedAtDesc(User guest);
    long countByGuest(User guest);

    boolean existsByPropertyAndGuestAndCheckInDateAndCheckOutDate(Property property, User guest, java.time.LocalDate checkIn, java.time.LocalDate checkOut);

    boolean existsByPropertyAndGuestAndCheckInDateAndCheckOutDateAndIdNot(Property property, User guest, java.time.LocalDate checkIn, java.time.LocalDate checkOut, Long id);
}
