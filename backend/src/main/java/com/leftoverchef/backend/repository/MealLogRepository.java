package com.leftoverchef.backend.repository;

import com.leftoverchef.backend.model.MealLog;
import com.leftoverchef.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MealLogRepository extends JpaRepository<MealLog, Long> {
    List<MealLog> findByUser(User user);
    List<MealLog> findByUserOrderByCreatedAtDesc(User user);
} 