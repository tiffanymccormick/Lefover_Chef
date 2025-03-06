package com.leftoverchef.backend.service;

import com.leftoverchef.backend.model.MealLog;
import com.leftoverchef.backend.model.User;
import com.leftoverchef.backend.repository.MealLogRepository;
import com.leftoverchef.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MealLogService {

    @Autowired
    private MealLogRepository mealLogRepository;
    
    @Autowired
    private UserRepository userRepository;

    /**
     * Get all meal logs
     */
    public List<MealLog> getAllMealLogs() {
        return mealLogRepository.findAll();
    }

    /**
     * Get meal log by ID
     */
    public Optional<MealLog> getMealLogById(Long id) {
        return mealLogRepository.findById(id);
    }

    /**
     * Get meal logs by user
     */
    public List<MealLog> getMealLogsByUser(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            return mealLogRepository.findByUserOrderByCreatedAtDesc(userOpt.get());
        }
        return List.of();
    }

    /**
     * Save a meal log
     */
    public MealLog saveMealLog(MealLog mealLog) {
        // Update the user's food saved count
        User user = mealLog.getUser();
        user.incrementFoodSaved(mealLog.getIngredientsSaved());
        userRepository.save(user);
        
        // Save the meal log
        return mealLogRepository.save(mealLog);
    }

    /**
     * Delete a meal log
     */
    public void deleteMealLog(Long id) {
        mealLogRepository.deleteById(id);
    }
} 