package com.travelxp.repository;

import com.travelxp.model.Review;
import com.travelxp.model.ReviewComment;
import com.travelxp.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewCommentRepository extends JpaRepository<ReviewComment, Long> {
    @Override
    @EntityGraph(attributePaths = {"review", "review.property", "review.reviewer", "review.reviewer.level", "commenter", "commenter.level"})
    List<ReviewComment> findAll();

    List<ReviewComment> findByReviewOrderByCreatedAtAsc(Review review);
    List<ReviewComment> findByCommenter(User commenter);
}
