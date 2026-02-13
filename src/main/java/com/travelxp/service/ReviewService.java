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
        review.setProperty(property);
        review.setRating(rating);
        review.setComment(comment);
        review.setXpEarned(25);

        Review savedReview = reviewRepository.save(review);

        gamificationService.awardExperiencePoints(reviewer, 25, "Review submitted");
        gamificationService.checkReviewAchievements(reviewer);

        return savedReview;
    }

    @Transactional
    public void deleteReview(Long id) {
        reviewRepository.deleteById(id);
    }
}
