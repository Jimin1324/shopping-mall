import React, { useState, useEffect } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { productsAPI } from '../../services/api';
import Header from '../../components/common/Header';

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

const ProductsPage: React.FC = () => {
  const location = useLocation();
  const searchParams = new URLSearchParams(location.search);
  const searchQuery = searchParams.get('search') || '';
  const categoryParam = searchParams.get('category') || 'all';
  
  const [selectedCategory, setSelectedCategory] = useState<string>(categoryParam);
  const [priceRange, setPriceRange] = useState<[number, number]>([0, 1000]);
  const [sortBy, setSortBy] = useState<string>('relevance');
  const [products, setProducts] = useState<Product[]>([]);
  const [categories, setCategories] = useState<string[]>(['all']);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Update selected category when URL changes
  useEffect(() => {
    setSelectedCategory(categoryParam);
  }, [categoryParam]);

  // Fetch categories on component mount
  useEffect(() => {
    const fetchCategories = async () => {
      try {
        const response = await productsAPI.getCategories();
        setCategories(['all', ...(response.categories || [])]);
      } catch (err) {
        console.error('Failed to fetch categories:', err);
      }
    };
    fetchCategories();
  }, []);

  // Fetch products when filters or search query change
  useEffect(() => {
    const fetchProducts = async () => {
      setLoading(true);
      setError(null);
      try {
        let response;
        
        if (searchQuery) {
          // Use backend search when search query is present
          response = await productsAPI.search(searchQuery);
        } else {
          // Use regular product listing when no search query
          const sortMapping: { [key: string]: string } = {
            'relevance': 'createdAt',
            'price-low': 'price',
            'price-high': 'price',
            'rating': 'rating'
          };

          response = await productsAPI.getAll({
            category: selectedCategory !== 'all' ? selectedCategory : undefined,
            minPrice: priceRange[0],
            maxPrice: priceRange[1],
            sort: sortMapping[sortBy] || 'createdAt',
            order: sortBy === 'price-high' ? 'desc' : sortBy === 'rating' ? 'desc' : 'asc'
          });
        }
        
        setProducts(response.products || response.content || response);
      } catch (err) {
        setError('Failed to load products. Please try again later.');
        console.error('Failed to fetch products:', err);
      } finally {
        setLoading(false);
      }
    };

    fetchProducts();
  }, [selectedCategory, priceRange, sortBy, searchQuery, categoryParam]);

  return (
    <div className="min-h-screen bg-gray-50">
      <Header />
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="flex flex-col lg:flex-row gap-8">
          {/* Sidebar Filters */}
          <aside className="w-full lg:w-64 flex-shrink-0">
            <div className="bg-white rounded-lg shadow p-6">
              <h3 className="text-lg font-semibold mb-4">Filters</h3>
              
              {/* Category Filter */}
              <div className="mb-6">
                <h4 className="font-medium mb-2">Category</h4>
                <div className="space-y-2">
                  {categories.map(category => (
                    <label key={category} className="flex items-center">
                      <input
                        type="radio"
                        name="category"
                        value={category}
                        checked={selectedCategory === category}
                        onChange={(e) => setSelectedCategory(e.target.value)}
                        className="mr-2"
                      />
                      <span className="capitalize">{category === 'all' ? 'All Categories' : category}</span>
                    </label>
                  ))}
                </div>
              </div>

              {/* Price Range Filter */}
              <div className="mb-6">
                <h4 className="font-medium mb-2">Price Range</h4>
                <div className="space-y-2">
                  <input
                    type="range"
                    min="0"
                    max="500"
                    value={priceRange[1]}
                    onChange={(e) => setPriceRange([0, parseInt(e.target.value)])}
                    className="w-full"
                  />
                  <div className="flex justify-between text-sm text-gray-600">
                    <span>${priceRange[0]}</span>
                    <span>${priceRange[1]}</span>
                  </div>
                </div>
              </div>
            </div>
          </aside>

          {/* Main Content */}
          <main className="flex-1">
            {/* Sort Bar */}
            <div className="bg-white rounded-lg shadow p-4 mb-6">
              {searchQuery && (
                <div className="mb-3">
                  <p className="text-lg font-medium text-gray-800">
                    Search results for: <span className="text-indigo-600">"{searchQuery}"</span>
                  </p>
                </div>
              )}
              <div className="flex justify-between items-center">
                <p className="text-gray-600">{products.length} products found</p>
              <select
                value={sortBy}
                onChange={(e) => setSortBy(e.target.value)}
                className="border rounded-md px-3 py-1"
              >
                <option value="relevance">Relevance</option>
                <option value="price-low">Price: Low to High</option>
                <option value="price-high">Price: High to Low</option>
                <option value="rating">Customer Rating</option>
              </select>
              </div>
            </div>

            {/* Products Grid */}
            {loading ? (
              <div className="flex justify-center items-center h-64">
                <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-indigo-600"></div>
              </div>
            ) : error ? (
              <div className="text-center text-red-600 py-8">{error}</div>
            ) : products.length === 0 ? (
              <div className="text-center text-gray-600 py-8">No products found matching your criteria.</div>
            ) : (
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                {products.map(product => (
                  <Link
                    key={product.id}
                    to={`/product/${product.id}`}
                    className="bg-white rounded-lg shadow hover:shadow-lg transition-shadow"
                  >
                    <img
                      src={product.imageUrl || '/images/product-placeholder.svg'}
                      alt={product.name}
                      className="w-full h-48 object-cover rounded-t-lg"
                      onError={(e) => {
                        const img = e.target as HTMLImageElement;
                        // Only set fallback if not already using it
                        if (!img.src.includes('/images/product-placeholder.svg')) {
                          img.src = '/images/product-placeholder.svg';
                        }
                      }}
                    />
                    <div className="p-4">
                      <h3 className="font-semibold text-lg mb-2">{product.name}</h3>
                      <div className="flex items-center mb-2">
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
                      <p className="text-xl font-bold text-indigo-600">${product.price}</p>
                    </div>
                  </Link>
                ))}
              </div>
            )}
          </main>
        </div>
      </div>
    </div>
  );
};

export default ProductsPage;