package com.mindmate.repository;

import com.mindmate.entity.CheckIn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CheckInRepository extends JpaRepository<CheckIn, Long> {
    List<CheckIn> findAllByOrderByCreatedAtDesc();
    List<CheckIn> findTop10ByOrderByCreatedAtDesc();
    List<CheckIn> findTop6ByOrderByCreatedAtAsc();
    List<CheckIn> findByUserIdOrderByCreatedAtDesc(Long userId);
}
