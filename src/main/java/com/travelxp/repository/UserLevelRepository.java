package com.travelxp.repository;

import com.travelxp.model.UserLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserLevelRepository extends JpaRepository<UserLevel, Long> {
    Optional<UserLevel> findByLevelNumber(Integer levelNumber);
    Optional<UserLevel> findTopByXpRequiredLessThanEqualOrderByXpRequiredDesc(Integer xp);
}
