package com.shoppingmall.service;

import com.shoppingmall.model.User;
import com.shoppingmall.security.JwtTokenProvider;
import com.shoppingmall.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class AuthService {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    @Autowired
    public AuthService(UserService userService, AuthenticationManager authenticationManager, 
                      JwtTokenProvider tokenProvider) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    /**
     * Authenticate user and generate JWT token
     */
    public AuthResponse authenticateUser(String email, String password) {
        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(email, password)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generate JWT token
        String jwt = tokenProvider.generateToken(authentication);

        // Get user details
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Optional<User> userOpt = userService.findById(userPrincipal.getId());

        if (!userOpt.isPresent()) {
            throw new RuntimeException("User not found");
        }

        return new AuthResponse(jwt, userOpt.get());
    }

    /**
     * Register new user and generate JWT token
     */
    public AuthResponse registerUser(String email, String password, String firstName, String lastName) {
        // Register user
        User user = userService.registerUser(email, password, firstName, lastName);

        // Authenticate and generate token
        return authenticateUser(email, password);
    }

    /**
     * Get current authenticated user
     */
    @Transactional(readOnly = true)
    public Optional<User> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal) {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            return userService.findById(userPrincipal.getId());
        }
        
        return Optional.empty();
    }

    /**
     * Get current authenticated user ID
     */
    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal) {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            return userPrincipal.getId();
        }
        
        throw new RuntimeException("User not authenticated");
    }

    /**
     * Check if user is authenticated
     */
    public boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated() && 
               authentication.getPrincipal() instanceof UserPrincipal;
    }

    /**
     * Validate JWT token
     */
    public boolean validateToken(String token) {
        return tokenProvider.validateToken(token);
    }

    /**
     * Get user ID from token
     */
    public Long getUserIdFromToken(String token) {
        return tokenProvider.getUserIdFromToken(token);
    }

    // Inner class for authentication response
    public static class AuthResponse {
        private final String token;
        private final User user;

        public AuthResponse(String token, User user) {
            this.token = token;
            this.user = user;
        }

        public String getToken() {
            return token;
        }

        public User getUser() {
            return user;
        }
    }
}