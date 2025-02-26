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
public class RecipeController {

    @Autowired
    private RecipeService recipeService;

    // Endpoint to receive a list of ingredients and return a matching recipe
    @PostMapping("/recipes")
    public ResponseEntity<Recipe> getRecipe(@RequestBody Map<String, List<String>> payload) {
        List<String> ingredients = payload.get("ingredients");
        Recipe recipe = recipeService.matchRecipe(ingredients);
        if (recipe != null) {
            return ResponseEntity.ok(recipe);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}