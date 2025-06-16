package com.shoppingmall.controller;

import com.shoppingmall.model.Product;
import com.shoppingmall.service.ProductService;
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
}