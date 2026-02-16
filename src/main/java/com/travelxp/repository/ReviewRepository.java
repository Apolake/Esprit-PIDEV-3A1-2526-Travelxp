package com.travelxp.repository;

import com.travelxp.model.Booking;
import com.travelxp.model.Property;
import com.travelxp.model.Review;
import com.travelxp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    @EntityGraph(attributePaths = {"property", "reviewer", "reviewer.level", "booking", "booking.property", "booking.guest", "booking.guest.level"})
    List<Review> findAll();

    List<Review> findByProperty(Property property);
    List<Review> findByReviewer(User reviewer);
    Optional<Review> findByBooking(Booking booking);
    boolean existsByBooking(Booking booking);
    long countByReviewer(User reviewer);
    long countByPropertyAndRating(Property property, Integer rating);
}
