package com.leftoverchef.backend.service;

import com.leftoverchef.backend.model.User;
import com.leftoverchef.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Get all users
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Get user by ID
     */
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Get user by username
     */
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Get user by email
     */
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Save a user
     */
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    /**
     * Update food saved count
     */
    public User updateFoodSaved(Long userId, int amount) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.incrementFoodSaved(amount);
            return userRepository.save(user);
        }
        return null;
    }

    /**
     * Delete a user
     */
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
} 