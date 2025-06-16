package com.shoppingmall.service;

import com.shoppingmall.mapper.UserMapper;
import com.shoppingmall.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Register a new user
     */
    public User registerUser(String email, String password, String firstName, String lastName) {
        // Check if email already exists
        if (userMapper.existsByEmail(email)) {
            throw new RuntimeException("Email already exists: " + email);
        }

        // Create new user with encoded password
        User user = new User(email, passwordEncoder.encode(password), firstName, lastName);
        
        int result = userMapper.insert(user);
        if (result == 0) {
            throw new RuntimeException("Failed to create user");
        }

        // Return user without password
        user.setPassword(null);
        return user;
    }

    /**
     * Authenticate user with email and password
     */
    public Optional<User> authenticateUser(String email, String password) {
        Optional<User> userOpt = userMapper.findByEmail(email);
        
        if (userOpt.isPresent() && userOpt.get().getIsActive()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                // Remove password from returned user
                user.setPassword(null);
                return Optional.of(user);
            }
        }
        
        return Optional.empty();
    }

    /**
     * Find user by ID
     */
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        Optional<User> userOpt = userMapper.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setPassword(null); // Don't expose password
            return Optional.of(user);
        }
        return Optional.empty();
    }

    /**
     * Find user by email
     */
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        Optional<User> userOpt = userMapper.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setPassword(null); // Don't expose password
            return Optional.of(user);
        }
        return Optional.empty();
    }

    /**
     * Update user profile
     */
    public User updateProfile(Long userId, String firstName, String lastName, String phone) {
        Optional<User> userOpt = userMapper.findById(userId);
        if (!userOpt.isPresent()) {
            throw new RuntimeException("User not found");
        }

        User user = userOpt.get();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPhone(phone);

        int result = userMapper.update(user);
        if (result == 0) {
            throw new RuntimeException("Failed to update user");
        }

        user.setPassword(null);
        return user;
    }

    /**
     * Change user password
     */
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        Optional<User> userOpt = userMapper.findById(userId);
        if (!userOpt.isPresent()) {
            throw new RuntimeException("User not found");
        }

        User user = userOpt.get();
        
        // Verify current password
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        // Update password
        String encodedNewPassword = passwordEncoder.encode(newPassword);
        int result = userMapper.updatePassword(userId, encodedNewPassword);
        
        if (result == 0) {
            throw new RuntimeException("Failed to update password");
        }
    }

    /**
     * Deactivate user account
     */
    public void deactivateUser(Long userId) {
        int result = userMapper.deactivate(userId);
        if (result == 0) {
            throw new RuntimeException("Failed to deactivate user");
        }
    }

    /**
     * Activate user account
     */
    public void activateUser(Long userId) {
        int result = userMapper.activate(userId);
        if (result == 0) {
            throw new RuntimeException("Failed to activate user");
        }
    }

    /**
     * Get all active users (admin function)
     */
    @Transactional(readOnly = true)
    public List<User> getAllActiveUsers() {
        List<User> users = userMapper.findAllActive();
        // Remove passwords from all users
        users.forEach(user -> user.setPassword(null));
        return users;
    }

    /**
     * Check if email exists
     */
    @Transactional(readOnly = true)
    public boolean emailExists(String email) {
        return userMapper.existsByEmail(email);
    }

    /**
     * Check if email exists for another user (useful for updates)
     */
    @Transactional(readOnly = true)
    public boolean emailExistsForOtherUser(String email, Long userId) {
        return userMapper.existsByEmailAndIdNot(email, userId);
    }
}