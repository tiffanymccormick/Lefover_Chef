package com.leftoverchef.backend.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class IngredientWeightCalculatorTest {

    @Test
    void testSimpleMeasurements() {
        assertEquals(1.0, IngredientWeightCalculator.calculateIngredientWeight("1 pound chicken"), 0.01);
        assertEquals(0.5, IngredientWeightCalculator.calculateIngredientWeight("1 cup flour"), 0.01);
        assertEquals(0.0625, IngredientWeightCalculator.calculateIngredientWeight("1 tablespoon oil"), 0.01);
    }

    @Test
    void testFractions() {
        assertEquals(0.25, IngredientWeightCalculator.calculateIngredientWeight("1/4 pound beef"), 0.01);
        assertEquals(0.25, IngredientWeightCalculator.calculateIngredientWeight("1/2 cup rice"), 0.01);
    }

    @Test
    void testIngredientTypes() {
        assertEquals(4.0, IngredientWeightCalculator.calculateIngredientWeight("whole chicken"), 0.01);
        assertEquals(0.5, IngredientWeightCalculator.calculateIngredientWeight("chicken breast"), 0.01);
        assertEquals(1.0, IngredientWeightCalculator.calculateIngredientWeight("ground beef"), 0.01);
    }

    @Test
    void testVegetables() {
        assertEquals(0.5, IngredientWeightCalculator.calculateIngredientWeight("1 onion"), 0.01);
        assertEquals(0.375, IngredientWeightCalculator.calculateIngredientWeight("2 medium potatoes"), 0.01);
        assertEquals(0.25, IngredientWeightCalculator.calculateIngredientWeight("carrot"), 0.01);
    }

    @Test
    void testUnknownIngredients() {
        assertEquals(0.25, IngredientWeightCalculator.calculateIngredientWeight("mystery ingredient"), 0.01);
        assertEquals(0.25, IngredientWeightCalculator.calculateIngredientWeight("exotic spice"), 0.01);
    }

    @Test
    void testUnitConversions() {
        assertEquals(0.5, IngredientWeightCalculator.calculateIngredientWeight("8 oz flour"), 0.01);
        assertEquals(0.0625, IngredientWeightCalculator.calculateIngredientWeight("1 oz butter"), 0.01);
        assertEquals(0.22, IngredientWeightCalculator.calculateIngredientWeight("100 g sugar"), 0.01);
    }
} 