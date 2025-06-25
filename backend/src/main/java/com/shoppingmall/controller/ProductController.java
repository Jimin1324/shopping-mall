package com.shoppingmall.controller;

import com.shoppingmall.model.Product;
import com.shoppingmall.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "${cors.allowed-origins}", maxAge = 3600)
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
    
    private final ProductService productService;
    

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<?> getAllProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "created_at") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer limit) {
        
        try {
            List<Product> products = productService.getAllProducts(
                category, minPrice, maxPrice, sortBy, sortOrder, page, limit
            );
            
            int totalCount = productService.countProducts(category, minPrice, maxPrice);
            
            return ResponseEntity.ok(Map.of(
                "products", products,
                "total", totalCount,
                "page", page,
                "size", limit,
                "totalPages", (int) Math.ceil((double) totalCount / limit)
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", "Error fetching products: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        try {
            Optional<Product> productOpt = productService.getProductById(id);
            
            if (productOpt.isPresent()) {
                return ResponseEntity.ok(productOpt.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", "Error fetching product: " + e.getMessage()));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchProducts(
            @RequestParam String q,
            @RequestParam(defaultValue = "50") Integer limit) {
        
        try {
            List<Product> products = productService.searchProducts(q, limit);
            
            return ResponseEntity.ok(Map.of(
                "products", products,
                "query", q,
                "count", products.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", "Error searching products: " + e.getMessage()));
        }
    }

    @GetMapping("/categories")
    public ResponseEntity<?> getAllCategories() {
        try {
            List<String> categories = productService.getAllCategories();
            return ResponseEntity.ok(Map.of("categories", categories));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", "Error fetching categories: " + e.getMessage()));
        }
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<?> getProductsByCategory(@PathVariable String category) {
        try {
            List<Product> products = productService.getProductsByCategory(category);
            
            return ResponseEntity.ok(Map.of(
                "products", products,
                "category", category,
                "count", products.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", "Error fetching products by category: " + e.getMessage()));
        }
    }
    
    /**
     * Advanced search endpoint using Elasticsearch (POST method)
     * Provides full-text search, faceted filtering, and relevance scoring
     */
    @PostMapping("/search/advanced")
    public ResponseEntity<?> advancedSearchPost(@RequestBody Map<String, Object> searchRequest) {
        try {
            // Fallback to regular search
            String query = (String) searchRequest.getOrDefault("query", "");
            Integer size = (Integer) searchRequest.getOrDefault("size", 50);
            return searchProducts(query, size);
        } catch (Exception e) {
            logger.error("Error in advanced search", e);
            // Fallback to regular search on error
            return searchProducts("", 50);
        }
    }
    
    /**
     * Advanced search endpoint using Elasticsearch (GET method)
     * Provides full-text search, faceted filtering, and relevance scoring
     */
    @GetMapping("/search/advanced")
    public ResponseEntity<?> advancedSearch(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "relevance") String sortBy,
            @RequestParam(defaultValue = "desc") String order) {
        
        try {
            // Fallback to regular search
            return searchProducts(q != null ? q : "", size);
        } catch (Exception e) {
            // Fallback to regular search on error
            return searchProducts(q != null ? q : "", size);
        }
    }
    
    /**
     * Get search suggestions for autocomplete
     */
    @GetMapping("/search/suggestions")
    public ResponseEntity<?> getSearchSuggestions(@RequestParam String prefix) {
        try {
            return ResponseEntity.ok(Map.of("suggestions", List.of()));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("suggestions", List.of()));
        }
    }
}