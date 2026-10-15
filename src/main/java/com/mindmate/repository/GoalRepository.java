package com.mindmate.repository;

import com.mindmate.entity.Goal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface GoalRepository extends JpaRepository<Goal, Long> {
    List<Goal> findAllByOrderByCreatedAtDesc();
    List<Goal> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Goal> findByTargetDateGreaterThanEqualOrderByCreatedAtDesc(LocalDate date);
    long countByCompleted(boolean completed);
}
