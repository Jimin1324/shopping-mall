package com.shoppingmall.mapper;

import com.shoppingmall.model.Cart;
import com.shoppingmall.model.CartItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface CartMapper {
    
    // Cart operations
    int insertCart(Cart cart);
    Optional<Cart> findByUserId(@Param("userId") Long userId);
    int deleteCartByUserId(@Param("userId") Long userId);
    
    // Cart item operations
    int insertCartItem(CartItem cartItem);
    List<CartItem> findCartItemsByCartId(@Param("cartId") Long cartId);
    List<CartItem> findCartItemsWithProductsByCartId(@Param("cartId") Long cartId);
    Optional<CartItem> findCartItemById(@Param("id") Long id);
    Optional<CartItem> findCartItemByCartIdAndProductId(@Param("cartId") Long cartId, @Param("productId") Long productId);
    
    int updateCartItemQuantity(@Param("id") Long id, @Param("quantity") Integer quantity);
    int deleteCartItem(@Param("id") Long id);
    int deleteCartItemsByCartId(@Param("cartId") Long cartId);
    
    // Utility methods
    int getCartItemCount(@Param("cartId") Long cartId);
    boolean cartItemExists(@Param("cartId") Long cartId, @Param("productId") Long productId);
}