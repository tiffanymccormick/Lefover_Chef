package com.leftoverchef.service;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leftoverchef.model.Recipe;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import com.fasterxml.jackson.core.type.TypeReference;

@Service
public class RecipeService {
    private List<Recipe> recipes;
    private final ObjectMapper objectMapper;

    public RecipeService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void init() throws IOException {
        ClassPathResource resource = new ClassPathResource("cleaned_recipe_data.json");
        try (InputStream inputStream = resource.getInputStream()) {
            JsonParser parser = objectMapper.getFactory().createParser(inputStream);
            
            // Skip the beginning array token
            if (parser.nextToken() != JsonToken.START_ARRAY) {
                throw new IllegalStateException("Expected content to be an array");
            }
            
            recipes = new ArrayList<>();
            TypeReference<Recipe> typeRef = new TypeReference<Recipe>() {};
            
            // Read the tokens one by one
            while (parser.nextToken() == JsonToken.START_OBJECT) {
                Recipe recipe = objectMapper.readValue(parser, typeRef);
                recipes.add(recipe);
            }
        }
    }

    public List<Recipe> getAllRecipes() {
        return recipes;
    }

    public List<Recipe> findRecipesByIngredients(List<String> ingredients) {
        return recipes.stream()
            .filter(recipe -> recipe.getIngredients().stream()
                .anyMatch(recipeIngredient -> 
                    ingredients.stream()
                        .anyMatch(userIngredient -> 
                            recipeIngredient.toLowerCase()
                                .contains(userIngredient.toLowerCase()))))
            .collect(Collectors.toList());
    }

    public Recipe getRecipeById(String id) {
        return recipes.stream()
            .filter(recipe -> recipe.getId().equals(id))
            .findFirst()
            .orElse(null);
    }
}
