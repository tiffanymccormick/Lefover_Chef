package com.leftoverchef.backend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leftoverchef.backend.model.Recipe;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecipeService {

    private List<Recipe> recipes;

    // Load the JSON file when the application starts
    @PostConstruct
    public void init() {
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<List<Recipe>> typeReference = new TypeReference<List<Recipe>>() {};
        InputStream inputStream = getClass().getResourceAsStream("/recipes.json");
        try {
            recipes = mapper.readValue(inputStream, typeReference);
            System.out.println("Recipes loaded successfully.");
        } catch (IOException e) {
            System.out.println("Unable to load recipes: " + e.getMessage());
        }
    }

    // Method to match a recipe based on user-provided ingredients
    public Recipe matchRecipe(List<String> userIngredients) {
        Recipe bestMatch = null;
        int bestScore = 0;

        for (Recipe recipe : recipes) {
            int score = 0;
            // Assume recipe ingredients are stored as a comma-separated string
            List<String> recipeIngredients = Arrays.stream(recipe.getIngredients().split(","))
                                                     .map(String::trim)
                                                     .map(String::toLowerCase)
                                                     .collect(Collectors.toList());
            for (String ing : userIngredients) {
                if (recipeIngredients.contains(ing.toLowerCase().trim())) {
                    score++;
                }
            }
            if (score > bestScore) {
                bestScore = score;
                bestMatch = recipe;
            }
        }
        return bestMatch;
    }
}
