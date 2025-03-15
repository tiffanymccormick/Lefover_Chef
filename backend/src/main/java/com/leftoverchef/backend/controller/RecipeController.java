package com.leftoverchef.backend.controller;

import com.leftoverchef.backend.model.Recipe;
import com.leftoverchef.backend.service.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class RecipeController {

    @Autowired
    private RecipeService recipeService;

    @PostMapping("/recipes")
    public ResponseEntity<Recipe> getRecipe(@RequestBody Map<String, Object> payload) {
        @SuppressWarnings("unchecked")
        List<String> userIngredients = (List<String>) payload.get("ingredients");
        Recipe recipe = recipeService.matchRecipe(userIngredients);
        
        if (recipe != null) {
            return ResponseEntity.ok(recipe);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/recipes/alternative")
    public ResponseEntity<Recipe> getAlternativeRecipe(@RequestBody Map<String, Object> payload) {
        @SuppressWarnings("unchecked")
        List<String> userIngredients = (List<String>) payload.get("ingredients");
        Recipe recipe = recipeService.getAlternativeRecipe(userIngredients);
        
        if (recipe != null) {
            return ResponseEntity.ok(recipe);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/food-saved")
    public ResponseEntity<Double> getTotalFoodSaved() {
        double totalPounds = recipeService.getTotalFoodSaved();
        return ResponseEntity.ok(totalPounds);
    }
}
