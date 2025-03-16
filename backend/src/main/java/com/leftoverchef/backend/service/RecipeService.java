package com.leftoverchef.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.leftoverchef.backend.model.Recipe;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecipeService {
    private static final Logger logger = LoggerFactory.getLogger(RecipeService.class);
    private List<Recipe> recipes;
    private Set<String> usedRecipeIds = new HashSet<>();
    private double totalFoodSaved = 0.0;

    // For testing purposes
    void setRecipes(List<Recipe> recipes) {
        this.recipes = recipes;
        this.usedRecipeIds.clear();
        this.totalFoodSaved = 0.0;
    }

    @PostConstruct
    public void init() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            InputStream inputStream = getClass().getResourceAsStream("/cleaned_recipe_data.json");
            if (inputStream == null) {
                System.out.println("Recipe data file not found, falling back to original file");
                inputStream = getClass().getResourceAsStream("/Final_Updated_Recipe_Data_with_weights.json");
            }
            
            if (inputStream == null) {
                System.out.println("No recipe data file found");
                recipes = new ArrayList<>();
                return;
            }

            // First read as JsonNode to handle potential wrapper object
            JsonNode rootNode = mapper.readTree(inputStream);
            JsonNode recipesNode = rootNode.has("recipes") ? rootNode.get("recipes") : rootNode;
            
            recipes = new ArrayList<>();
            int totalRecipes = 0;
            int validRecipes = 0;
            
            for (JsonNode recipeNode : recipesNode) {
                totalRecipes++;
                try {
                    Recipe recipe = mapper.treeToValue(recipeNode, Recipe.class);
                    if (recipe != null && recipe.getTitle() != null && !recipe.getTitle().isEmpty() 
                        && recipe.getCleanedIngredients() != null && !recipe.getCleanedIngredients().isEmpty()) {
                        recipes.add(recipe);
                        validRecipes++;
                    }
                } catch (Exception e) {
                    System.out.println("Error parsing recipe: " + e.getMessage());
                }
            }
            
            System.out.println("Recipe loading summary:");
            System.out.println("Total recipes in file: " + totalRecipes);
            System.out.println("Valid recipes loaded: " + validRecipes);
            
            // Print first few recipes and their ingredients for debugging
            System.out.println("\nSample recipes:");
            for (int i = 0; i < Math.min(5, recipes.size()); i++) {
                Recipe recipe = recipes.get(i);
                System.out.println("\nRecipe " + (i+1) + ": " + recipe.getTitle());
                System.out.println("Ingredients: " + recipe.getCleanedIngredients());
            }
        } catch (IOException e) {
            System.out.println("Unable to load recipes: " + e.getMessage());
            e.printStackTrace();
            recipes = new ArrayList<>();
        }
    }

    public Recipe matchRecipe(List<String> userIngredients) {
        if (userIngredients == null || userIngredients.isEmpty() || recipes == null || recipes.isEmpty()) {
            logger.warn("Invalid input or no recipes available");
            return recipes != null && !recipes.isEmpty() ? recipes.get(0) : null;
        }

        logger.info("Matching recipe for ingredients: {}", userIngredients);
        
        // Preprocess user ingredients
        List<String> processedUserIngredients = userIngredients.stream()
            .map(ing -> ing.toLowerCase().trim())
            .filter(ing -> !ing.isEmpty())
            .collect(Collectors.toList());

        logger.info("Processed user ingredients: {}", processedUserIngredients);

        // Get all recipes with their scores
        List<Recipe> scoredRecipes = recipes.stream()
            .filter(recipe -> !usedRecipeIds.contains(recipe.getRecipeIndex()))
            .map(recipe -> {
                double score = computeScore(processedUserIngredients, recipe);
                recipe.setScore(score);
                logger.debug("Recipe: {} | Score: {} | Ingredients: {}", 
                    recipe.getTitle(), score, recipe.getCleanedIngredients());
                return recipe;
            })
            .sorted(Comparator.comparingDouble(Recipe::getScore).reversed())
            .collect(Collectors.toList());

        logger.info("Found {} potential matches", scoredRecipes.size());

        // If all recipes have been used or no matches found, reset and try again
        if (scoredRecipes.isEmpty()) {
            logger.info("No matches found or all recipes used, resetting used recipes set");
            usedRecipeIds.clear();
            scoredRecipes = new ArrayList<>(recipes);
            scoredRecipes.forEach(recipe -> {
                double score = computeScore(processedUserIngredients, recipe);
                recipe.setScore(score);
                logger.debug("Recipe after reset: {} | Score: {} | Ingredients: {}", 
                    recipe.getTitle(), score, recipe.getCleanedIngredients());
            });
            scoredRecipes.sort(Comparator.comparingDouble(Recipe::getScore).reversed());
        }

        // Always return a recipe, even if it's a poor match
        Recipe bestRecipe = scoredRecipes.get(0);
        logger.info("Best match selected: {} | Score: {} | Ingredients: {}", 
            bestRecipe.getTitle(), bestRecipe.getScore(), bestRecipe.getCleanedIngredients());
        
        usedRecipeIds.add(bestRecipe.getRecipeIndex());
        updateTotalFoodSaved(bestRecipe);
        
        return bestRecipe;
    }

    public Recipe getAlternativeRecipe(List<String> userIngredients) {
        if (userIngredients == null || userIngredients.isEmpty() || recipes == null || recipes.isEmpty()) {
            logger.warn("Invalid input or no recipes available for alternative");
            return recipes != null && !recipes.isEmpty() ? recipes.get(0) : null;
        }

        logger.info("Getting alternative recipe for ingredients: {}", userIngredients);
        
        // Preprocess user ingredients
        List<String> processedUserIngredients = userIngredients.stream()
            .map(ing -> ing.toLowerCase().trim())
            .filter(ing -> !ing.isEmpty())
            .collect(Collectors.toList());

        logger.info("Processed user ingredients: {}", processedUserIngredients);

        // Get all recipes with their scores, excluding the last matched recipe
        List<Recipe> scoredRecipes = recipes.stream()
            .filter(recipe -> !usedRecipeIds.contains(recipe.getRecipeIndex()))
            .map(recipe -> {
                double score = computeScore(processedUserIngredients, recipe);
                recipe.setScore(score);
                logger.debug("Alternative recipe: {} | Score: {} | Ingredients: {}", 
                    recipe.getTitle(), score, recipe.getCleanedIngredients());
                return recipe;
            })
            .sorted(Comparator.comparingDouble(Recipe::getScore).reversed())
            .collect(Collectors.toList());

        logger.info("Found {} potential alternative matches", scoredRecipes.size());

        // If no unused recipes, reset and try again but keep the last used recipe excluded
        if (scoredRecipes.isEmpty()) {
            logger.info("No unused recipes found, resetting");
            Set<String> lastUsed = new HashSet<>(usedRecipeIds);
            usedRecipeIds.clear();
            
            scoredRecipes = recipes.stream()
                .filter(recipe -> !lastUsed.contains(recipe.getRecipeIndex()))
                .map(recipe -> {
                    double score = computeScore(processedUserIngredients, recipe);
                    recipe.setScore(score);
                    logger.debug("Alternative recipe after reset: {} | Score: {} | Ingredients: {}", 
                        recipe.getTitle(), score, recipe.getCleanedIngredients());
                    return recipe;
                })
                .sorted(Comparator.comparingDouble(Recipe::getScore).reversed())
                .collect(Collectors.toList());
        }

        // Always return a recipe, even if it's a poor match
        if (!scoredRecipes.isEmpty()) {
            Recipe alternativeRecipe = scoredRecipes.get(0);
            logger.info("Best alternative selected: {} | Score: {} | Ingredients: {}", 
                alternativeRecipe.getTitle(), alternativeRecipe.getScore(), alternativeRecipe.getCleanedIngredients());
            usedRecipeIds.add(alternativeRecipe.getRecipeIndex());
            updateTotalFoodSaved(alternativeRecipe);
            return alternativeRecipe;
        }

        // If we somehow have no recipes left, return any recipe except the last used
        logger.warn("No recipes left, returning first available");
        return recipes.stream()
            .filter(recipe -> !usedRecipeIds.contains(recipe.getRecipeIndex()))
            .findFirst()
            .orElse(recipes.get(0));
    }

    private double computeScore(List<String> userIngredients, Recipe recipe) {
        if (recipe.getCleanedIngredients() == null || recipe.getCleanedIngredients().isEmpty()) {
            logger.debug("Recipe has no ingredients: {}", recipe.getTitle());
            return 0.0;
        }

        List<String> recipeIngredients = recipe.getCleanedIngredients().stream()
            .map(String::toLowerCase)
            .collect(Collectors.toList());

        long matchCount = userIngredients.stream()
            .filter(userIng -> recipeIngredients.stream()
                .anyMatch(recipeIng -> recipeIng.contains(userIng)))
            .count();

        double score = (double) matchCount / recipeIngredients.size();
        logger.debug("Score calculation: {} matches out of {} ingredients = {}", 
            matchCount, recipeIngredients.size(), score);
        
        // Enhance scoring by considering ingredient weights
        if (recipe.getIngredientWeights() != null && !recipe.getIngredientWeights().isEmpty()) {
            double weightedScore = 0.0;
            for (String userIng : userIngredients) {
                for (Map.Entry<String, Double> entry : recipe.getIngredientWeights().entrySet()) {
                    if (entry.getKey().toLowerCase().contains(userIng)) {
                        weightedScore += entry.getValue();
                    }
                }
            }
            score = (score + weightedScore) / 2;
            logger.debug("Weighted score calculation: {} + {} = {}", score, weightedScore, score);
        }
        
        return score;
    }

    private void updateTotalFoodSaved(Recipe recipe) {
        if (recipe.getEstimatedPounds() != null && !recipe.getEstimatedPounds().isEmpty()) {
            try {
                double pounds = Double.parseDouble(recipe.getEstimatedPounds());
                totalFoodSaved += pounds;
                logger.info("Updated total food saved: {} lbs", totalFoodSaved);
            } catch (NumberFormatException e) {
                logger.warn("Invalid estimated pounds value: {}", recipe.getEstimatedPounds());
            }
        }
    }

    public double getTotalFoodSaved() {
        return totalFoodSaved;
    }
}
