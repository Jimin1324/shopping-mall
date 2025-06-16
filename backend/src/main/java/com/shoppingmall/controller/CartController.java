package com.shoppingmall.controller;

import com.shoppingmall.model.Cart;
import com.shoppingmall.model.CartItem;
import com.shoppingmall.service.AuthService;
import com.shoppingmall.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "${cors.allowed-origins}", maxAge = 3600)
@PreAuthorize("hasRole('USER')")
public class CartController {

    private final CartService cartService;
    private final AuthService authService;

    @Autowired
    public CartController(CartService cartService, AuthService authService) {
        this.cartService = cartService;
        this.authService = authService;
    }

    @GetMapping
    public ResponseEntity<?> getCart() {
        try {
            Long userId = authService.getCurrentUserId();
            Optional<Cart> cartOpt = cartService.getCartWithItems(userId);
            
            if (cartOpt.isPresent()) {
                Cart cart = cartOpt.get();
                CartService.CartTotals totals = cartService.calculateCartTotals(userId);
                
                return ResponseEntity.ok(Map.of(
                    "cart", cart,
                    "totals", Map.of(
                        "subtotal", totals.getSubtotal(),
                        "tax", totals.getTax(),
                        "shippingFee", totals.getShippingFee(),
                        "total", totals.getTotal()
                    )
                ));
            } else {
                // Return empty cart
                return ResponseEntity.ok(Map.of(
                    "cart", Map.of("items", new Object[0]),
                    "totals", Map.of(
                        "subtotal", 0,
                        "tax", 0,
                        "shippingFee", 0,
                        "total", 0
                    )
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", "Error fetching cart: " + e.getMessage()));
        }
    }

    @PostMapping("/items")
    public ResponseEntity<?> addItemToCart(@Valid @RequestBody AddToCartRequest request) {
        try {
            Long userId = authService.getCurrentUserId();
            CartItem cartItem = cartService.addItemToCart(
                userId, 
                request.getProductId(), 
                request.getQuantity(), 
                request.getSize()
            );
            
            return ResponseEntity.ok(Map.of(
                "message", "Item added to cart successfully",
                "cartItem", cartItem
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<?> updateCartItem(
            @PathVariable Long itemId, 
            @Valid @RequestBody UpdateCartItemRequest request) {
        
        try {
            Long userId = authService.getCurrentUserId();
            CartItem cartItem = cartService.updateCartItemQuantity(
                userId, 
                itemId, 
                request.getQuantity()
            );
            
            if (cartItem != null) {
                return ResponseEntity.ok(Map.of(
                    "message", "Cart item updated successfully",
                    "cartItem", cartItem
                ));
            } else {
                return ResponseEntity.ok(Map.of(
                    "message", "Cart item removed successfully"
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<?> removeCartItem(@PathVariable Long itemId) {
        try {
            Long userId = authService.getCurrentUserId();
            cartService.removeCartItem(userId, itemId);
            
            return ResponseEntity.ok(Map.of(
                "message", "Item removed from cart successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping
    public ResponseEntity<?> clearCart() {
        try {
            Long userId = authService.getCurrentUserId();
            cartService.clearCart(userId);
            
            return ResponseEntity.ok(Map.of(
                "message", "Cart cleared successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/count")
    public ResponseEntity<?> getCartItemCount() {
        try {
            Long userId = authService.getCurrentUserId();
            int count = cartService.getCartItemCount(userId);
            
            return ResponseEntity.ok(Map.of("count", count));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", "Error fetching cart count: " + e.getMessage()));
        }
    }

    // Inner classes for request bodies
    public static class AddToCartRequest {
        private Long productId;
        private Integer quantity;
        private String size;

        // Getters and setters
        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
        public String getSize() { return size; }
        public void setSize(String size) { this.size = size; }
    }

    public static class UpdateCartItemRequest {
        private Integer quantity;

        // Getters and setters
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }
}