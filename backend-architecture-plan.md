# Backend Architecture Plan for Shopping Mall Application

## Overview
This document outlines the backend structure for the customer-facing shopping mall application. The backend will be built using Spring Boot with MyBatis for database operations.

## Database Schema

### Users Table
```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE
);
```

### Products Table
```sql
CREATE TABLE products (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    category VARCHAR(100) NOT NULL,
    image_url VARCHAR(500),
    stock_quantity INT DEFAULT 0,
    rating DECIMAL(3,2) DEFAULT 0,
    review_count INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE
);
```

### Shopping Cart Tables
```sql
CREATE TABLE cart (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE cart_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    cart_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    size VARCHAR(20),
    added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (cart_id) REFERENCES cart(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
);
```

### Orders Tables
```sql
CREATE TABLE orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    order_number VARCHAR(50) UNIQUE NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    subtotal DECIMAL(10,2) NOT NULL,
    tax DECIMAL(10,2) NOT NULL,
    shipping_fee DECIMAL(10,2) NOT NULL,
    total DECIMAL(10,2) NOT NULL,
    shipping_address JSON,
    payment_method VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE order_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    size VARCHAR(20),
    price DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
);
```

### User Addresses Table
```sql
CREATE TABLE user_addresses (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    address_line1 VARCHAR(255) NOT NULL,
    address_line2 VARCHAR(255),
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    zip_code VARCHAR(20) NOT NULL,
    country VARCHAR(100) NOT NULL DEFAULT 'US',
    is_default BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

## Backend Package Structure

```
com.shoppingmall/
├── ShoppingMallApplication.java
├── config/
│   ├── DatabaseConfig.java
│   ├── SecurityConfig.java
│   ├── WebConfig.java
│   └── MyBatisConfig.java
├── controller/
│   ├── AuthController.java
│   ├── ProductController.java
│   ├── CartController.java
│   ├── OrderController.java
│   └── UserController.java
├── service/
│   ├── AuthService.java
│   ├── ProductService.java
│   ├── CartService.java
│   ├── OrderService.java
│   └── UserService.java
├── mapper/
│   ├── UserMapper.java
│   ├── ProductMapper.java
│   ├── CartMapper.java
│   └── OrderMapper.java
├── model/
│   ├── User.java
│   ├── Product.java
│   ├── Cart.java
│   ├── CartItem.java
│   ├── Order.java
│   ├── OrderItem.java
│   └── UserAddress.java
├── dto/
│   ├── request/
│   │   ├── LoginRequest.java
│   │   ├── RegisterRequest.java
│   │   ├── AddToCartRequest.java
│   │   ├── CreateOrderRequest.java
│   │   └── UpdateProfileRequest.java
│   └── response/
│       ├── AuthResponse.java
│       ├── ProductResponse.java
│       ├── CartResponse.java
│       └── OrderResponse.java
├── security/
│   ├── JwtTokenProvider.java
│   ├── JwtAuthenticationFilter.java
│   └── UserDetailsServiceImpl.java
├── exception/
│   ├── GlobalExceptionHandler.java
│   ├── ResourceNotFoundException.java
│   ├── BadRequestException.java
│   └── UnauthorizedException.java
└── util/
    ├── OrderNumberGenerator.java
    └── PasswordEncoder.java
```

## API Endpoints

### Authentication Endpoints
- `POST /api/auth/login` - User login
- `POST /api/auth/register` - User registration
- `POST /api/auth/forgot-password` - Request password reset
- `POST /api/auth/reset-password` - Reset password with token
- `POST /api/auth/refresh` - Refresh JWT token
- `POST /api/auth/logout` - User logout

### Product Endpoints
- `GET /api/products` - Get all products (with pagination, filtering, sorting)
- `GET /api/products/{id}` - Get product by ID
- `GET /api/products/search` - Search products
- `GET /api/products/categories` - Get all categories

### Cart Endpoints
- `GET /api/cart` - Get current user's cart
- `POST /api/cart/items` - Add item to cart
- `PUT /api/cart/items/{itemId}` - Update cart item quantity
- `DELETE /api/cart/items/{itemId}` - Remove item from cart
- `DELETE /api/cart` - Clear entire cart

### Order Endpoints
- `POST /api/orders` - Create new order
- `GET /api/orders` - Get user's orders
- `GET /api/orders/{orderId}` - Get order details
- `POST /api/orders/{orderId}/cancel` - Cancel order

### User Endpoints
- `GET /api/user/profile` - Get user profile
- `PUT /api/user/profile` - Update user profile
- `POST /api/user/change-password` - Change password
- `GET /api/user/addresses` - Get user addresses
- `POST /api/user/addresses` - Add new address
- `PUT /api/user/addresses/{addressId}` - Update address
- `DELETE /api/user/addresses/{addressId}` - Delete address

## Security Configuration

### JWT Authentication
- Use JWT tokens for authentication
- Token expiration: 24 hours for access token, 7 days for refresh token
- Store refresh tokens in database for revocation

### CORS Configuration
- Allow requests from frontend origin (http://localhost:3000 in development)
- Configure allowed methods: GET, POST, PUT, DELETE
- Configure allowed headers

### Password Security
- Use BCrypt for password hashing
- Implement password strength requirements
- Secure password reset with time-limited tokens

## MyBatis Mapper XML Files

Location: `src/main/resources/mapper/`

### Example: ProductMapper.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" 
"http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.shoppingmall.mapper.ProductMapper">
    <select id="findAll" resultType="com.shoppingmall.model.Product">
        SELECT * FROM products 
        WHERE is_active = true
        <if test="category != null">
            AND category = #{category}
        </if>
        <if test="minPrice != null">
            AND price >= #{minPrice}
        </if>
        <if test="maxPrice != null">
            AND price <= #{maxPrice}
        </if>
        ORDER BY ${sortBy} ${sortOrder}
        LIMIT #{limit} OFFSET #{offset}
    </select>
</mapper>
```

## Integration Points

### Frontend Integration
- All API responses follow consistent JSON structure
- Include appropriate HTTP status codes
- Implement proper error handling and messages

### Future Admin/Seller Integration
- Design APIs to be reusable by admin and seller applications
- Implement role-based access control (RBAC)
- Separate endpoints for admin/seller operations

## Deployment Considerations

### Environment Variables
```yaml
SPRING_DATASOURCE_URL: jdbc:mysql://localhost:3306/shopping_mall
SPRING_DATASOURCE_USERNAME: root
SPRING_DATASOURCE_PASSWORD: password
JWT_SECRET: your-secret-key
JWT_EXPIRATION: 86400000
```

### Docker Configuration
- Create Dockerfile for Spring Boot application
- Use docker-compose for local development with MySQL
- Configure health checks and resource limits

### Kubernetes Deployment
- Create deployment manifests for backend service
- Configure service and ingress
- Set up ConfigMaps for environment configuration
- Implement horizontal pod autoscaling

This architecture provides a solid foundation for the customer-facing shopping mall application with room for future expansion to support admin and seller functionalities.