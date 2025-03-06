package com.leftoverchef.backend.controller;

import com.leftoverchef.backend.model.User;
import com.leftoverchef.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // Allow requests from any origin
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Get all users
     */
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Get user by ID
     */
    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Create a new user
     */
    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody Map<String, String> payload) {
        String username = payload.get("username");
        String email = payload.get("email");
        
        if (username == null || email == null) {
            return ResponseEntity.badRequest().build();
        }
        
        // Check if username or email already exists
        if (userService.getUserByUsername(username).isPresent()) {
            return ResponseEntity.badRequest().build();
        }
        
        if (userService.getUserByEmail(email).isPresent()) {
            return ResponseEntity.badRequest().build();
        }
        
        User user = new User(username, email);
        User savedUser = userService.saveUser(user);
        return ResponseEntity.ok(savedUser);
    }

    /**
     * Update food saved count
     */
    @PutMapping("/users/{id}/foodsaved")
    public ResponseEntity<User> updateFoodSaved(@PathVariable Long id, @RequestBody Map<String, Integer> payload) {
        Integer amount = payload.get("amount");
        
        if (amount == null) {
            return ResponseEntity.badRequest().build();
        }
        
        User updatedUser = userService.updateFoodSaved(id, amount);
        if (updatedUser != null) {
            return ResponseEntity.ok(updatedUser);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Delete a user
     */
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        if (user.isPresent()) {
            userService.deleteUser(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
} 