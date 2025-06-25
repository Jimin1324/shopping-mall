package com.shoppingmall.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shoppingmall.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class ProductElasticsearchService {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductElasticsearchService.class);
    
    @Value("${elasticsearch.url:http://elasticsearch:9200}")
    private String elasticsearchUrl;
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    public ProductElasticsearchService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Search products using Elasticsearch with multi-match query
     */
    public List<Product> searchProducts(String query, int size) {
        try {
            Map<String, Object> searchQuery = createSearchQuery(query, size);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(searchQuery, headers);
            
            String url = elasticsearchUrl + "/products/_search";
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                return parseSearchResponse(response.getBody());
            }
            
        } catch (Exception e) {
            logger.error("Elasticsearch search failed: ", e);
        }
        
        return new ArrayList<>();
    }
    
    /**
     * Index a product to Elasticsearch (called when product is created/updated)
     */
    public void indexProduct(Product product) {
        try {
            Map<String, Object> document = createProductDocument(product);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(document, headers);
            
            String url = elasticsearchUrl + "/products/_doc/" + product.getId();
            restTemplate.exchange(url, HttpMethod.PUT, request, String.class);
            
            logger.debug("Indexed product {} to Elasticsearch", product.getId());
            
        } catch (Exception e) {
            logger.error("Failed to index product {} to Elasticsearch: ", product.getId(), e);
        }
    }
    
    /**
     * Remove a product from Elasticsearch (called when product is deleted)
     */
    public void deleteProduct(Long productId) {
        try {
            String url = elasticsearchUrl + "/products/_doc/" + productId;
            restTemplate.delete(url);
            
            logger.debug("Deleted product {} from Elasticsearch", productId);
            
        } catch (Exception e) {
            logger.error("Failed to delete product {} from Elasticsearch: ", productId, e);
        }
    }
    
    /**
     * Check if Elasticsearch is available
     */
    public boolean isAvailable() {
        try {
            String url = elasticsearchUrl + "/_cluster/health";
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Bulk index all products (for admin/initial setup)
     */
    public int bulkIndexProducts(List<Product> products) {
        int indexedCount = 0;
        
        for (Product product : products) {
            try {
                indexProduct(product);
                indexedCount++;
            } catch (Exception e) {
                logger.error("Failed to index product {}: ", product.getId(), e);
            }
        }
        
        logger.info("Bulk indexed {} out of {} products", indexedCount, products.size());
        return indexedCount;
    }
    
    private Map<String, Object> createSearchQuery(String query, int size) {
        Map<String, Object> searchQuery = new HashMap<>();
        
        // Multi-match query with field boosting
        Map<String, Object> multiMatch = new HashMap<>();
        multiMatch.put("query", query);
        multiMatch.put("fields", Arrays.asList("name^2", "description"));
        multiMatch.put("type", "best_fields");
        multiMatch.put("fuzziness", "AUTO");
        
        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("multi_match", multiMatch);
        
        searchQuery.put("query", queryMap);
        searchQuery.put("size", size);
        searchQuery.put("sort", Arrays.asList("_score"));
        
        return searchQuery;
    }
    
    private Map<String, Object> createProductDocument(Product product) {
        Map<String, Object> document = new HashMap<>();
        document.put("id", product.getId());
        document.put("name", product.getName());
        document.put("description", product.getDescription());
        document.put("price", product.getPrice());
        document.put("category", product.getCategory());
        document.put("imageUrl", product.getImageUrl());
        document.put("stockQuantity", product.getStockQuantity());
        document.put("rating", product.getRating());
        document.put("reviewCount", product.getReviewCount());
        document.put("isActive", product.getIsActive());
        document.put("createdAt", product.getCreatedAt() != null ? product.getCreatedAt().toString() : null);
        document.put("updatedAt", product.getUpdatedAt() != null ? product.getUpdatedAt().toString() : null);
        
        return document;
    }
    
    private List<Product> parseSearchResponse(String responseBody) {
        List<Product> products = new ArrayList<>();
        
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode hits = root.path("hits").path("hits");
            
            for (JsonNode hit : hits) {
                JsonNode source = hit.path("_source");
                
                Product product = new Product();
                product.setId(source.path("id").asLong());
                product.setName(source.path("name").asText());
                product.setDescription(source.path("description").asText());
                product.setPrice(source.path("price").decimalValue());
                product.setCategory(source.path("category").asText());
                product.setImageUrl(source.path("imageUrl").asText());
                product.setStockQuantity(source.path("stockQuantity").asInt());
                product.setRating(source.path("rating").decimalValue());
                product.setReviewCount(source.path("reviewCount").asInt());
                product.setIsActive(source.path("isActive").asBoolean());
                
                products.add(product);
            }
            
        } catch (Exception e) {
            logger.error("Error parsing Elasticsearch response: ", e);
        }
        
        return products;
    }
}