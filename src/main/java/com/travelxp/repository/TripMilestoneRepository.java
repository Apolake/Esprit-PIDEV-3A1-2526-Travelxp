package com.travelxp.repository;

import com.travelxp.model.Trip;
import com.travelxp.model.TripMilestone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TripMilestoneRepository extends JpaRepository<TripMilestone, Long> {
    List<TripMilestone> findByTripOrderByCreatedAtAsc(Trip trip);

    @EntityGraph(attributePaths = {"trip"})
    List<TripMilestone> findAll();
}
