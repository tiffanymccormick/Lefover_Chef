package com.leftoverchef.backend.controller;

import com.leftoverchef.backend.model.MealLog;
import com.leftoverchef.backend.model.MealType;
import com.leftoverchef.backend.model.User;
import com.leftoverchef.backend.service.MealLogService;
import com.leftoverchef.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // Allow requests from any origin
public class MealLogController {

    @Autowired
    private MealLogService mealLogService;
    
    @Autowired
    private UserService userService;

    /**
     * Get all meal logs
     */
    @GetMapping("/meallogs")
    public ResponseEntity<List<MealLog>> getAllMealLogs() {
        List<MealLog> mealLogs = mealLogService.getAllMealLogs();
        return ResponseEntity.ok(mealLogs);
    }

    /**
     * Get meal log by ID
     */
    @GetMapping("/meallogs/{id}")
    public ResponseEntity<MealLog> getMealLogById(@PathVariable Long id) {
        Optional<MealLog> mealLog = mealLogService.getMealLogById(id);
        return mealLog.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Get meal logs by user
     */
    @GetMapping("/meallogs/user/{userId}")
    public ResponseEntity<List<MealLog>> getMealLogsByUser(@PathVariable Long userId) {
        List<MealLog> mealLogs = mealLogService.getMealLogsByUser(userId);
        return ResponseEntity.ok(mealLogs);
    }

    /**
     * Create a new meal log
     */
    @PostMapping("/meallogs")
    public ResponseEntity<MealLog> createMealLog(@RequestBody Map<String, Object> payload) {
        Long userId = Long.parseLong(payload.get("userId").toString());
        String recipeName = (String) payload.get("recipeName");
        String recipeId = (String) payload.get("recipeId");
        String mealTypeStr = (String) payload.get("mealType");
        Integer ingredientsSaved = (Integer) payload.get("ingredientsSaved");
        
        if (userId == null || recipeName == null || recipeId == null || mealTypeStr == null || ingredientsSaved == null) {
            return ResponseEntity.badRequest().build();
        }
        
        Optional<User> userOpt = userService.getUserById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        MealType mealType;
        try {
            mealType = MealType.valueOf(mealTypeStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
        
        MealLog mealLog = new MealLog(userOpt.get(), recipeName, recipeId, mealType, ingredientsSaved);
        MealLog savedMealLog = mealLogService.saveMealLog(mealLog);
        return ResponseEntity.ok(savedMealLog);
    }

    /**
     * Delete a meal log
     */
    @DeleteMapping("/meallogs/{id}")
    public ResponseEntity<Void> deleteMealLog(@PathVariable Long id) {
        Optional<MealLog> mealLog = mealLogService.getMealLogById(id);
        if (mealLog.isPresent()) {
            mealLogService.deleteMealLog(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
} 