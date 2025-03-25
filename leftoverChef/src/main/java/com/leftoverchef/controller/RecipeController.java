package com.leftoverchef.controller;

import com.leftoverchef.model.Recipe;
import com.leftoverchef.service.RecipeService;
import org.springframework.web.bind.annotation.*;

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
    public List<Recipe> findRecipesByIngredients(@RequestParam List<String> ingredients) {
        return recipeService.findRecipesByIngredients(ingredients);
    }
}
