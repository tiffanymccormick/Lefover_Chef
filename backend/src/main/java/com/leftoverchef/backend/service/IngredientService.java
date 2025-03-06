package com.leftoverchef.backend.service;

import com.leftoverchef.backend.model.Ingredient;
import com.leftoverchef.backend.model.Ingredient.IngredientCategory;
import com.leftoverchef.backend.repository.IngredientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class IngredientService {

    @Autowired
    private IngredientRepository ingredientRepository;

    /**
     * Get all ingredients
     */
    public List<Ingredient> getAllIngredients() {
        return ingredientRepository.findAll();
    }

    /**
     * Get ingredients by category
     */
    public List<Ingredient> getIngredientsByCategory(IngredientCategory category) {
        return ingredientRepository.findByCategory(category);
    }

    /**
     * Get ingredient by name
     */
    public Optional<Ingredient> getIngredientByName(String name) {
        return ingredientRepository.findByName(name);
    }

    /**
     * Save an ingredient
     */
    public Ingredient saveIngredient(Ingredient ingredient) {
        return ingredientRepository.save(ingredient);
    }

    /**
     * Categorize an ingredient based on its name
     */
    public IngredientCategory categorizeIngredient(String ingredientName) {
        String lowerName = ingredientName.toLowerCase();
        
        // Produce category
        if (lowerName.contains("apple") || 
            lowerName.contains("banana") || 
            lowerName.contains("orange") || 
            lowerName.contains("tomato") || 
            lowerName.contains("lettuce") || 
            lowerName.contains("carrot") || 
            lowerName.contains("potato") || 
            lowerName.contains("onion") || 
            lowerName.contains("garlic") || 
            lowerName.contains("pepper") || 
            lowerName.contains("broccoli") || 
            lowerName.contains("spinach") || 
            lowerName.contains("cucumber") || 
            lowerName.contains("celery") || 
            lowerName.contains("avocado") || 
            lowerName.contains("berry") || 
            lowerName.contains("fruit") || 
            lowerName.contains("vegetable")) {
            return IngredientCategory.PRODUCE;
        }
        
        // Dairy category
        else if (lowerName.contains("milk") || 
                 lowerName.contains("cheese") || 
                 lowerName.contains("yogurt") || 
                 lowerName.contains("butter") || 
                 lowerName.contains("cream") || 
                 lowerName.contains("egg")) {
            return IngredientCategory.DAIRY;
        }
        
        // Spices category
        else if (lowerName.contains("salt") || 
                 lowerName.contains("pepper") || 
                 lowerName.contains("spice") || 
                 lowerName.contains("herb") || 
                 lowerName.contains("cinnamon") || 
                 lowerName.contains("oregano") || 
                 lowerName.contains("basil") || 
                 lowerName.contains("thyme") || 
                 lowerName.contains("cumin") || 
                 lowerName.contains("paprika") || 
                 lowerName.contains("curry")) {
            return IngredientCategory.SPICES;
        }
        
        // Default to OTHER
        else {
            return IngredientCategory.OTHER;
        }
    }
} 