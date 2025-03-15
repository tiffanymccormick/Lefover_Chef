package com.leftoverchef.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.leftoverchef.backend.model.Recipe;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecipeService {
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
        InputStream inputStream = getClass().getResourceAsStream("/Final_Updated_Recipe_Data_with_weights.json");
        try {
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
        System.out.println("\nMatching recipe for ingredients: " + userIngredients);
        
        if (userIngredients == null || userIngredients.isEmpty()) {
            System.out.println("Invalid input - userIngredients empty");
            return null;
        }

        if (recipes == null || recipes.isEmpty()) {
            System.out.println("No recipes available in database");
            return null;
        }

        // Preprocess user ingredients
        List<String> processedUserIngredients = userIngredients.stream()
            .map(ing -> ing.toLowerCase().trim())
            .filter(ing -> !ing.isEmpty())
            .collect(Collectors.toList());

        System.out.println("Processed user ingredients: " + processedUserIngredients);

        // Get all recipes with their scores
        List<Recipe> scoredRecipes = recipes.stream()
            .filter(recipe -> !usedRecipeIds.contains(recipe.getRecipeIndex()))
            .map(recipe -> {
                double score = computeScore(processedUserIngredients, recipe);
                recipe.setScore(score);
                if (score > 0.0) {
                    System.out.println("\nPotential match found:");
                    System.out.println("Recipe: " + recipe.getTitle());
                    System.out.println("Score: " + score);
                    System.out.println("Ingredients: " + recipe.getCleanedIngredients());
                }
                return recipe;
            })
            .filter(recipe -> recipe.getScore() > 0.0) // Only include recipes with matches
            .sorted(Comparator.comparingDouble(Recipe::getScore).reversed())
            .collect(Collectors.toList());

        System.out.println("\nFound " + scoredRecipes.size() + " potential matches");

        // If all recipes have been used or no matches found, reset and try again
        if (scoredRecipes.isEmpty()) {
            System.out.println("No matches found or all recipes used, resetting used recipes set");
            usedRecipeIds.clear();
            scoredRecipes = recipes.stream()
                .map(recipe -> {
                    double score = computeScore(processedUserIngredients, recipe);
                    recipe.setScore(score);
                    if (score > 0.0) {
                        System.out.println("\nPotential match found (after reset):");
                        System.out.println("Recipe: " + recipe.getTitle());
                        System.out.println("Score: " + score);
                        System.out.println("Ingredients: " + recipe.getCleanedIngredients());
                    }
                    return recipe;
                })
                .filter(recipe -> recipe.getScore() > 0.0)
                .sorted(Comparator.comparingDouble(Recipe::getScore).reversed())
                .collect(Collectors.toList());
        }

        if (scoredRecipes.isEmpty()) {
            System.out.println("No recipes found with matching ingredients");
            return null;
        }

        // Get the best matching recipe
        Recipe bestRecipe = scoredRecipes.get(0);
        System.out.println("\nBest match selected:");
        System.out.println("Title: " + bestRecipe.getTitle());
        System.out.println("Score: " + bestRecipe.getScore());
        System.out.println("Ingredients: " + bestRecipe.getCleanedIngredients());
        
        usedRecipeIds.add(bestRecipe.getRecipeIndex());
        updateTotalFoodSaved(bestRecipe);
        
        return bestRecipe;
    }

    public Recipe getAlternativeRecipe(List<String> userIngredients) {
        System.out.println("Getting alternative recipe for ingredients: " + userIngredients);
        
        if (userIngredients == null || userIngredients.isEmpty()) {
            System.out.println("Invalid input for alternative recipe");
            return null;
        }

        // Preprocess user ingredients
        List<String> processedUserIngredients = userIngredients.stream()
            .map(ing -> ing.toLowerCase().trim())
            .filter(ing -> !ing.isEmpty())
            .collect(Collectors.toList());

        System.out.println("Processed user ingredients: " + processedUserIngredients);

        // Get all recipes with their scores, excluding the last matched recipe
        List<Recipe> scoredRecipes = recipes.stream()
            .filter(recipe -> !usedRecipeIds.contains(recipe.getRecipeIndex()))
            .map(recipe -> {
                double score = computeScore(processedUserIngredients, recipe);
                recipe.setScore(score);
                if (score > 0.0) {
                    System.out.println("\nPotential alternative match found:");
                    System.out.println("Recipe: " + recipe.getTitle());
                    System.out.println("Score: " + score);
                    System.out.println("Ingredients: " + recipe.getCleanedIngredients());
                }
                return recipe;
            })
            .filter(recipe -> recipe.getScore() > 0.0) // Only include recipes with matches
            .sorted(Comparator.comparingDouble(Recipe::getScore).reversed())
            .collect(Collectors.toList());

        System.out.println("\nFound " + scoredRecipes.size() + " potential alternative matches");

        // If no unused recipes, reset and try again
        if (scoredRecipes.isEmpty()) {
            String lastUsedId = usedRecipeIds.iterator().next();
            usedRecipeIds.clear();
            usedRecipeIds.add(lastUsedId); // Keep the last used recipe in the set
            
            scoredRecipes = recipes.stream()
                .filter(recipe -> !usedRecipeIds.contains(recipe.getRecipeIndex()))
                .map(recipe -> {
                    double score = computeScore(processedUserIngredients, recipe);
                    recipe.setScore(score);
                    if (score > 0.0) {
                        System.out.println("\nPotential alternative match found (after reset):");
                        System.out.println("Recipe: " + recipe.getTitle());
                        System.out.println("Score: " + score);
                        System.out.println("Ingredients: " + recipe.getCleanedIngredients());
                    }
                    return recipe;
                })
                .filter(recipe -> recipe.getScore() > 0.0)
                .sorted(Comparator.comparingDouble(Recipe::getScore).reversed())
                .collect(Collectors.toList());
        }

        if (scoredRecipes.isEmpty()) {
            System.out.println("No alternative recipes found with matching ingredients");
            return null;
        }

        Recipe alternativeRecipe = scoredRecipes.get(0);
        System.out.println("\nBest alternative selected:");
        System.out.println("Title: " + alternativeRecipe.getTitle());
        System.out.println("Score: " + alternativeRecipe.getScore());
        System.out.println("Ingredients: " + alternativeRecipe.getCleanedIngredients());
        
        usedRecipeIds.add(alternativeRecipe.getRecipeIndex());
        updateTotalFoodSaved(alternativeRecipe);
        
        return alternativeRecipe;
    }

    private double computeScore(List<String> userIngredients, Recipe recipe) {
        List<String> recipeIngredients = recipe.getCleanedIngredients();
        if (recipeIngredients == null || recipeIngredients.isEmpty()) {
            return 0.0;
        }

        // Preprocess recipe ingredients
        List<String> processedRecipeIngredients = recipeIngredients.stream()
            .map(ing -> ing.toLowerCase().trim())
            .collect(Collectors.toList());

        int matches = 0;
        Set<String> matchedIngredients = new HashSet<>();
        Set<String> matchedUserIngredients = new HashSet<>();

        // Try different matching strategies
        for (String userIng : userIngredients) {
            boolean foundMatch = false;
            
            // Strategy 1: Direct contains match
            for (String recipeIng : processedRecipeIngredients) {
                if (!matchedIngredients.contains(recipeIng) && 
                    (recipeIng.contains(userIng) || userIng.contains(recipeIng))) {
                    matches++;
                    matchedIngredients.add(recipeIng);
                    matchedUserIngredients.add(userIng);
                    foundMatch = true;
                    System.out.println("Direct match: " + userIng + " -> " + recipeIng);
                    break;
                }
            }
            
            // Strategy 2: Word-based matching if no direct match found
            if (!foundMatch) {
                String[] userWords = userIng.split("\\s+");
                for (String recipeIng : processedRecipeIngredients) {
                    if (!matchedIngredients.contains(recipeIng)) {
                        String[] recipeWords = recipeIng.split("\\s+");
                        // Check if any word in user ingredient matches any word in recipe ingredient
                        for (String userWord : userWords) {
                            for (String recipeWord : recipeWords) {
                                if (recipeWord.contains(userWord) || userWord.contains(recipeWord)) {
                                    matches++;
                                    matchedIngredients.add(recipeIng);
                                    matchedUserIngredients.add(userIng);
                                    System.out.println("Word match: " + userIng + " -> " + recipeIng + " (matched words: " + userWord + ", " + recipeWord + ")");
                                    foundMatch = true;
                                    break;
                                }
                            }
                            if (foundMatch) break;
                        }
                    }
                    if (foundMatch) break;
                }
            }
        }

        if (matches == 0) {
            return 0.0;
        }

        // Calculate scores based on matches
        double matchRatio = (double) matchedUserIngredients.size() / userIngredients.size();
        double coverageRatio = (double) matchedIngredients.size() / processedRecipeIngredients.size();
        
        // Weighted score calculation
        double score = (0.7 * matchRatio) + (0.3 * coverageRatio);
        
        if (score > 0.0) {
            System.out.println("\nScore calculation for " + recipe.getTitle() + ":");
            System.out.println("Matches found: " + matches);
            System.out.println("User ingredients matched: " + matchedUserIngredients.size() + "/" + userIngredients.size() + " = " + matchRatio);
            System.out.println("Recipe ingredients matched: " + matchedIngredients.size() + "/" + processedRecipeIngredients.size() + " = " + coverageRatio);
            System.out.println("Final score: " + score);
        }
        
        return score;
    }

    public double getTotalFoodSaved() {
        return totalFoodSaved;
    }

    private void updateTotalFoodSaved(Recipe recipe) {
        String poundsStr = recipe.getEstimatedPounds();
        if (poundsStr != null && !poundsStr.isEmpty()) {
            try {
                poundsStr = poundsStr.replaceAll("[^0-9.]", "");
                double pounds = Double.parseDouble(poundsStr);
                totalFoodSaved += pounds;
            } catch (NumberFormatException e) {
                System.out.println("Error parsing estimated pounds: " + e.getMessage());
            }
        }
    }
}
