package com.travelxp.service;

import com.travelxp.model.Review;
import com.travelxp.model.ReviewComment;
import com.travelxp.model.User;
import com.travelxp.repository.ReviewCommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReviewCommentService {

    @Autowired
    private ReviewCommentRepository reviewCommentRepository;

    public List<ReviewComment> getAllComments() {
        return reviewCommentRepository.findAll();
    }

    public List<ReviewComment> getCommentsForReview(Review review) {
        return reviewCommentRepository.findByReviewOrderByCreatedAtAsc(review);
    }

    public List<ReviewComment> getCommentsByUser(User user) {
        return reviewCommentRepository.findByCommenter(user);
    }

    @Transactional
    public ReviewComment addComment(Review review, User commenter, String commentText) {
        ReviewComment comment = new ReviewComment();
        comment.setReview(review);
        comment.setCommenter(commenter);
        comment.setComment(commentText);
        validate(comment);
        return reviewCommentRepository.save(comment);
    }

    @Transactional
    public ReviewComment saveExisting(ReviewComment comment) {
        validate(comment);
        return reviewCommentRepository.save(comment);
    }

    @Transactional
    public void deleteComment(Long id) {
        reviewCommentRepository.deleteById(id);
    }

    private void validate(ReviewComment comment) {
        if (comment.getReview() == null) {
            throw new IllegalArgumentException("Review is required.");
        }
        if (comment.getCommenter() == null) {
            throw new IllegalArgumentException("User is required to comment.");
        }
        if (comment.getComment() == null || comment.getComment().trim().isEmpty()) {
            throw new IllegalArgumentException("Comment cannot be empty.");
        }
        comment.setComment(comment.getComment().trim());
    }
}
