package com.shoppingmall.service;

import com.shoppingmall.mapper.ProductMapper;
import com.shoppingmall.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);
    
    private final ProductMapper productMapper;
    
    @Autowired(required = false)
    private ProductElasticsearchService elasticsearchService;

    @Autowired
    public ProductService(ProductMapper productMapper) {
        this.productMapper = productMapper;
    }

    /**
     * Get all products with filtering, sorting, and pagination
     */
    @Transactional(readOnly = true)
    public List<Product> getAllProducts(String category, BigDecimal minPrice, BigDecimal maxPrice, 
                                       String sortBy, String sortOrder, Integer page, Integer size) {
        
        // Calculate offset for pagination
        Integer offset = null;
        Integer limit = null;
        
        if (page != null && size != null && page > 0 && size > 0) {
            offset = (page - 1) * size;
            limit = size;
        }

        // Validate sort parameters
        if (sortBy == null || sortBy.trim().isEmpty()) {
            sortBy = "created_at";
        }
        if (sortOrder == null || (!sortOrder.equalsIgnoreCase("asc") && !sortOrder.equalsIgnoreCase("desc"))) {
            sortOrder = "desc";
        }

        return productMapper.findAll(category, minPrice, maxPrice, sortBy, sortOrder, limit, offset);
    }

    /**
     * Get product by ID
     */
    @Transactional(readOnly = true)
    public Optional<Product> getProductById(Long id) {
        return productMapper.findById(id);
    }

    /**
     * Get products by category
     */
    @Transactional(readOnly = true)
    public List<Product> getProductsByCategory(String category) {
        return productMapper.findByCategory(category);
    }

    /**
     * Search products by name or description
     * Uses Elasticsearch if available, falls back to MySQL
     */
    @Transactional(readOnly = true)
    public List<Product> searchProducts(String query, Integer limit) {
        if (query == null || query.trim().isEmpty()) {
            return List.of();
        }
        
        if (limit == null || limit <= 0) {
            limit = 50; // Default limit
        }
        
        // Try to use Elasticsearch if available
        if (elasticsearchService != null && elasticsearchService.isAvailable()) {
            try {
                List<Product> elasticResults = elasticsearchService.searchProducts(query.trim(), limit);
                if (!elasticResults.isEmpty()) {
                    return elasticResults;
                }
            } catch (Exception e) {
                // Log error and fall back to MySQL
                System.err.println("Elasticsearch search failed, falling back to MySQL: " + e.getMessage());
            }
        }
        
        // Fallback to MySQL search
        return productMapper.searchByName(query.trim(), limit);
    }

    /**
     * Get all available categories
     */
    @Transactional(readOnly = true)
    public List<String> getAllCategories() {
        return productMapper.findAllCategories();
    }

    /**
     * Count products with filters
     */
    @Transactional(readOnly = true)
    public int countProducts(String category, BigDecimal minPrice, BigDecimal maxPrice) {
        return productMapper.countProducts(category, minPrice, maxPrice);
    }

    /**
     * Check if product is available in the requested quantity
     */
    @Transactional(readOnly = true)
    public boolean isProductAvailable(Long productId, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            return false;
        }
        return productMapper.isAvailable(productId, quantity);
    }

    /**
     * Decrease product stock (used when creating orders)
     */
    public boolean decreaseStock(Long productId, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            return false;
        }

        // Check availability first
        if (!isProductAvailable(productId, quantity)) {
            return false;
        }

        int result = productMapper.decreaseStock(productId, quantity);
        return result > 0;
    }

    /**
     * Create a new product (for sellers/admin)
     * Automatically indexes to Elasticsearch
     */
    public Product createProduct(Product product) {
        // Save to MySQL first
        productMapper.insert(product);
        
        // Index to Elasticsearch if available
        if (elasticsearchService != null && elasticsearchService.isAvailable()) {
            try {
                elasticsearchService.indexProduct(product);
            } catch (Exception e) {
                logger.warn("Failed to index new product {} to Elasticsearch: {}", product.getId(), e.getMessage());
            }
        }
        
        return product;
    }
    
    /**
     * Update an existing product (for sellers/admin)
     * Automatically updates in Elasticsearch
     */
    public Product updateProduct(Product product) {
        // Update in MySQL first
        productMapper.update(product);
        
        // Update in Elasticsearch if available
        if (elasticsearchService != null && elasticsearchService.isAvailable()) {
            try {
                elasticsearchService.indexProduct(product);
            } catch (Exception e) {
                logger.warn("Failed to update product {} in Elasticsearch: {}", product.getId(), e.getMessage());
            }
        }
        
        return product;
    }
    
    /**
     * Delete a product (for sellers/admin)
     * Automatically removes from Elasticsearch
     */
    public boolean deleteProduct(Long productId) {
        // Delete from MySQL first
        int result = productMapper.deleteById(productId);
        boolean deleted = result > 0;
        
        // Remove from Elasticsearch if available
        if (deleted && elasticsearchService != null && elasticsearchService.isAvailable()) {
            try {
                elasticsearchService.deleteProduct(productId);
            } catch (Exception e) {
                logger.warn("Failed to delete product {} from Elasticsearch: {}", productId, e.getMessage());
            }
        }
        
        return deleted;
    }

    /**
     * Increase product stock (used when canceling orders)
     */
    public boolean increaseStock(Long productId, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            return false;
        }

        int result = productMapper.increaseStock(productId, quantity);
        return result > 0;
    }

    /**
     * Update product stock directly
     */
    public boolean updateStock(Long productId, Integer newQuantity) {
        if (newQuantity == null || newQuantity < 0) {
            return false;
        }

        int result = productMapper.updateStock(productId, newQuantity);
        return result > 0;
    }

    /**
     * Create a new product (admin/seller function)
     */
    public Product createProduct(String name, String description, BigDecimal price, 
                               String category, String imageUrl, Integer stockQuantity) {
        
        Product product = new Product(name, description, price, category);
        product.setImageUrl(imageUrl);
        product.setStockQuantity(stockQuantity != null ? stockQuantity : 0);

        int result = productMapper.insert(product);
        if (result == 0) {
            throw new RuntimeException("Failed to create product");
        }

        return product;
    }

    /**
     * Update existing product (admin/seller function)
     */
    public Optional<Product> updateProduct(Long productId, String name, String description, 
                                         BigDecimal price, String category, String imageUrl, 
                                         Integer stockQuantity) {
        
        Optional<Product> productOpt = productMapper.findById(productId);
        if (!productOpt.isPresent()) {
            return Optional.empty();
        }

        Product product = productOpt.get();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setCategory(category);
        product.setImageUrl(imageUrl);
        product.setStockQuantity(stockQuantity);

        int result = productMapper.update(product);
        if (result == 0) {
            throw new RuntimeException("Failed to update product");
        }

        return Optional.of(product);
    }

    /**
     * Update product rating and review count
     */
    public boolean updateProductRating(Long productId, BigDecimal rating, Integer reviewCount) {
        int result = productMapper.updateRating(productId, rating, reviewCount);
        return result > 0;
    }

    /**
     * Deactivate product
     */
    public boolean deactivateProduct(Long productId) {
        int result = productMapper.deactivate(productId);
        return result > 0;
    }

    /**
     * Activate product
     */
    public boolean activateProduct(Long productId) {
        int result = productMapper.activate(productId);
        return result > 0;
    }

}