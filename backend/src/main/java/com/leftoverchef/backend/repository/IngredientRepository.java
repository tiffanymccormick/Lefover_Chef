package com.leftoverchef.backend.repository;

import com.leftoverchef.backend.model.Ingredient;
import com.leftoverchef.backend.model.Ingredient.IngredientCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
    Optional<Ingredient> findByName(String name);
    List<Ingredient> findByCategory(IngredientCategory category);
} 