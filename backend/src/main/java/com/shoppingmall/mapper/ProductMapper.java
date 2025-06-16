package com.shoppingmall.mapper;

import com.shoppingmall.model.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Mapper
public interface ProductMapper {
    
    // Create
    int insert(Product product);
    
    // Read
    Optional<Product> findById(@Param("id") Long id);
    List<Product> findAll(@Param("category") String category, 
                         @Param("minPrice") BigDecimal minPrice,
                         @Param("maxPrice") BigDecimal maxPrice,
                         @Param("sortBy") String sortBy,
                         @Param("sortOrder") String sortOrder,
                         @Param("limit") Integer limit,
                         @Param("offset") Integer offset);
    
    List<Product> findByCategory(@Param("category") String category);
    List<Product> searchByName(@Param("query") String query, @Param("limit") Integer limit);
    List<String> findAllCategories();
    int countProducts(@Param("category") String category, 
                     @Param("minPrice") BigDecimal minPrice,
                     @Param("maxPrice") BigDecimal maxPrice);
    
    // Update
    int update(Product product);
    int updateStock(@Param("id") Long id, @Param("quantity") Integer quantity);
    int decreaseStock(@Param("id") Long id, @Param("quantity") Integer quantity);
    int increaseStock(@Param("id") Long id, @Param("quantity") Integer quantity);
    int updateRating(@Param("id") Long id, @Param("rating") BigDecimal rating, @Param("reviewCount") Integer reviewCount);
    
    // Delete
    int deleteById(@Param("id") Long id);
    int deactivate(@Param("id") Long id);
    int activate(@Param("id") Long id);
    
    // Check availability
    boolean isAvailable(@Param("id") Long id, @Param("quantity") Integer quantity);
}