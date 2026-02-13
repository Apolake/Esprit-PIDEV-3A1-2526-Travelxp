package com.travelxp.repository;

import com.travelxp.model.Property;
import com.travelxp.model.Review;
import com.travelxp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProperty(Property property);
    List<Review> findByReviewer(User reviewer);
    long countByReviewer(User reviewer);
    long countByPropertyAndRating(Property property, Integer rating);
}
