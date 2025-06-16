package com.shoppingmall.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CartItem {
    private Long id;
    private Long cartId;
    private Long productId;
    private Integer quantity;
    private String size;
    private LocalDateTime addedAt;
    
    // Additional fields for display purposes
    private Product product;

    // Constructors
    public CartItem() {
    }

    public CartItem(Long cartId, Long productId, Integer quantity) {
        this.cartId = cartId;
        this.productId = productId;
        this.quantity = quantity;
    }

    public CartItem(Long cartId, Long productId, Integer quantity, String size) {
        this.cartId = cartId;
        this.productId = productId;
        this.quantity = quantity;
        this.size = size;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCartId() {
        return cartId;
    }

    public void setCartId(Long cartId) {
        this.cartId = cartId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public LocalDateTime getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(LocalDateTime addedAt) {
        this.addedAt = addedAt;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    // Business methods
    public BigDecimal getSubtotal() {
        if (product != null && product.getPrice() != null && quantity != null) {
            return product.getPrice().multiply(BigDecimal.valueOf(quantity));
        }
        return BigDecimal.ZERO;
    }

    public boolean isValidQuantity() {
        return quantity != null && quantity > 0;
    }

    @Override
    public String toString() {
        return "CartItem{" +
                "id=" + id +
                ", cartId=" + cartId +
                ", productId=" + productId +
                ", quantity=" + quantity +
                ", size='" + size + '\'' +
                '}';
    }
}