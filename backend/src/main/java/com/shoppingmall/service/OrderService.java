package com.shoppingmall.service;

import com.shoppingmall.mapper.OrderMapper;
import com.shoppingmall.model.Cart;
import com.shoppingmall.model.CartItem;
import com.shoppingmall.model.Order;
import com.shoppingmall.model.OrderItem;
import com.shoppingmall.util.OrderNumberGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OrderService {

    private final OrderMapper orderMapper;
    private final CartService cartService;
    private final ProductService productService;
    private final OrderNumberGenerator orderNumberGenerator;

    @Autowired
    public OrderService(OrderMapper orderMapper, CartService cartService, 
                       ProductService productService, OrderNumberGenerator orderNumberGenerator) {
        this.orderMapper = orderMapper;
        this.cartService = cartService;
        this.productService = productService;
        this.orderNumberGenerator = orderNumberGenerator;
    }

    /**
     * Create order from cart
     */
    public Order createOrderFromCart(Long userId, String shippingAddress, String paymentMethod) {
        // Get cart with items
        Optional<Cart> cartOpt = cartService.getCartWithItems(userId);
        if (!cartOpt.isPresent() || cartOpt.get().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        Cart cart = cartOpt.get();
        
        // Calculate totals
        CartService.CartTotals totals = cartService.calculateCartTotals(userId);

        // Generate unique order number
        String orderNumber = orderNumberGenerator.generateUniqueOrderNumber();
        
        // Validate order number uniqueness (in case of collision)
        while (orderMapper.orderNumberExists(orderNumber)) {
            orderNumber = orderNumberGenerator.generateUniqueOrderNumber();
        }

        // Create order
        Order order = new Order(userId, orderNumber, totals.getSubtotal(), 
                               totals.getTax(), totals.getShippingFee());
        order.setShippingAddress(shippingAddress);
        order.setPaymentMethod(paymentMethod);

        int result = orderMapper.insertOrder(order);
        if (result == 0) {
            throw new RuntimeException("Failed to create order");
        }

        // Create order items and decrease stock
        for (CartItem cartItem : cart.getItems()) {
            // Check stock availability one more time
            if (!productService.isProductAvailable(cartItem.getProductId(), cartItem.getQuantity())) {
                throw new RuntimeException("Product " + cartItem.getProduct().getName() + 
                                         " is no longer available in requested quantity");
            }

            // Decrease stock
            boolean stockDecreased = productService.decreaseStock(cartItem.getProductId(), cartItem.getQuantity());
            if (!stockDecreased) {
                throw new RuntimeException("Failed to reserve stock for product " + cartItem.getProduct().getName());
            }

            // Create order item
            OrderItem orderItem = new OrderItem(order.getId(), cartItem.getProductId(), 
                                               cartItem.getQuantity(), cartItem.getSize(), 
                                               cartItem.getProduct().getPrice());
            
            int orderItemResult = orderMapper.insertOrderItem(orderItem);
            if (orderItemResult == 0) {
                throw new RuntimeException("Failed to create order item");
            }
        }

        // Clear cart after successful order creation
        cartService.clearCart(userId);

        // Load order items for return
        List<OrderItem> orderItems = orderMapper.findOrderItemsWithProductsByOrderId(order.getId());
        order.setItems(orderItems);

        return order;
    }

    /**
     * Get order by ID
     */
    @Transactional(readOnly = true)
    public Optional<Order> getOrderById(Long orderId) {
        Optional<Order> orderOpt = orderMapper.findById(orderId);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            List<OrderItem> items = orderMapper.findOrderItemsWithProductsByOrderId(orderId);
            order.setItems(items);
            return Optional.of(order);
        }
        return Optional.empty();
    }

    /**
     * Get order by order number
     */
    @Transactional(readOnly = true)
    public Optional<Order> getOrderByOrderNumber(String orderNumber) {
        Optional<Order> orderOpt = orderMapper.findByOrderNumber(orderNumber);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            List<OrderItem> items = orderMapper.findOrderItemsWithProductsByOrderId(order.getId());
            order.setItems(items);
            return Optional.of(order);
        }
        return Optional.empty();
    }

    /**
     * Get all orders for user
     */
    @Transactional(readOnly = true)
    public List<Order> getUserOrders(Long userId) {
        List<Order> orders = orderMapper.findByUserId(userId);
        
        // Load items for each order
        for (Order order : orders) {
            List<OrderItem> items = orderMapper.findOrderItemsWithProductsByOrderId(order.getId());
            order.setItems(items);
        }
        
        return orders;
    }

    /**
     * Update order status
     */
    public boolean updateOrderStatus(Long orderId, Order.OrderStatus newStatus) {
        Optional<Order> orderOpt = orderMapper.findById(orderId);
        if (!orderOpt.isPresent()) {
            return false;
        }

        Order order = orderOpt.get();
        Order.OrderStatus currentStatus = order.getStatus();

        // Validate status transition
        if (!isValidStatusTransition(currentStatus, newStatus)) {
            throw new RuntimeException("Invalid status transition from " + currentStatus + " to " + newStatus);
        }

        int result = orderMapper.updateOrderStatus(orderId, newStatus.name());
        return result > 0;
    }

    /**
     * Cancel order
     */
    public boolean cancelOrder(Long orderId, Long userId) {
        Optional<Order> orderOpt = orderMapper.findById(orderId);
        if (!orderOpt.isPresent()) {
            return false;
        }

        Order order = orderOpt.get();

        // Verify order belongs to user
        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("Order does not belong to user");
        }

        // Check if order can be cancelled
        if (!order.canBeCancelled()) {
            throw new RuntimeException("Order cannot be cancelled in current status: " + order.getStatus());
        }

        // Restore stock for order items
        List<OrderItem> orderItems = orderMapper.findOrderItemsByOrderId(orderId);
        for (OrderItem item : orderItems) {
            productService.increaseStock(item.getProductId(), item.getQuantity());
        }

        // Update order status
        int result = orderMapper.updateOrderStatus(orderId, Order.OrderStatus.CANCELLED.name());
        return result > 0;
    }

    /**
     * Get order count for user
     */
    @Transactional(readOnly = true)
    public int getUserOrderCount(Long userId) {
        return orderMapper.countOrdersByUserId(userId);
    }

    /**
     * Get all orders with pagination (admin function)
     */
    @Transactional(readOnly = true)
    public List<Order> getAllOrders(Integer page, Integer size) {
        Integer offset = null;
        Integer limit = null;
        
        if (page != null && size != null && page > 0 && size > 0) {
            offset = (page - 1) * size;
            limit = size;
        }

        List<Order> orders = orderMapper.findAll(limit, offset);
        
        // Load items for each order
        for (Order order : orders) {
            List<OrderItem> items = orderMapper.findOrderItemsWithProductsByOrderId(order.getId());
            order.setItems(items);
        }
        
        return orders;
    }

    /**
     * Get total order count (admin function)
     */
    @Transactional(readOnly = true)
    public int getTotalOrderCount() {
        return orderMapper.countAllOrders();
    }

    /**
     * Validate order status transitions
     */
    private boolean isValidStatusTransition(Order.OrderStatus current, Order.OrderStatus next) {
        switch (current) {
            case PENDING:
                return next == Order.OrderStatus.CONFIRMED || next == Order.OrderStatus.CANCELLED;
            case CONFIRMED:
                return next == Order.OrderStatus.SHIPPED || next == Order.OrderStatus.CANCELLED;
            case SHIPPED:
                return next == Order.OrderStatus.DELIVERED;
            case DELIVERED:
                return next == Order.OrderStatus.REFUNDED;
            case CANCELLED:
            case REFUNDED:
                return false; // Terminal states
            default:
                return false;
        }
    }
}