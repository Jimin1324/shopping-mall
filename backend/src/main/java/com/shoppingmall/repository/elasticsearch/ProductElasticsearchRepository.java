package com.shoppingmall.repository.elasticsearch;

import com.shoppingmall.document.ProductDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductElasticsearchRepository extends ElasticsearchRepository<ProductDocument, Long> {
    
    List<ProductDocument> findByNameContainingOrDescriptionContaining(String name, String description);
    
    List<ProductDocument> findByCategory(String category);
    
    List<ProductDocument> findByIsActiveTrue();
}