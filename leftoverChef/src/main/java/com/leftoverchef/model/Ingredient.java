package com.leftoverchef.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Ingredient {
    private String name;
    private String quantity;
    private double estimatedPounds;
    
    public Ingredient() {}
    
    public Ingredient(String name) {
        this.name = name;
    }
    
    public Ingredient(String name, String quantity, double estimatedPounds) {
        this.name = name;
        this.quantity = quantity;
        this.estimatedPounds = estimatedPounds;
    }
    
    @Override
    public String toString() {
        if (quantity != null && !quantity.isEmpty()) {
            return quantity + " " + name;
        }
        return name;
    }
}
