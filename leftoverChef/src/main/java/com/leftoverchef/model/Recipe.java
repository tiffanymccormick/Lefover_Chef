package com.leftoverchef.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Recipe {
    private String id;
    private String title;
    private List<String> ingredients;
    private List<String> cleanedIngredients;
    private String instructions;
    private String imageName;
    private int estimatedTimeMinutes;
    private double estimatedPounds;
    private double matchScore;

    @JsonProperty("ingredients")
    public void setIngredientsFromString(String ingredientsStr) {
        if (ingredientsStr != null && ingredientsStr.startsWith("['") && ingredientsStr.endsWith("']")) {
            String cleaned = ingredientsStr.substring(2, ingredientsStr.length() - 2);
            this.ingredients = Arrays.asList(cleaned.split("', '"));
        } else {
            this.ingredients = new ArrayList<>();
        }
    }

    @JsonProperty("cleanedIngredients")
    public void setCleanedIngredientsFromList(List<String> cleanedIngredients) {
        this.cleanedIngredients = cleanedIngredients != null ? cleanedIngredients : new ArrayList<>();
    }
}
