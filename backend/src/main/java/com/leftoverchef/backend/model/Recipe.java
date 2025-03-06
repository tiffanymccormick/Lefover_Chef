package com.leftoverchef.backend.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
// Maps the JSON fields to Java fields and converts the "Cleaned_Ingredients" string into a List of ingredients
public class Recipe {

    // Maps the JSON key "Recipe Index" to this field
    @JsonProperty("Recipe Index")
    private String recipeIndex;

    // Maps the JSON key "Title" to this field
    @JsonProperty("Title")
    private String title;

    // Maps the JSON key "Ingredients" to this field (raw string version)
    @JsonProperty("Ingredients")
    private String ingredients;

    // Maps the JSON key "Instructions" to this field
    @JsonProperty("Instructions")
    private String instructions;

    // Maps the JSON key "Image_Name" to this field
    @JsonProperty("Image_Name")
    private String imageName;

    // Maps the JSON key "Cleaned_Ingredients" to this field as a raw string
    @JsonProperty("Cleaned_Ingredients")
    private String cleanedIngredientsRaw;

    // Store the parsed list of ingredients from cleanedIngredientsRaw.
    private List<String> cleanedIngredients;

    // Maps the JSON key "Estimated_Pounds" to this field
    @JsonProperty("Estimated_Pounds")
    private String estimatedPounds;

    // Maps the JSON key "Estimated_Time_Minutes" to this field
    @JsonProperty("Estimated_Time_Minutes")
    private String estimatedTimeMinutes;
    
    // Meal type (breakfast, lunch, dinner)
    private MealType mealType;
    
    // Score for recipe matching (not persisted)
    private double score;

    // Getter and Setter methods for each field

    public String getRecipeIndex() {
        return recipeIndex;
    }

    public void setRecipeIndex(String recipeIndex) {
        this.recipeIndex = recipeIndex;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getCleanedIngredientsRaw() {
        return cleanedIngredientsRaw;
    }

    // When we set the raw cleaned ingredients string, we also parse it into a List of strings.
    public void setCleanedIngredientsRaw(String cleanedIngredientsRaw) {
        this.cleanedIngredientsRaw = cleanedIngredientsRaw;
        this.cleanedIngredients = parseCleanedIngredients(cleanedIngredientsRaw);
    }

    public List<String> getCleanedIngredients() {
        return cleanedIngredients;
    }

    public String getEstimatedPounds() {
        return estimatedPounds;
    }

    public void setEstimatedPounds(String estimatedPounds) {
        this.estimatedPounds = estimatedPounds;
    }

    public String getEstimatedTimeMinutes() {
        return estimatedTimeMinutes;
    }

    public void setEstimatedTimeMinutes(String estimatedTimeMinutes) {
        this.estimatedTimeMinutes = estimatedTimeMinutes;
    }
    
    public MealType getMealType() {
        return mealType;
    }

    public void setMealType(MealType mealType) {
        this.mealType = mealType;
    }
    
    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    // Helper method to convert the raw string of cleaned ingredients into a List.
    // This method removes square brackets, splits the string by commas, and trims quotes.
    private List<String> parseCleanedIngredients(String raw) {
        List<String> list = new ArrayList<>();
        if (raw == null || raw.isEmpty()) {
            return list;
        }
        // Remove the square brackets at the start and end
        String trimmed = raw.trim();
        if (trimmed.startsWith("[")) {
            trimmed = trimmed.substring(1);
        }
        if (trimmed.endsWith("]")) {
            trimmed = trimmed.substring(0, trimmed.length() - 1);
        }
        // Split the string by comma and optional spaces, assuming commas don't appear inside an ingredient.
        String[] parts = trimmed.split("',\\s*'");
        for (String part : parts) {
            // Remove any stray single quotes and trim extra spaces.
            part = part.replace("'", "").trim();
            if (!part.isEmpty()) {
                list.add(part);
            }
        }
        return list;
    }
    
    // Determine meal type based on recipe title or ingredients
    public void determineMealType() {
        String titleLower = title.toLowerCase();
        
        // Check for breakfast keywords
        if (titleLower.contains("breakfast") || 
            titleLower.contains("pancake") || 
            titleLower.contains("waffle") || 
            titleLower.contains("egg") || 
            titleLower.contains("omelette") || 
            titleLower.contains("cereal") || 
            titleLower.contains("oatmeal")) {
            this.mealType = MealType.BREAKFAST;
        } 
        // Check for lunch keywords
        else if (titleLower.contains("lunch") || 
                 titleLower.contains("sandwich") || 
                 titleLower.contains("salad") || 
                 titleLower.contains("soup") || 
                 titleLower.contains("wrap")) {
            this.mealType = MealType.LUNCH;
        } 
        // Check for dinner keywords
        else if (titleLower.contains("dinner") || 
                 titleLower.contains("roast") || 
                 titleLower.contains("steak") || 
                 titleLower.contains("pasta") || 
                 titleLower.contains("casserole") || 
                 titleLower.contains("curry")) {
            this.mealType = MealType.DINNER;
        } 
        // Default to ANY if no specific meal type is detected
        else {
            this.mealType = MealType.ANY;
        }
    }
}
