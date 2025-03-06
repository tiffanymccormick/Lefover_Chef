package com.leftoverchef.backend.controller;

import com.leftoverchef.backend.model.Recipe;
import com.leftoverchef.backend.model.MealType;
import com.leftoverchef.backend.service.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // Allow requests from any origin
public class RecipeController {

    @Autowired
    private RecipeService recipeService;

    /**
     * Get a recipe based on ingredients and meal type
     */
    @PostMapping("/recipes")
    public ResponseEntity<Recipe> getRecipe(@RequestBody Map<String, Object> payload) {
        List<String> userIngredients = (List<String>) payload.get("ingredients");
        MealType mealType = MealType.ANY;
        
        // Parse meal type if provided
        if (payload.containsKey("mealType")) {
            String mealTypeStr = (String) payload.get("mealType");
            try {
                mealType = MealType.valueOf(mealTypeStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Invalid meal type, default to ANY
                mealType = MealType.ANY;
            }
        }
        
        Recipe recipe = recipeService.matchRecipe(userIngredients, mealType);
        if (recipe != null) {
            return ResponseEntity.ok(recipe);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Get alternative recipes based on ingredients and meal type
     */
    @PostMapping("/recipes/alternatives")
    public ResponseEntity<List<Recipe>> getAlternativeRecipes(@RequestBody Map<String, Object> payload) {
        List<String> userIngredients = (List<String>) payload.get("ingredients");
        MealType mealType = MealType.ANY;
        int limit = 5; // Default limit
        
        // Parse meal type if provided
        if (payload.containsKey("mealType")) {
            String mealTypeStr = (String) payload.get("mealType");
            try {
                mealType = MealType.valueOf(mealTypeStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Invalid meal type, default to ANY
                mealType = MealType.ANY;
            }
        }
        
        // Parse limit if provided
        if (payload.containsKey("limit")) {
            limit = (int) payload.get("limit");
        }
        
        List<Recipe> recipes = recipeService.getAlternativeRecipes(userIngredients, mealType, limit);
        return ResponseEntity.ok(recipes);
    }
    
    /**
     * Get recipes by meal type
     */
    @GetMapping("/recipes/mealtype/{mealType}")
    public ResponseEntity<List<Recipe>> getRecipesByMealType(@PathVariable String mealType) {
        try {
            MealType type = MealType.valueOf(mealType.toUpperCase());
            List<Recipe> recipes = recipeService.getRecipesByMealType(type);
            return ResponseEntity.ok(recipes);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
