package com.shoppingmall.mapper;

import com.shoppingmall.model.Order;
import com.shoppingmall.model.OrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface OrderMapper {
    
    // Order operations
    int insertOrder(Order order);
    Optional<Order> findById(@Param("id") Long id);
    Optional<Order> findByOrderNumber(@Param("orderNumber") String orderNumber);
    List<Order> findByUserId(@Param("userId") Long userId);
    List<Order> findByUserIdWithItems(@Param("userId") Long userId);
    List<Order> findAll(@Param("limit") Integer limit, @Param("offset") Integer offset);
    
    int updateOrderStatus(@Param("id") Long id, @Param("status") String status);
    int updateOrder(Order order);
    
    // Order item operations
    int insertOrderItem(OrderItem orderItem);
    List<OrderItem> findOrderItemsByOrderId(@Param("orderId") Long orderId);
    List<OrderItem> findOrderItemsWithProductsByOrderId(@Param("orderId") Long orderId);
    
    // Utility methods
    boolean orderNumberExists(@Param("orderNumber") String orderNumber);
    int countOrdersByUserId(@Param("userId") Long userId);
    int countAllOrders();
}