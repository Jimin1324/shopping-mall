package com.shoppingmall.service;

import com.shoppingmall.mapper.CartMapper;
import com.shoppingmall.mapper.ProductMapper;
import com.shoppingmall.model.Cart;
import com.shoppingmall.model.CartItem;
import com.shoppingmall.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CartService {

    private final CartMapper cartMapper;
    private final ProductMapper productMapper;

    @Autowired
    public CartService(CartMapper cartMapper, ProductMapper productMapper) {
        this.cartMapper = cartMapper;
        this.productMapper = productMapper;
    }

    /**
     * Get or create cart for user
     */
    public Cart getOrCreateCart(Long userId) {
        Optional<Cart> cartOpt = cartMapper.findByUserId(userId);
        
        if (cartOpt.isPresent()) {
            Cart cart = cartOpt.get();
            // Load cart items with product details
            List<CartItem> items = cartMapper.findCartItemsWithProductsByCartId(cart.getId());
            cart.setItems(items);
            return cart;
        } else {
            // Create new cart for user
            Cart newCart = new Cart(userId);
            int result = cartMapper.insertCart(newCart);
            if (result == 0) {
                throw new RuntimeException("Failed to create cart");
            }
            return newCart;
        }
    }

    /**
     * Add item to cart
     */
    public CartItem addItemToCart(Long userId, Long productId, Integer quantity, String size) {
        // Validate quantity
        if (quantity == null || quantity <= 0) {
            throw new RuntimeException("Invalid quantity");
        }

        // Check if product exists and is available
        Optional<Product> productOpt = productMapper.findById(productId);
        if (!productOpt.isPresent()) {
            throw new RuntimeException("Product not found");
        }

        Product product = productOpt.get();
        if (!product.isAvailable(quantity)) {
            throw new RuntimeException("Product not available in requested quantity");
        }

        // Get or create cart
        Cart cart = getOrCreateCart(userId);

        // Check if item already exists in cart
        Optional<CartItem> existingItemOpt = cartMapper.findCartItemByCartIdAndProductId(cart.getId(), productId);
        
        if (existingItemOpt.isPresent()) {
            // Update existing item quantity
            CartItem existingItem = existingItemOpt.get();
            int newQuantity = existingItem.getQuantity() + quantity;
            
            // Check if total quantity is available
            if (!product.isAvailable(newQuantity)) {
                throw new RuntimeException("Product not available in total requested quantity");
            }
            
            int result = cartMapper.updateCartItemQuantity(existingItem.getId(), newQuantity);
            if (result == 0) {
                throw new RuntimeException("Failed to update cart item");
            }
            
            existingItem.setQuantity(newQuantity);
            existingItem.setProduct(product);
            return existingItem;
        } else {
            // Create new cart item
            CartItem cartItem = new CartItem(cart.getId(), productId, quantity, size);
            int result = cartMapper.insertCartItem(cartItem);
            if (result == 0) {
                throw new RuntimeException("Failed to add item to cart");
            }
            
            cartItem.setProduct(product);
            return cartItem;
        }
    }

    /**
     * Update cart item quantity
     */
    public CartItem updateCartItemQuantity(Long userId, Long cartItemId, Integer quantity) {
        // Validate quantity
        if (quantity == null || quantity < 0) {
            throw new RuntimeException("Invalid quantity");
        }

        // If quantity is 0, remove the item
        if (quantity == 0) {
            removeCartItem(userId, cartItemId);
            return null;
        }

        // Find cart item
        Optional<CartItem> cartItemOpt = cartMapper.findCartItemById(cartItemId);
        if (!cartItemOpt.isPresent()) {
            throw new RuntimeException("Cart item not found");
        }

        CartItem cartItem = cartItemOpt.get();

        // Verify cart belongs to user
        Optional<Cart> cartOpt = cartMapper.findByUserId(userId);
        if (!cartOpt.isPresent() || !cartOpt.get().getId().equals(cartItem.getCartId())) {
            throw new RuntimeException("Cart item does not belong to user");
        }

        // Check product availability
        Optional<Product> productOpt = productMapper.findById(cartItem.getProductId());
        if (!productOpt.isPresent()) {
            throw new RuntimeException("Product not found");
        }

        Product product = productOpt.get();
        if (!product.isAvailable(quantity)) {
            throw new RuntimeException("Product not available in requested quantity");
        }

        // Update quantity
        int result = cartMapper.updateCartItemQuantity(cartItemId, quantity);
        if (result == 0) {
            throw new RuntimeException("Failed to update cart item");
        }

        cartItem.setQuantity(quantity);
        cartItem.setProduct(product);
        return cartItem;
    }

    /**
     * Remove item from cart
     */
    public void removeCartItem(Long userId, Long cartItemId) {
        // Find cart item
        Optional<CartItem> cartItemOpt = cartMapper.findCartItemById(cartItemId);
        if (!cartItemOpt.isPresent()) {
            throw new RuntimeException("Cart item not found");
        }

        CartItem cartItem = cartItemOpt.get();

        // Verify cart belongs to user
        Optional<Cart> cartOpt = cartMapper.findByUserId(userId);
        if (!cartOpt.isPresent() || !cartOpt.get().getId().equals(cartItem.getCartId())) {
            throw new RuntimeException("Cart item does not belong to user");
        }

        // Remove item
        int result = cartMapper.deleteCartItem(cartItemId);
        if (result == 0) {
            throw new RuntimeException("Failed to remove cart item");
        }
    }

    /**
     * Clear entire cart
     */
    public void clearCart(Long userId) {
        Optional<Cart> cartOpt = cartMapper.findByUserId(userId);
        if (cartOpt.isPresent()) {
            int result = cartMapper.deleteCartItemsByCartId(cartOpt.get().getId());
            // Note: We don't check result here as clearing an empty cart is valid
        }
    }

    /**
     * Get cart with all items and product details
     */
    @Transactional(readOnly = true)
    public Optional<Cart> getCartWithItems(Long userId) {
        Optional<Cart> cartOpt = cartMapper.findByUserId(userId);
        if (cartOpt.isPresent()) {
            Cart cart = cartOpt.get();
            List<CartItem> items = cartMapper.findCartItemsWithProductsByCartId(cart.getId());
            cart.setItems(items);
            return Optional.of(cart);
        }
        return Optional.empty();
    }

    /**
     * Calculate cart totals
     */
    @Transactional(readOnly = true)
    public CartTotals calculateCartTotals(Long userId) {
        Optional<Cart> cartOpt = getCartWithItems(userId);
        if (!cartOpt.isPresent() || cartOpt.get().isEmpty()) {
            return new CartTotals(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        }

        Cart cart = cartOpt.get();
        BigDecimal subtotal = cart.getItems().stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal tax = subtotal.multiply(BigDecimal.valueOf(0.08)); // 8% tax
        BigDecimal shippingFee = subtotal.compareTo(BigDecimal.valueOf(100)) >= 0 ? 
                BigDecimal.ZERO : BigDecimal.valueOf(10); // Free shipping over $100
        BigDecimal total = subtotal.add(tax).add(shippingFee);

        return new CartTotals(subtotal, tax, shippingFee, total);
    }

    /**
     * Get cart item count for user
     */
    @Transactional(readOnly = true)
    public int getCartItemCount(Long userId) {
        Optional<Cart> cartOpt = cartMapper.findByUserId(userId);
        if (cartOpt.isPresent()) {
            return cartMapper.getCartItemCount(cartOpt.get().getId());
        }
        return 0;
    }

    // Inner class for cart totals
    public static class CartTotals {
        private final BigDecimal subtotal;
        private final BigDecimal tax;
        private final BigDecimal shippingFee;
        private final BigDecimal total;

        public CartTotals(BigDecimal subtotal, BigDecimal tax, BigDecimal shippingFee, BigDecimal total) {
            this.subtotal = subtotal;
            this.tax = tax;
            this.shippingFee = shippingFee;
            this.total = total;
        }

        // Getters
        public BigDecimal getSubtotal() { return subtotal; }
        public BigDecimal getTax() { return tax; }
        public BigDecimal getShippingFee() { return shippingFee; }
        public BigDecimal getTotal() { return total; }
    }
}