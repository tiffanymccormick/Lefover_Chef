package com.leftoverchef.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.JoinColumn;
import java.util.Date;

@Entity
public class MealLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String recipeName;
    private String recipeId;
    
    @Enumerated(EnumType.STRING)
    private MealType mealType;
    
    private Date createdAt;
    private int ingredientsSaved;

    // Constructors
    public MealLog() {
        this.createdAt = new Date();
    }

    public MealLog(User user, String recipeName, String recipeId, MealType mealType, int ingredientsSaved) {
        this.user = user;
        this.recipeName = recipeName;
        this.recipeId = recipeId;
        this.mealType = mealType;
        this.ingredientsSaved = ingredientsSaved;
        this.createdAt = new Date();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public String getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(String recipeId) {
        this.recipeId = recipeId;
    }

    public MealType getMealType() {
        return mealType;
    }

    public void setMealType(MealType mealType) {
        this.mealType = mealType;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public int getIngredientsSaved() {
        return ingredientsSaved;
    }

    public void setIngredientsSaved(int ingredientsSaved) {
        this.ingredientsSaved = ingredientsSaved;
    }

    @Override
    public String toString() {
        return "MealLog{" +
                "id=" + id +
                ", user=" + user.getUsername() +
                ", recipeName='" + recipeName + '\'' +
                ", recipeId='" + recipeId + '\'' +
                ", mealType=" + mealType +
                ", createdAt=" + createdAt +
                ", ingredientsSaved=" + ingredientsSaved +
                '}';
    }
} 