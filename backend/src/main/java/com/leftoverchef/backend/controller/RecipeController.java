package com.leftoverchef.backend.controller;

import com.leftoverchef.backend.model.Recipe;
import com.leftoverchef.backend.service.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class RecipeController {
    private static final Logger logger = LoggerFactory.getLogger(RecipeController.class);

    @Autowired
    private RecipeService recipeService;

    @PostMapping(value = "/recipes", consumes = "application/json")
    public ResponseEntity<Recipe> getRecipe(@RequestBody Map<String, Object> payload) {
        logger.info("Received recipe request with payload: {}", payload);
        
        @SuppressWarnings("unchecked")
        List<String> userIngredients = (List<String>) payload.get("ingredients");
        logger.info("Extracted ingredients: {}", userIngredients);
        
        if (userIngredients == null || userIngredients.isEmpty()) {
            logger.warn("No ingredients provided in request");
            return ResponseEntity.badRequest().build();
        }
        
        Recipe recipe = recipeService.matchRecipe(userIngredients);
        logger.info("Found recipe: {}", recipe);
        
        if (recipe != null) {
            return ResponseEntity.ok(recipe);
        } else {
            logger.warn("No recipe found");
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping(value = "/recipes/alternative", consumes = "application/json")
    public ResponseEntity<Recipe> getAlternativeRecipe(@RequestBody Map<String, Object> payload) {
        logger.info("Received alternative recipe request with payload: {}", payload);
        
        @SuppressWarnings("unchecked")
        List<String> userIngredients = (List<String>) payload.get("ingredients");
        logger.info("Extracted ingredients for alternative: {}", userIngredients);
        
        if (userIngredients == null || userIngredients.isEmpty()) {
            logger.warn("No ingredients provided in alternative request");
            return ResponseEntity.badRequest().build();
        }
        
        Recipe recipe = recipeService.getAlternativeRecipe(userIngredients);
        logger.info("Found alternative recipe: {}", recipe);
        
        if (recipe != null) {
            return ResponseEntity.ok(recipe);
        } else {
            logger.warn("No alternative recipe found");
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/food-saved")
    public ResponseEntity<Double> getTotalFoodSaved() {
        logger.info("Getting total food saved");
        double totalPounds = recipeService.getTotalFoodSaved();
        logger.info("Total food saved: {} lbs", totalPounds);
        return ResponseEntity.ok(totalPounds);
    }
}
