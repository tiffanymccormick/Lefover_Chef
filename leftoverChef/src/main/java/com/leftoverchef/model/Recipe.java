package com.leftoverchef.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Recipe {
    private String id;
    private String title;
    private List<Ingredient> ingredients;
    private List<String> cleanedIngredients;
    private String instructions;
    private String imageName;
    private int estimatedTimeMinutes;
    private double estimatedPounds;
    private double matchScore;

    public Recipe() {
        this.ingredients = new ArrayList<>();
        this.cleanedIngredients = new ArrayList<>();
    }

    @JsonProperty("ingredients")
    public void setIngredientsFromString(String ingredientsStr) {
        if (ingredientsStr != null && ingredientsStr.startsWith("['") && ingredientsStr.endsWith("']")) {
            String cleaned = ingredientsStr.substring(2, ingredientsStr.length() - 2);
            List<String> rawIngredients = Arrays.asList(cleaned.split("', '"));
            this.ingredients = rawIngredients.stream()
                .map(raw -> {
                    Ingredient ingredient = new Ingredient(raw.trim());
                    return ingredient;
                })
                .collect(Collectors.toList());
        } else {
            this.ingredients = new ArrayList<>();
        }
    }

    @JsonProperty("cleanedIngredients")
    public void setCleanedIngredientsFromList(List<String> cleanedIngredients) {
        this.cleanedIngredients = cleanedIngredients != null ? cleanedIngredients : new ArrayList<>();
    }
}
