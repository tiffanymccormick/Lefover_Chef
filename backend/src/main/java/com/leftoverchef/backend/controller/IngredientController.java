package com.leftoverchef.backend.controller;

import com.leftoverchef.backend.model.Ingredient;
import com.leftoverchef.backend.model.Ingredient.IngredientCategory;
import com.leftoverchef.backend.service.IngredientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // Allow requests from any origin
public class IngredientController {

    @Autowired
    private IngredientService ingredientService;

    /**
     * Get all ingredients
     */
    @GetMapping("/ingredients")
    public ResponseEntity<List<Ingredient>> getAllIngredients() {
        List<Ingredient> ingredients = ingredientService.getAllIngredients();
        return ResponseEntity.ok(ingredients);
    }

    /**
     * Get ingredients by category
     */
    @GetMapping("/ingredients/category/{category}")
    public ResponseEntity<List<Ingredient>> getIngredientsByCategory(@PathVariable String category) {
        try {
            IngredientCategory ingredientCategory = IngredientCategory.valueOf(category.toUpperCase());
            List<Ingredient> ingredients = ingredientService.getIngredientsByCategory(ingredientCategory);
            return ResponseEntity.ok(ingredients);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get all ingredient categories
     */
    @GetMapping("/ingredients/categories")
    public ResponseEntity<List<String>> getIngredientCategories() {
        List<String> categories = Arrays.stream(IngredientCategory.values())
                .map(Enum::name)
                .collect(Collectors.toList());
        return ResponseEntity.ok(categories);
    }

    /**
     * Add a new ingredient
     */
    @PostMapping("/ingredients")
    public ResponseEntity<Ingredient> addIngredient(@RequestBody Map<String, String> payload) {
        String name = payload.get("name");
        String category = payload.get("category");
        
        if (name == null || category == null) {
            return ResponseEntity.badRequest().build();
        }
        
        try {
            IngredientCategory ingredientCategory = IngredientCategory.valueOf(category.toUpperCase());
            Ingredient ingredient = new Ingredient(name, ingredientCategory);
            Ingredient savedIngredient = ingredientService.saveIngredient(ingredient);
            return ResponseEntity.ok(savedIngredient);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Categorize ingredients
     */
    @PostMapping("/ingredients/categorize")
    public ResponseEntity<List<Ingredient>> categorizeIngredients(@RequestBody Map<String, List<String>> payload) {
        List<String> ingredientNames = payload.get("ingredients");
        
        if (ingredientNames == null || ingredientNames.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        List<Ingredient> categorizedIngredients = ingredientNames.stream()
                .map(name -> {
                    Optional<Ingredient> existingIngredient = ingredientService.getIngredientByName(name);
                    if (existingIngredient.isPresent()) {
                        return existingIngredient.get();
                    } else {
                        IngredientCategory category = ingredientService.categorizeIngredient(name);
                        return new Ingredient(name, category);
                    }
                })
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(categorizedIngredients);
    }
} 