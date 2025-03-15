package com.leftoverchef.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Recipe {

    @JsonProperty("Recipe Index")
    private String recipeIndex;

    @JsonProperty("Title")
    private String title;

    @JsonProperty("Instructions")
    private String instructions;

    @JsonProperty("Image_Name")
    private String imageName;

    @JsonProperty("Estimated_Time_Minutes")
    private String estimatedTimeMinutes;

    @JsonProperty("Ingredients")
    private String ingredients;

    @JsonProperty("Cleaned_Ingredients")
    private List<String> cleanedIngredients;

    @JsonProperty("Estimated_Pounds")
    private String estimatedPounds;

    private double score;

    public Recipe() {
        this.cleanedIngredients = new ArrayList<>();
    }

    // Getter and Setter methods
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

    public String getEstimatedTimeMinutes() {
        return estimatedTimeMinutes;
    }

    public void setEstimatedTimeMinutes(String estimatedTimeMinutes) {
        this.estimatedTimeMinutes = estimatedTimeMinutes;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public List<String> getCleanedIngredients() {
        return cleanedIngredients;
    }

    public void setCleanedIngredients(List<String> cleanedIngredients) {
        this.cleanedIngredients = cleanedIngredients;
    }

    public String getEstimatedPounds() {
        return estimatedPounds;
    }

    public void setEstimatedPounds(String estimatedPounds) {
        this.estimatedPounds = estimatedPounds;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}
