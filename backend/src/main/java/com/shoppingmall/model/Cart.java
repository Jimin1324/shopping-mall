package com.shoppingmall.model;

import java.time.LocalDateTime;
import java.util.List;

public class Cart {
    private Long id;
    private Long userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<CartItem> items;

    // Constructors
    public Cart() {
    }

    public Cart(Long userId) {
        this.userId = userId;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    // Business methods
    public int getTotalItems() {
        return items != null ? items.stream()
                .mapToInt(CartItem::getQuantity)
                .sum() : 0;
    }

    public boolean isEmpty() {
        return items == null || items.isEmpty();
    }

    @Override
    public String toString() {
        return "Cart{" +
                "id=" + id +
                ", userId=" + userId +
                ", totalItems=" + getTotalItems() +
                '}';
    }
}