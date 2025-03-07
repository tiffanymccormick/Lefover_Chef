package com.leftoverchef.backend.service;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import com.leftoverchef.backend.model.MealType;
import com.leftoverchef.backend.model.Recipe;

class RecipeServiceTest {

    @InjectMocks
    private RecipeService recipeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Create test recipes
        Recipe breakfastRecipe = new Recipe();
        breakfastRecipe.setTitle("Pancakes");
        breakfastRecipe.setCleanedIngredientsRaw("[\"2 cups flour\", \"1 cup milk\", \"2 eggs\", \"1 tablespoon sugar\"]");
        breakfastRecipe.determineMealType();

        Recipe dinnerRecipe = new Recipe();
        dinnerRecipe.setTitle("Chicken Stir Fry");
        dinnerRecipe.setCleanedIngredientsRaw("[\"1 pound chicken breast\", \"2 cups rice\", \"1 onion\", \"2 carrots\"]");
        dinnerRecipe.determineMealType();

        // Set test recipes in service
        List<Recipe> testRecipes = Arrays.asList(breakfastRecipe, dinnerRecipe);
        recipeService.setRecipes(testRecipes); // You'll need to add this setter method
    }

    @Test
    void testMatchRecipeByMealType() {
        List<String> ingredients = Arrays.asList("chicken", "rice", "onion");
        Recipe match = recipeService.matchRecipe(ingredients, MealType.DINNER);
        
        assertNotNull(match);
        assertEquals("Chicken Stir Fry", match.getTitle());
    }

    @Test
    void testMatchRecipeByIngredients() {
        List<String> ingredients = Arrays.asList("flour", "milk", "eggs");
        Recipe match = recipeService.matchRecipe(ingredients, MealType.ANY);
        
        assertNotNull(match);
        assertEquals("Pancakes", match.getTitle());
    }

    @Test
    void testGetAlternativeRecipes() {
        List<String> ingredients = Arrays.asList("chicken", "rice");
        List<Recipe> alternatives = recipeService.getAlternativeRecipes(ingredients, MealType.ANY, 2);
        
        assertNotNull(alternatives);
        assertFalse(alternatives.isEmpty());
        assertTrue(alternatives.size() <= 2);
    }

    @Test
    void testRecipeScoring() {
        List<String> ingredients = Arrays.asList("chicken", "rice", "carrot");
        Recipe match = recipeService.matchRecipe(ingredients, MealType.ANY);
        
        assertNotNull(match);
        assertTrue(match.getScore() > 0.0);
        assertNotNull(match.getEstimatedPounds());
    }

    @Test
    void testNoMatchFound() {
        List<String> ingredients = Arrays.asList("caviar", "truffles", "saffron");
        Recipe match = recipeService.matchRecipe(ingredients, MealType.ANY);
        
        // Should return null or have very low score if no good match found
        if (match != null) {
            assertTrue(match.getScore() < 0.3);
        }
    }

    @Test
    void testMealTypeFiltering() {
        List<Recipe> breakfastRecipes = recipeService.getRecipesByMealType(MealType.BREAKFAST);
        List<Recipe> dinnerRecipes = recipeService.getRecipesByMealType(MealType.DINNER);
        
        assertFalse(breakfastRecipes.isEmpty());
        assertFalse(dinnerRecipes.isEmpty());
        
        for (Recipe recipe : breakfastRecipes) {
            assertTrue(recipe.getMealType() == MealType.BREAKFAST || recipe.getMealType() == MealType.ANY);
        }
        
        for (Recipe recipe : dinnerRecipes) {
            assertTrue(recipe.getMealType() == MealType.DINNER || recipe.getMealType() == MealType.ANY);
        }
    }
} 