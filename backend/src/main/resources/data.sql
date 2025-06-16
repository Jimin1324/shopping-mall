-- Sample data for shopping mall application

-- Insert sample products
INSERT IGNORE INTO products (id, name, description, price, category, image_url, stock_quantity, rating, review_count) VALUES
(1, 'Wireless Headphones', 'Premium wireless headphones with noise cancellation and 30-hour battery life', 99.99, 'electronics', 'https://via.placeholder.com/300', 50, 4.5, 234),
(2, 'Running Shoes', 'Comfortable running shoes with advanced cushioning technology', 79.99, 'sports', 'https://via.placeholder.com/300', 30, 4.2, 156),
(3, 'Coffee Maker', 'Programmable coffee maker with thermal carafe and auto-brew feature', 149.99, 'home', 'https://via.placeholder.com/300', 25, 4.8, 89),
(4, 'Backpack', 'Durable backpack with multiple compartments and laptop sleeve', 49.99, 'accessories', 'https://via.placeholder.com/300', 75, 4.0, 203),
(5, 'Smartwatch', 'Advanced smartwatch with fitness tracking and heart rate monitor', 299.99, 'electronics', 'https://via.placeholder.com/300', 40, 4.6, 412),
(6, 'Yoga Mat', 'Non-slip yoga mat with carrying strap and extra thickness', 29.99, 'sports', 'https://via.placeholder.com/300', 100, 4.3, 178),
(7, 'Desk Lamp', 'LED desk lamp with adjustable brightness and USB charging port', 39.99, 'home', 'https://via.placeholder.com/300', 60, 4.4, 92),
(8, 'Wireless Mouse', 'Ergonomic wireless mouse with precision tracking', 24.99, 'electronics', 'https://via.placeholder.com/300', 80, 4.1, 145),
(9, 'Water Bottle', 'Insulated stainless steel water bottle that keeps drinks cold for 24 hours', 19.99, 'accessories', 'https://via.placeholder.com/300', 120, 4.7, 267),
(10, 'Bluetooth Speaker', 'Portable Bluetooth speaker with 360-degree sound and waterproof design', 59.99, 'electronics', 'https://via.placeholder.com/300', 45, 4.3, 189);

-- Note: User data will be created through registration process
-- Cart and order data will be created through user interactions