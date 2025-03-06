package com.leftoverchef.backend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leftoverchef.backend.model.Recipe;
import com.leftoverchef.backend.model.MealType;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecipeService {

    // List that holds all recipes loaded from the JSON file
    private List<Recipe> recipes;

    // Once the service is created, it loads the recipes from the JSON file into the recipes list
    @PostConstruct
    public void init() {
        ObjectMapper mapper = new ObjectMapper();
        // Type reference for list of Recipe objects
        TypeReference<List<Recipe>> typeReference = new TypeReference<List<Recipe>>() {};
        InputStream inputStream = getClass().getResourceAsStream("/recipes_ingredients.json");
        try {
            recipes = mapper.readValue(inputStream, typeReference);
            System.out.println("Recipes loaded successfully: " + recipes.size() + " recipes");
            
            // Determine meal type for each recipe
            for (Recipe recipe : recipes) {
                recipe.determineMealType();
            }
        } catch (IOException e) {
            System.out.println("Unable to load recipes: " + e.getMessage());
            recipes = new ArrayList<>();
        }
    }

    /**
     * Get all recipes
     */
    public List<Recipe> getAllRecipes() {
        return recipes;
    }

    /**
     * Get recipes by meal type
     */
    public List<Recipe> getRecipesByMealType(MealType mealType) {
        return recipes.stream()
                .filter(recipe -> recipe.getMealType() == mealType || recipe.getMealType() == MealType.ANY)
                .collect(Collectors.toList());
    }

    /**
     * Match a recipe based on user ingredients and meal type
     */
    public Recipe matchRecipe(List<String> userIngredients, MealType mealType) {
        List<Recipe> filteredRecipes;
        
        // Filter by meal type if specified
        if (mealType != null && mealType != MealType.ANY) {
            filteredRecipes = recipes.stream()
                    .filter(recipe -> recipe.getMealType() == mealType || recipe.getMealType() == MealType.ANY)
                    .collect(Collectors.toList());
        } else {
            filteredRecipes = recipes;
        }
        
        Recipe bestRecipe = null;
        double bestScore = 0.0;
        
        // Loop through each recipe to compute a match score.
        for (Recipe recipe : filteredRecipes) {
            double score = computeScore(userIngredients, recipe);
            if (score > bestScore) {
                bestScore = score;
                bestRecipe = recipe;
            }
        }
        
        return bestRecipe;
    }

    /**
     * Get alternative recipes based on user ingredients and meal type
     */
    public List<Recipe> getAlternativeRecipes(List<String> userIngredients, MealType mealType, int limit) {
        List<Recipe> filteredRecipes;
        
        // Filter by meal type if specified
        if (mealType != null && mealType != MealType.ANY) {
            filteredRecipes = recipes.stream()
                    .filter(recipe -> recipe.getMealType() == mealType || recipe.getMealType() == MealType.ANY)
                    .collect(Collectors.toList());
        } else {
            filteredRecipes = recipes;
        }
        
        // Compute scores for all recipes
        return filteredRecipes.stream()
                .map(recipe -> {
                    recipe.setScore(computeScore(userIngredients, recipe));
                    return recipe;
                })
                .sorted(Comparator.comparingDouble(Recipe::getScore).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Compute a match score for one recipe
     * The score is the number of matching user ingredients divided by the total number of recipe ingredients.
     */
    private double computeScore(List<String> userIngredients, Recipe recipe) {
        int matches = 0;
        int totalIngredients = 0;
        
        // Get the list of cleaned ingredients from the recipe.
        List<String> recipeIngs = recipe.getCleanedIngredients();
        if (recipeIngs == null || recipeIngs.isEmpty()) {
            return 0.0;
        }
        
        totalIngredients = recipeIngs.size();
        
        // For each user-provided ingredient, check if it appears in the recipe's ingredients.
        for (String userIng : userIngredients) {
            for (String recipeIng : recipeIngs) {
                // Convert both strings to lowercase for case-insensitive comparison.
                if (recipeIng.toLowerCase().contains(userIng.toLowerCase())) {
                    matches++;
                    break; // Move to the next user ingredient after a match is found.
                }
            }
        }
        
        // Calculate normalized score: matches divided by total number of ingredients.
        double matchRatio = (double) matches / userIngredients.size();
        double coverageRatio = (double) matches / totalIngredients;
        
        // Combine both ratios with weights
        return (0.7 * matchRatio) + (0.3 * coverageRatio);
    }
}
