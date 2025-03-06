package com.leftoverchef.backend.model;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "app_user") // 'user' is a reserved keyword in some databases
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String email;
    private Date createdAt;
    private int foodSaved; // Track how much food waste was saved

    // Constructors
    public User() {
        this.createdAt = new Date();
        this.foodSaved = 0;
    }

    public User(String username, String email) {
        this.username = username;
        this.email = email;
        this.createdAt = new Date();
        this.foodSaved = 0;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public int getFoodSaved() {
        return foodSaved;
    }

    public void setFoodSaved(int foodSaved) {
        this.foodSaved = foodSaved;
    }

    // Increment food saved
    public void incrementFoodSaved(int amount) {
        this.foodSaved += amount;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", createdAt=" + createdAt +
                ", foodSaved=" + foodSaved +
                '}';
    }
} 