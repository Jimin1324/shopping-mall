import React, { useState, useEffect } from 'react';
import { productsAPI } from '../../services/api';

interface Product {
  id: number;
  name: string;
  price: number;
  imageUrl: string;
  category: string;
  rating: number;
  reviewCount: number;
  description?: string;
  stockQuantity?: number;
}

const TodayRecommendation: React.FC = () => {
  const [recommendedProducts, setRecommendedProducts] = useState<Product[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchRecommendedProducts = async () => {
      setLoading(true);
      setError(null);
      try {
        // Fetch products sorted by rating (highest rated as recommendations)
        const response = await productsAPI.getAll({
          sort: 'rating',
          order: 'desc',
          limit: 4
        });
        
        // API returns {products: [...], total, ...}
        const products = response.products || [];
        // Take the first 4 products as recommendations
        setRecommendedProducts(products.slice(0, 4));
      } catch (err) {
        setError('Failed to load recommendations');
        console.error('Failed to fetch recommended products:', err);
      } finally {
        setLoading(false);
      }
    };

    fetchRecommendedProducts();
  }, []);

  // Calculate discount percentage (random for demo purposes)
  const getDiscountInfo = (price: number) => {
    const discountPercent = Math.floor(Math.random() * 30) + 10; // 10-40% discount
    const originalPrice = Math.round(price / (1 - discountPercent / 100));
    return { originalPrice, discountPercent };
  };

  if (loading) {
    return (
      <section className="py-16 bg-gray-50">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-center items-center h-64">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-indigo-600"></div>
          </div>
        </div>
      </section>
    );
  }

  if (error || recommendedProducts.length === 0) {
    return null; // Don't show the section if there's an error or no products
  }

  return (
    <section className="py-16 bg-gray-50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="text-center mb-12">
          <h2 className="text-3xl md:text-4xl font-bold text-gray-800 mb-4">
            Today's Recommendations
          </h2>
          <p className="text-lg text-gray-600">
            Handpicked deals just for you
          </p>
        </div>
        
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
          {recommendedProducts.map((product) => {
            const { originalPrice, discountPercent } = getDiscountInfo(product.price);
            return (
              <div key={product.id} className="bg-white rounded-lg shadow-md hover:shadow-lg transition-shadow cursor-pointer">
                <div className="p-6">
                  <div className="h-48 mb-4 overflow-hidden rounded-lg">
                    <img 
                      src={product.imageUrl || '/images/product-placeholder.svg'} 
                      alt={product.name}
                      className="w-full h-full object-cover"
                      onError={(e) => {
                        const img = e.target as HTMLImageElement;
                        if (!img.src.includes('/images/product-placeholder.svg')) {
                          img.src = '/images/product-placeholder.svg';
                        }
                      }}
                    />
                  </div>
                  <span className="inline-block bg-red-500 text-white text-sm px-2 py-1 rounded mb-2">
                    -{discountPercent}%
                  </span>
                  <h3 className="font-semibold text-lg mb-2">{product.name}</h3>
                  <div className="flex items-center gap-2">
                    <span className="text-2xl font-bold text-gray-800">${product.price.toFixed(2)}</span>
                    <span className="text-gray-500 line-through">${originalPrice.toFixed(2)}</span>
                  </div>
                  <div className="flex items-center mt-2 mb-4">
                    <div className="flex text-yellow-400">
                      {[...Array(5)].map((_, i) => (
                        <svg
                          key={i}
                          className={`w-4 h-4 ${i < Math.floor(product.rating) ? 'fill-current' : 'stroke-current'}`}
                          viewBox="0 0 20 20"
                        >
                          <path d="M10 15l-5.878 3.09 1.123-6.545L.489 6.91l6.572-.955L10 0l2.939 5.955 6.572.955-4.756 4.635 1.123 6.545z" />
                        </svg>
                      ))}
                    </div>
                    <span className="text-sm text-gray-600 ml-2">({product.reviewCount})</span>
                  </div>
                  <button className="w-full bg-blue-600 text-white py-2 rounded-md hover:bg-blue-700 transition-colors">
                    Add to Cart
                  </button>
                </div>
              </div>
            );
          })}
        </div>
      </div>
    </section>
  );
};

export default TodayRecommendation;