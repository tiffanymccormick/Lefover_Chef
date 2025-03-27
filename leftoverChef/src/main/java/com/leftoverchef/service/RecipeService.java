package com.leftoverchef.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leftoverchef.model.Recipe;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecipeService {
    private static final Logger logger = LoggerFactory.getLogger(RecipeService.class);
    private List<Recipe> recipes;
    private final ObjectMapper objectMapper;

    public RecipeService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.recipes = new ArrayList<>();
    }

    @PostConstruct
    public void init() throws IOException {
        ClassPathResource resource = new ClassPathResource("cleaned_recipe_data.json");
        try (InputStream inputStream = resource.getInputStream()) {
            JsonNode root = objectMapper.readTree(inputStream);
            JsonNode recipesNode = root.get("recipes");
            if (recipesNode != null && recipesNode.isArray()) {
                for (JsonNode recipeNode : recipesNode) {
                    try {
                        Recipe recipe = objectMapper.treeToValue(recipeNode, Recipe.class);
                        recipe.setId(UUID.randomUUID().toString()); // Generate unique ID
                        recipes.add(recipe);
                    } catch (Exception e) {
                        logger.error("Error parsing recipe: " + e.getMessage());
                    }
                }
            }
            logger.info("Loaded " + recipes.size() + " recipes");
        }
    }

    public List<Recipe> getAllRecipes() {
        return recipes;
    }

    public Recipe getRecipeById(String id) {
        return recipes.stream()
                .filter(recipe -> recipe.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public List<Recipe> findRecipesByIngredients(List<String> ingredients, List<String> excludeIds) {
        if (ingredients == null || ingredients.isEmpty()) {
            return new ArrayList<>();
        }

        // Normalize user ingredients to lowercase for comparison
        List<String> normalizedUserIngredients = ingredients.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList());

        // Calculate scores for all recipes
        List<Recipe> matchedRecipes = recipes.stream()
                .filter(recipe -> !excludeIds.contains(recipe.getId())) // Filter out excluded recipes
                .map(recipe -> {
                    Recipe recipeWithScore = new Recipe();
                    copyProperties(recipe, recipeWithScore);
                    
                    // Calculate match score
                    double score = calculateMatchScore(normalizedUserIngredients, recipe.getCleanedIngredients());
                    recipeWithScore.setMatchScore(score);
                    
                    return recipeWithScore;
                })
                .filter(recipe -> recipe.getMatchScore() > 0.0) // Filter out recipes with no matches
                .sorted(Comparator.comparing(Recipe::getMatchScore).reversed()) // Sort by score descending
                .collect(Collectors.toList());

        return matchedRecipes;
    }

    private double calculateMatchScore(List<String> userIngredients, List<String> recipeIngredients) {
        if (userIngredients.isEmpty() || recipeIngredients == null || recipeIngredients.isEmpty()) {
            return 0.0;
        }

        // Count how many user ingredients are found in the recipe
        long matchedIngredients = userIngredients.stream()
                .filter(userIngr -> recipeIngredients.stream()
                        .anyMatch(recipeIngr -> recipeIngr.toLowerCase().contains(userIngr)))
                .count();

        // Calculate both fractions
        double userFraction = (double) matchedIngredients / userIngredients.size();
        double recipeFraction = (double) matchedIngredients / recipeIngredients.size();

        // Combine scores with equal weight
        return userFraction * recipeFraction;
    }

    private void copyProperties(Recipe source, Recipe target) {
        target.setId(source.getId());
        target.setTitle(source.getTitle());
        target.setIngredients(source.getIngredients());
        target.setCleanedIngredients(source.getCleanedIngredients());
        target.setInstructions(source.getInstructions());
        target.setImageName(source.getImageName());
        target.setEstimatedTimeMinutes(source.getEstimatedTimeMinutes());
        target.setEstimatedPounds(source.getEstimatedPounds());
    }
}
