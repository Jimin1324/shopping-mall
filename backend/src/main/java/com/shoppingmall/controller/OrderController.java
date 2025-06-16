package com.shoppingmall.controller;

import com.shoppingmall.model.Order;
import com.shoppingmall.service.AuthService;
import com.shoppingmall.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "${cors.allowed-origins}", maxAge = 3600)
@PreAuthorize("hasRole('USER')")
public class OrderController {

    private final OrderService orderService;
    private final AuthService authService;

    @Autowired
    public OrderController(OrderService orderService, AuthService authService) {
        this.orderService = orderService;
        this.authService = authService;
    }

    @PostMapping
    public ResponseEntity<?> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        try {
            Long userId = authService.getCurrentUserId();
            Order order = orderService.createOrderFromCart(
                userId,
                request.getShippingAddress(),
                request.getPaymentMethod()
            );
            
            return ResponseEntity.ok(Map.of(
                "orderId", order.getOrderNumber(),
                "message", "Order created successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> getUserOrders() {
        try {
            Long userId = authService.getCurrentUserId();
            List<Order> orders = orderService.getUserOrders(userId);
            
            return ResponseEntity.ok(Map.of(
                "orders", orders,
                "count", orders.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", "Error fetching orders: " + e.getMessage()));
        }
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrderById(@PathVariable String orderId) {
        try {
            Long userId = authService.getCurrentUserId();
            Optional<Order> orderOpt = orderService.getOrderByOrderNumber(orderId);
            
            if (orderOpt.isPresent()) {
                Order order = orderOpt.get();
                // Verify order belongs to user
                if (!order.getUserId().equals(userId)) {
                    return ResponseEntity.status(403)
                        .body(Map.of("message", "Access denied"));
                }
                return ResponseEntity.ok(order);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", "Error fetching order: " + e.getMessage()));
        }
    }

    @GetMapping("/number/{orderNumber}")
    public ResponseEntity<?> getOrderByOrderNumber(@PathVariable String orderNumber) {
        try {
            Long userId = authService.getCurrentUserId();
            Optional<Order> orderOpt = orderService.getOrderByOrderNumber(orderNumber);
            
            if (orderOpt.isPresent()) {
                Order order = orderOpt.get();
                // Verify order belongs to user
                if (!order.getUserId().equals(userId)) {
                    return ResponseEntity.status(403)
                        .body(Map.of("message", "Access denied"));
                }
                return ResponseEntity.ok(order);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", "Error fetching order: " + e.getMessage()));
        }
    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable String orderId) {
        try {
            Long userId = authService.getCurrentUserId();
            boolean cancelled = orderService.cancelOrderByOrderNumber(orderId, userId);
            
            if (cancelled) {
                return ResponseEntity.ok(Map.of(
                    "message", "Order cancelled successfully"
                ));
            } else {
                return ResponseEntity.badRequest()
                    .body(Map.of("message", "Failed to cancel order"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/count")
    public ResponseEntity<?> getUserOrderCount() {
        try {
            Long userId = authService.getCurrentUserId();
            int count = orderService.getUserOrderCount(userId);
            
            return ResponseEntity.ok(Map.of("count", count));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", "Error fetching order count: " + e.getMessage()));
        }
    }

    // Inner class for request body
    public static class CreateOrderRequest {
        private String shippingAddress;
        private String paymentMethod;

        // Getters and setters
        public String getShippingAddress() { return shippingAddress; }
        public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }
        public String getPaymentMethod() { return paymentMethod; }
        public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    }
}