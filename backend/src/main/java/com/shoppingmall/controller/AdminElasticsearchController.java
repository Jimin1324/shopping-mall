package com.shoppingmall.controller;

import com.shoppingmall.model.Product;
import com.shoppingmall.service.ProductElasticsearchService;
import com.shoppingmall.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/elasticsearch")
@CrossOrigin(origins = "${cors.allowed-origins}", maxAge = 3600)
public class AdminElasticsearchController {

    @Autowired
    private ProductElasticsearchService elasticsearchService;
    
    @Autowired
    private ProductService productService;

    /**
     * Bulk index all products to Elasticsearch (for initial setup or re-indexing)
     */
    @PostMapping("/reindex-products")
    public ResponseEntity<?> reindexAllProducts() {
        try {
            // Get all products from MySQL
            List<Product> products = productService.getAllProducts(null, null, null, "id", "asc", 1, 1000);
            
            if (products.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "No products found in database");
                response.put("success", false);
                return ResponseEntity.ok(response);
            }
            
            // Bulk index to Elasticsearch
            int indexedCount = elasticsearchService.bulkIndexProducts(products);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Products reindexed successfully");
            response.put("totalProducts", products.size());
            response.put("indexedCount", indexedCount);
            response.put("success", true);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Failed to reindex products");
            response.put("error", e.getMessage());
            response.put("success", false);
            
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Check Elasticsearch status and connection
     */
    @GetMapping("/status")
    public ResponseEntity<?> getElasticsearchStatus() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            boolean isAvailable = elasticsearchService.isAvailable();
            response.put("available", isAvailable);
            response.put("status", isAvailable ? "connected" : "disconnected");
            response.put("service", "ProductElasticsearchService");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("available", false);
            response.put("status", "error");
            response.put("error", e.getMessage());
            
            return ResponseEntity.status(500).body(response);
        }
    }
}