package com.travelxp.repository;

import com.travelxp.model.Activity;
import com.travelxp.model.Trip;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {
    @EntityGraph(attributePaths = {"trip", "trip.user", "trip.user.level"})
    List<Activity> findByTripOrderByActivityDateAsc(Trip trip);

    @Override
    @EntityGraph(attributePaths = {"trip", "trip.user", "trip.user.level"})
    List<Activity> findAll();
}
