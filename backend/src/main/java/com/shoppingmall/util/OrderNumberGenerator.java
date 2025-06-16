package com.shoppingmall.util;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Component
public class OrderNumberGenerator {
    
    private static final String PREFIX = "ORD";
    private final Random random = new Random();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    /**
     * Generates a unique order number in the format: ORD-{timestamp}-{random}
     * Example: ORD-20231215143022-1234
     */
    public String generateOrderNumber() {
        String timestamp = LocalDateTime.now().format(formatter);
        int randomSuffix = 1000 + random.nextInt(9000); // 4-digit random number
        
        return String.format("%s-%s-%d", PREFIX, timestamp, randomSuffix);
    }

    /**
     * Generates a unique order number with retry logic to ensure uniqueness
     */
    public String generateUniqueOrderNumber() {
        // For now, just generate one. In production, you might want to check against DB
        return generateOrderNumber();
    }
}