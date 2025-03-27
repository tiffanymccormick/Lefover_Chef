package com.leftoverchef.controller;

import com.leftoverchef.model.Recipe;
import com.leftoverchef.service.RecipeService;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/recipes")
@CrossOrigin(origins = "*")
public class RecipeController {
    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @GetMapping
    public List<Recipe> getAllRecipes() {
        return recipeService.getAllRecipes();
    }

    @GetMapping("/{id}")
    public Recipe getRecipeById(@PathVariable String id) {
        return recipeService.getRecipeById(id);
    }

    @GetMapping("/search")
    public List<Recipe> findRecipesByIngredients(
            @RequestParam List<String> ingredients,
            @RequestParam(required = false) List<String> excludeIds) {
        // Initialize empty list if excludeIds is null
        List<String> excludeList = excludeIds != null ? excludeIds : new ArrayList<>();
        
        // Get all matching recipes sorted by score
        List<Recipe> matchedRecipes = recipeService.findRecipesByIngredients(ingredients, excludeList);
        
        // Return only the first recipe if there are matches
        return !matchedRecipes.isEmpty() ? List.of(matchedRecipes.get(0)) : new ArrayList<>();
    }
}
