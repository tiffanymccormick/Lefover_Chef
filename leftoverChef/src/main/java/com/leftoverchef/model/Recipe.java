package com.leftoverchef.model;

import lombok.Data;
import java.util.List;

@Data
public class Recipe {
    private String id;
    private String name;
    private List<String> ingredients;
    private List<String> instructions;
    private String cookTime;
    private String prepTime;
    private String totalTime;
    private String servings;
    private List<String> categories;
}
