package com.shoppingmall.controller;

import com.shoppingmall.model.Address;
import com.shoppingmall.model.User;
import com.shoppingmall.service.AddressService;
import com.shoppingmall.service.AuthService;
import com.shoppingmall.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "${cors.allowed-origins}", maxAge = 3600)
@PreAuthorize("hasRole('USER')")
public class UserController {

    private final UserService userService;
    private final AuthService authService;
    private final AddressService addressService;

    @Autowired
    public UserController(UserService userService, AuthService authService, AddressService addressService) {
        this.userService = userService;
        this.authService = authService;
        this.addressService = addressService;
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile() {
        try {
            Optional<User> userOpt = authService.getCurrentUser();
            
            if (userOpt.isPresent()) {
                return ResponseEntity.ok(userOpt.get());
            } else {
                return ResponseEntity.status(401)
                    .body(Map.of("message", "User not authenticated"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", "Error fetching profile: " + e.getMessage()));
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateUserProfile(@Valid @RequestBody UpdateProfileRequest request) {
        try {
            Long userId = authService.getCurrentUserId();
            User updatedUser = userService.updateProfile(
                userId,
                request.getFirstName(),
                request.getLastName(),
                request.getPhone()
            );
            
            return ResponseEntity.ok(Map.of(
                "message", "Profile updated successfully",
                "user", updatedUser
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        try {
            Long userId = authService.getCurrentUserId();
            userService.changePassword(
                userId,
                request.getCurrentPassword(),
                request.getNewPassword()
            );
            
            return ResponseEntity.ok(Map.of(
                "message", "Password changed successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/deactivate")
    public ResponseEntity<?> deactivateAccount() {
        try {
            Long userId = authService.getCurrentUserId();
            userService.deactivateUser(userId);
            
            return ResponseEntity.ok(Map.of(
                "message", "Account deactivated successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", e.getMessage()));
        }
    }

    // Address endpoints
    @GetMapping("/addresses")
    public ResponseEntity<?> getUserAddresses() {
        try {
            Long userId = authService.getCurrentUserId();
            List<Address> addresses = addressService.getUserAddresses(userId);
            
            return ResponseEntity.ok(Map.of("addresses", addresses));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", "Error fetching addresses: " + e.getMessage()));
        }
    }

    @PostMapping("/addresses")
    public ResponseEntity<?> addAddress(@Valid @RequestBody AddressRequest request) {
        try {
            Long userId = authService.getCurrentUserId();
            Address address = addressService.createAddress(
                userId,
                request.getAddressLine1(),
                request.getAddressLine2(),
                request.getCity(),
                request.getState(),
                request.getZipCode(),
                request.getCountry(),
                request.isDefaultAddress()
            );
            
            return ResponseEntity.ok(Map.of(
                "message", "Address added successfully",
                "address", address
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/addresses/{addressId}")
    public ResponseEntity<?> updateAddress(
            @PathVariable Long addressId,
            @Valid @RequestBody AddressRequest request) {
        try {
            Long userId = authService.getCurrentUserId();
            Address address = addressService.updateAddress(
                userId,
                addressId,
                request.getAddressLine1(),
                request.getAddressLine2(),
                request.getCity(),
                request.getState(),
                request.getZipCode(),
                request.getCountry(),
                request.isDefaultAddress()
            );
            
            return ResponseEntity.ok(Map.of(
                "message", "Address updated successfully",
                "address", address
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/addresses/{addressId}")
    public ResponseEntity<?> deleteAddress(@PathVariable Long addressId) {
        try {
            Long userId = authService.getCurrentUserId();
            addressService.deleteAddress(userId, addressId);
            
            return ResponseEntity.ok(Map.of(
                "message", "Address deleted successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", e.getMessage()));
        }
    }

    // Inner classes for request bodies
    public static class UpdateProfileRequest {
        private String firstName;
        private String lastName;
        private String phone;

        // Getters and setters
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
    }

    public static class ChangePasswordRequest {
        private String currentPassword;
        private String newPassword;

        // Getters and setters
        public String getCurrentPassword() { return currentPassword; }
        public void setCurrentPassword(String currentPassword) { this.currentPassword = currentPassword; }
        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }

    public static class AddressRequest {
        private String addressLine1;
        private String addressLine2;
        private String city;
        private String state;
        private String zipCode;
        private String country;
        private boolean defaultAddress;

        // Getters and setters
        public String getAddressLine1() { return addressLine1; }
        public void setAddressLine1(String addressLine1) { this.addressLine1 = addressLine1; }
        public String getAddressLine2() { return addressLine2; }
        public void setAddressLine2(String addressLine2) { this.addressLine2 = addressLine2; }
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        public String getState() { return state; }
        public void setState(String state) { this.state = state; }
        public String getZipCode() { return zipCode; }
        public void setZipCode(String zipCode) { this.zipCode = zipCode; }
        public String getCountry() { return country; }
        public void setCountry(String country) { this.country = country; }
        public boolean isDefaultAddress() { return defaultAddress; }
        public void setDefaultAddress(boolean defaultAddress) { this.defaultAddress = defaultAddress; }
    }
}