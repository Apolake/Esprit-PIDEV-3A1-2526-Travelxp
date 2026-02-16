package com.travelxp.repository;

import com.travelxp.model.Trip;
import com.travelxp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {
    List<Trip> findByUserOrderByStartDateAsc(User user);
    Optional<Trip> findByIdAndUser(Long id, User user);

    @EntityGraph(attributePaths = {"user", "user.level"})
    List<Trip> findAll();
}
