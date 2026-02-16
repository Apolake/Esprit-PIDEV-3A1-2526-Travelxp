package com.travelxp.service;

import com.travelxp.model.Booking;
import com.travelxp.model.Property;
import com.travelxp.model.Review;
import com.travelxp.model.User;
import com.travelxp.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private GamificationService gamificationService;

    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    public Optional<Review> getReviewById(Long id) {
        return reviewRepository.findById(id);
    }

    @Transactional
    public Review saveReview(Review review) {
        validateReview(review, review.getId());
        return reviewRepository.save(review);
    }

    public List<Review> getReviewsByProperty(Property property) {
        return reviewRepository.findByProperty(property);
    }

    public List<Review> getReviewsByReviewer(User reviewer) {
        return reviewRepository.findByReviewer(reviewer);
    }

    @Transactional
    public Review createReview(Booking booking, User reviewer, Property property,
                               Integer rating, String comment) {
        Review review = new Review();
        review.setBooking(booking);
        review.setReviewer(reviewer);
        review.setProperty(booking != null ? booking.getProperty() : property);
        review.setRating(rating);
        review.setComment(comment);
        review.setXpEarned(25);

        validateReview(review, null);
        Review savedReview = reviewRepository.save(review);

        gamificationService.awardExperiencePoints(reviewer, 25, "Review submitted");
        gamificationService.checkReviewAchievements(reviewer);

        return savedReview;
    }

    @Transactional
    public void deleteReview(Long id) {
        reviewRepository.deleteById(id);
    }

    private void validateReview(Review review, Long currentId) {
        if (review.getBooking() == null) {
            throw new IllegalArgumentException("Booking is required for a review.");
        }
        if (review.getReviewer() == null) {
            throw new IllegalArgumentException("Reviewer is required.");
        }
        if (review.getBooking().getGuest() == null || !review.getBooking().getGuest().equals(review.getReviewer())) {
            throw new IllegalArgumentException("Only the booking guest can submit a review.");
        }
        if (review.getProperty() == null) {
            throw new IllegalArgumentException("Property is required.");
        }
        if (review.getBooking().getProperty() != null && !review.getBooking().getProperty().equals(review.getProperty())) {
            review.setProperty(review.getBooking().getProperty());
        }
        if (review.getRating() == null || review.getRating() < 1 || review.getRating() > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5.");
        }

        if (currentId == null) {
            if (reviewRepository.existsByBooking(review.getBooking())) {
                throw new IllegalArgumentException("This booking already has a review.");
            }
        } else {
            reviewRepository.findByBooking(review.getBooking())
                    .filter(existing -> !existing.getId().equals(currentId))
                    .ifPresent(existing -> {
                        throw new IllegalArgumentException("This booking already has a review.");
                    });
        }
    }
}
