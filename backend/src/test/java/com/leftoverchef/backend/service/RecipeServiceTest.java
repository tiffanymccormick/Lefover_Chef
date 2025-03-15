package com.leftoverchef.backend.service;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.leftoverchef.backend.model.Recipe;

class RecipeServiceTest {
    private RecipeService recipeService;

    @BeforeEach
    void setUp() {
        recipeService = new RecipeService();
        
        // Create test recipes
        Recipe recipe1 = new Recipe();
        recipe1.setRecipeIndex("1");
        recipe1.setTitle("Pancakes");
        recipe1.setCleanedIngredientsFromString("[\"flour\", \"milk\", \"eggs\", \"sugar\"]");
        recipe1.setEstimatedPounds("1.5");

        Recipe recipe2 = new Recipe();
        recipe2.setRecipeIndex("2");
        recipe2.setTitle("Chicken Stir Fry");
        recipe2.setCleanedIngredientsFromString("[\"chicken\", \"rice\", \"onion\", \"carrots\"]");
        recipe2.setEstimatedPounds("2.0");

        // Set test recipes in service
        List<Recipe> testRecipes = Arrays.asList(recipe1, recipe2);
        recipeService.setRecipes(testRecipes);
    }

    @Test
    void testMatchRecipeByIngredients() {
        List<String> ingredients = Arrays.asList("chicken", "rice", "onion");
        Recipe match = recipeService.matchRecipe(ingredients);
        
        assertNotNull(match);
        assertEquals("Chicken Stir Fry", match.getTitle());
    }

    @Test
    void testGetAlternativeRecipe() {
        List<String> ingredients = Arrays.asList("flour", "milk", "eggs");
        Recipe firstMatch = recipeService.matchRecipe(ingredients);
        
        assertNotNull(firstMatch);
        assertEquals("Pancakes", firstMatch.getTitle());
        
        // Get alternative recipe
        Recipe alternativeMatch = recipeService.getAlternativeRecipe(ingredients);
        assertNotNull(alternativeMatch);
        assertNotEquals(firstMatch.getTitle(), alternativeMatch.getTitle());
    }

    @Test
    void testRecipeScoring() {
        List<String> ingredients = Arrays.asList("chicken", "rice", "carrot");
        Recipe match = recipeService.matchRecipe(ingredients);
        
        assertNotNull(match);
        assertTrue(match.getScore() > 0.0);
        assertEquals("2.0", match.getEstimatedPounds());
    }

    @Test
    void testNoMatchFound() {
        List<String> ingredients = Arrays.asList("caviar", "truffles", "saffron");
        Recipe match = recipeService.matchRecipe(ingredients);
        
        // Should return a recipe with low score since we need to return something
        assertNotNull(match);
        assertTrue(match.getScore() < 0.3);
    }

    @Test
    void testFoodWasteSaved() {
        List<String> ingredients = Arrays.asList("chicken", "rice", "onion");
        Recipe match = recipeService.matchRecipe(ingredients);
        
        assertNotNull(match);
        assertEquals(2.0, recipeService.getTotalFoodSaved(), 0.01);
        
        // Match another recipe
        ingredients = Arrays.asList("flour", "milk", "eggs");
        match = recipeService.matchRecipe(ingredients);
        
        assertNotNull(match);
        assertEquals(3.5, recipeService.getTotalFoodSaved(), 0.01);
    }

    @Test
    void testRecipeRotation() {
        List<String> ingredients = Arrays.asList("flour", "milk");
        
        // First match should be Pancakes (best match)
        Recipe firstMatch = recipeService.matchRecipe(ingredients);
        assertNotNull(firstMatch);
        assertEquals("Pancakes", firstMatch.getTitle());
        
        // Second match should be Chicken Stir Fry (due to rotation)
        Recipe secondMatch = recipeService.matchRecipe(ingredients);
        assertNotNull(secondMatch);
        assertEquals("Chicken Stir Fry", secondMatch.getTitle());
        
        // Third match should be Pancakes again (after rotation reset)
        Recipe thirdMatch = recipeService.matchRecipe(ingredients);
        assertNotNull(thirdMatch);
        assertEquals("Pancakes", thirdMatch.getTitle());
    }
}