package com.leftoverchef.backend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leftoverchef.backend.model.Recipe;
import com.leftoverchef.backend.model.MealType;
import com.leftoverchef.backend.util.IngredientWeightCalculator;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;

@Service
public class RecipeService {

    // List that holds all recipes loaded from the JSON file
    private List<Recipe> recipes;

    // For testing purposes
    void setRecipes(List<Recipe> recipes) {
        this.recipes = recipes;
    }

    // Once the service is created, it loads the recipes from the JSON file into the recipes list
    @PostConstruct
    public void init() {
        ObjectMapper mapper = new ObjectMapper();
        // Type reference for list of Recipe objects
        TypeReference<List<Recipe>> typeReference = new TypeReference<List<Recipe>>() {};
        InputStream inputStream = getClass().getResourceAsStream("/recipes_ingredients.json");
        try {
            recipes = mapper.readValue(inputStream, typeReference);
            System.out.println("Successfully loaded " + recipes.size() + " recipes");
            
            // Determine meal type for each recipe
            recipes.forEach(Recipe::determineMealType);
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
        // Filter recipes by meal type if specified
        List<Recipe> filteredRecipes;
        if (mealType != null && mealType != MealType.ANY) {
            filteredRecipes = recipes.stream()
                    .filter(recipe -> recipe.getMealType() == mealType || recipe.getMealType() == MealType.ANY)
                    .collect(Collectors.toList());
        } else {
            filteredRecipes = recipes;
        }

        Recipe bestRecipe = null;
        double bestScore = 0.0;

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
        // Filter recipes by meal type if specified
        List<Recipe> filteredRecipes;
        if (mealType != null && mealType != MealType.ANY) {
            filteredRecipes = recipes.stream()
                    .filter(recipe -> recipe.getMealType() == mealType || recipe.getMealType() == MealType.ANY)
                    .collect(Collectors.toList());
        } else {
            filteredRecipes = recipes;
        }

        // Compute scores and sort recipes
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
        List<String> recipeIngredients = recipe.getCleanedIngredients();
        if (recipeIngredients == null || recipeIngredients.isEmpty()) {
            return 0.0;
        }

        int matches = 0;
        double totalWeight = 0.0;

        // Calculate matches and total weight
        for (String userIng : userIngredients) {
            for (String recipeIng : recipeIngredients) {
                if (recipeIng.toLowerCase().contains(userIng.toLowerCase())) {
                    matches++;
                    totalWeight += IngredientWeightCalculator.calculateIngredientWeight(recipeIng);
                    break;
                }
            }
        }

        // Calculate match and coverage ratios
        double matchRatio = (double) matches / userIngredients.size();
        double coverageRatio = (double) matches / recipeIngredients.size();

        // Apply cooking method modifier
        String recipeType = recipe.getMealType().toString().toLowerCase();
        double modifier = COOKING_MODIFIERS.getOrDefault(recipeType, 1.0);
        totalWeight *= modifier;

        // Set the estimated weight for the recipe (convert to String with 2 decimal places)
        double finalWeight = Math.min(Math.max(totalWeight, 0.25), 10.0);
        recipe.setEstimatedPounds(String.format("%.2f", finalWeight));

        // Return weighted score
        return (0.7 * matchRatio) + (0.3 * coverageRatio);
    }

    private static final Map<String, Double> COOKING_MODIFIERS = new HashMap<>();
    
    static {
        COOKING_MODIFIERS.put("soup", 1.2);      // accounts for added water
        COOKING_MODIFIERS.put("stew", 1.1);      // accounts for reduced liquid
        COOKING_MODIFIERS.put("roast", 0.85);    // accounts for moisture loss
        COOKING_MODIFIERS.put("baked", 0.9);     // accounts for moisture loss
        COOKING_MODIFIERS.put("fried", 0.8);     // accounts for oil absorption
        COOKING_MODIFIERS.put("salad", 1.0);     // no modification
        COOKING_MODIFIERS.put("cocktail", 0.5);  // standard drink weight
        COOKING_MODIFIERS.put("dessert", 0.85);  // accounts for cooking loss
        COOKING_MODIFIERS.put("pasta", 1.8);     // accounts for water absorption
        COOKING_MODIFIERS.put("sandwich", 1.0);  // no modification
        COOKING_MODIFIERS.put("breakfast", 0.9); // slight moisture loss
    }
}
