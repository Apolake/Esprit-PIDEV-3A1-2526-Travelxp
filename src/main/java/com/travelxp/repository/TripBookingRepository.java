package com.travelxp.repository;

import com.travelxp.model.Booking;
import com.travelxp.model.Trip;
import com.travelxp.model.TripBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TripBookingRepository extends JpaRepository<TripBooking, Long> {
    List<TripBooking> findByTrip(Trip trip);

    @EntityGraph(attributePaths = {"booking", "booking.property"})
    List<TripBooking> findWithBookingAndPropertyByTrip(Trip trip);

    Optional<TripBooking> findByTripAndBooking(Trip trip, Booking booking);
}
