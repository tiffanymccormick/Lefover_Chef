package com.leftoverchef.backend.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IngredientWeightCalculator {
    private static final Map<String, Map<String, Double>> INGREDIENT_WEIGHTS = new HashMap<>();
    private static final Map<String, Double> MEASUREMENT_CONVERSIONS = new HashMap<>();
    
    static {
        // Initialize ingredient weights
        Map<String, Double> chickenWeights = new HashMap<>();
        chickenWeights.put("whole", 4.0);
        chickenWeights.put("breast", 0.5);
        chickenWeights.put("thigh", 0.375);
        chickenWeights.put("wing", 0.25);
        INGREDIENT_WEIGHTS.put("chicken", chickenWeights);

        Map<String, Double> beefWeights = new HashMap<>();
        beefWeights.put("ground", 1.0);
        beefWeights.put("steak", 0.75);
        beefWeights.put("roast", 3.0);
        INGREDIENT_WEIGHTS.put("beef", beefWeights);

        Map<String, Double> vegetableWeights = new HashMap<>();
        vegetableWeights.put("onion", 0.5);
        vegetableWeights.put("potato", 0.375);
        vegetableWeights.put("carrot", 0.25);
        vegetableWeights.put("squash", 1.5);
        vegetableWeights.put("tomato", 0.375);
        vegetableWeights.put("pepper", 0.25);
        vegetableWeights.put("garlic", 0.0625);
        INGREDIENT_WEIGHTS.put("vegetables", vegetableWeights);

        // Initialize measurement conversions
        MEASUREMENT_CONVERSIONS.put("cup", 0.5);
        MEASUREMENT_CONVERSIONS.put("tablespoon", 0.0625);
        MEASUREMENT_CONVERSIONS.put("teaspoon", 0.0208);
        MEASUREMENT_CONVERSIONS.put("ounce", 0.0625);
        MEASUREMENT_CONVERSIONS.put("pound", 1.0);
        MEASUREMENT_CONVERSIONS.put("gram", 0.0022);
    }

    public static double calculateIngredientWeight(String ingredient) {
        String ingredientLower = ingredient.toLowerCase();
        
        // Extract quantity and unit using regex
        Pattern pattern = Pattern.compile("(\\d+(?:/\\d+)?|\\d*\\.\\d+)\\s*(cup|tablespoon|teaspoon|pound|ounce|lb|oz|g)s?");
        Matcher matcher = pattern.matcher(ingredientLower);
        
        if (matcher.find()) {
            String quantityStr = matcher.group(1);
            String unit = matcher.group(2);
            
            // Convert fraction to decimal if necessary
            double quantity;
            if (quantityStr.contains("/")) {
                String[] fraction = quantityStr.split("/");
                quantity = Double.parseDouble(fraction[0]) / Double.parseDouble(fraction[1]);
            } else {
                quantity = Double.parseDouble(quantityStr);
            }
            
            // Standardize units
            Map<String, String> unitMapping = new HashMap<>();
            unitMapping.put("lb", "pound");
            unitMapping.put("oz", "ounce");
            unitMapping.put("g", "gram");
            unitMapping.put("tbsp", "tablespoon");
            unitMapping.put("tsp", "teaspoon");
            
            String standardUnit = unitMapping.getOrDefault(unit, unit);
            return quantity * MEASUREMENT_CONVERSIONS.getOrDefault(standardUnit, 1.0);
        }
        
        // If no measurement found, estimate based on ingredient type
        for (Map.Entry<String, Map<String, Double>> category : INGREDIENT_WEIGHTS.entrySet()) {
            for (Map.Entry<String, Double> item : category.getValue().entrySet()) {
                if (ingredientLower.contains(item.getKey())) {
                    return item.getValue();
                }
            }
        }
        
        // Default weight for unknown ingredients
        return 0.25;
    }
} 