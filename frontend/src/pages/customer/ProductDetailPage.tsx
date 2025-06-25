import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import Header from '../../components/common/Header';
import { cartAPI, productsAPI } from '../../services/api';
import { useAuth } from '../../contexts/AuthContext';

const ProductDetailPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { isAuthenticated } = useAuth();
  const [quantity, setQuantity] = useState(1);
  const [selectedSize, setSelectedSize] = useState('M');
  const [selectedImage, setSelectedImage] = useState(0);
  const [product, setProduct] = useState<any>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [addingToCart, setAddingToCart] = useState(false);

  useEffect(() => {
    if (id) {
      fetchProduct();
    }
  }, [id]);

  const fetchProduct = async () => {
    try {
      setLoading(true);
      const response = await productsAPI.getById(Number(id));
      setProduct(response);
      setError(null);
    } catch (err) {
      console.error('Failed to fetch product:', err);
      setError('Product not found');
    } finally {
      setLoading(false);
    }
  };

  const handleAddToCart = async () => {
    if (!isAuthenticated) {
      navigate('/login', { state: { returnTo: `/product/${id}` } });
      return;
    }

    if (!product) return;

    try {
      setAddingToCart(true);
      await cartAPI.addItem(product.id, quantity, selectedSize);
      
      // Show success message (you can add a toast notification here)
      alert('Item added to cart successfully!');
    } catch (err) {
      console.error('Failed to add to cart:', err);
      alert('Failed to add item to cart. Please try again.');
    } finally {
      setAddingToCart(false);
    }
  };

  const handleBuyNow = async () => {
    await handleAddToCart();
    navigate('/cart');
  };

  // Loading state
  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50">
        <Header />
        <div className="py-8">
          <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
            <div className="bg-white rounded-lg shadow-lg p-8 text-center">
              <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-indigo-600 mx-auto"></div>
              <p className="mt-4 text-gray-600">Loading product...</p>
            </div>
          </div>
        </div>
      </div>
    );
  }

  // Error state
  if (error || !product) {
    return (
      <div className="min-h-screen bg-gray-50">
        <Header />
        <div className="py-8">
          <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
            <div className="bg-white rounded-lg shadow-lg p-8 text-center">
              <h2 className="text-2xl font-semibold text-gray-900 mb-2">Product Not Found</h2>
              <p className="text-gray-600 mb-8">The product you're looking for doesn't exist.</p>
              <button
                onClick={() => navigate('/products')}
                className="bg-indigo-600 text-white px-6 py-2 rounded-md hover:bg-indigo-700 transition-colors"
              >
                Browse Products
              </button>
            </div>
          </div>
        </div>
      </div>
    );
  }

  // Default data for demo (in case some fields are missing)
  const productData = {
    ...product,
    images: product.imageUrl ? [product.imageUrl] : ["/images/product-placeholder.svg"],
    rating: product.rating || 4.5,
    reviews: product.reviewCount || 234,
    sizes: ['S', 'M', 'L', 'XL'], // You may want to add this to your product model
    inStock: product.stockQuantity > 0,
    features: [
      "High quality construction",
      "Premium materials",
      "Excellent performance",
      "Great value for money"
    ] // You may want to add this to your product model
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <Header />
      <div className="py-8">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="bg-white rounded-lg shadow-lg overflow-hidden">
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-8 p-8">
            {/* Product Images */}
            <div className="space-y-4">
              <div className="aspect-square rounded-lg overflow-hidden">
                <img
                  src={productData.images[selectedImage]}
                  alt={productData.name}
                  className="w-full h-full object-cover"
                  onError={(e) => {
                    const img = e.target as HTMLImageElement;
                    if (!img.src.includes('/images/product-placeholder.svg')) {
                      img.src = '/images/product-placeholder.svg';
                    }
                  }}
                />
              </div>
              <div className="grid grid-cols-4 gap-2">
                {productData.images.map((image: string, index: number) => (
                  <button
                    key={index}
                    onClick={() => setSelectedImage(index)}
                    className={`aspect-square rounded-md overflow-hidden border-2 ${
                      selectedImage === index ? 'border-indigo-600' : 'border-gray-200'
                    }`}
                  >
                    <img src={image} alt={`${productData.name} ${index + 1}`} className="w-full h-full object-cover" 
                      onError={(e) => {
                        const img = e.target as HTMLImageElement;
                        if (!img.src.includes('/images/product-placeholder.svg')) {
                          img.src = '/images/product-placeholder.svg';
                        }
                      }}
                    />
                  </button>
                ))}
              </div>
            </div>

            {/* Product Info */}
            <div className="space-y-6">
              <div>
                <h1 className="text-3xl font-bold text-gray-900">{productData.name}</h1>
                <div className="flex items-center mt-2">
                  <div className="flex text-yellow-400">
                    {[...Array(5)].map((_, i) => (
                      <svg
                        key={i}
                        className={`w-5 h-5 ${i < Math.floor(productData.rating) ? 'fill-current' : 'stroke-current'}`}
                        viewBox="0 0 20 20"
                      >
                        <path d="M10 15l-5.878 3.09 1.123-6.545L.489 6.91l6.572-.955L10 0l2.939 5.955 6.572.955-4.756 4.635 1.123 6.545z" />
                      </svg>
                    ))}
                  </div>
                  <span className="ml-2 text-gray-600">({productData.reviews} reviews)</span>
                </div>
              </div>

              <div className="text-3xl font-bold text-indigo-600">${productData.price}</div>

              <p className="text-gray-600">{productData.description}</p>

              {/* Size Selection */}
              <div>
                <h3 className="text-sm font-medium text-gray-900 mb-2">Size</h3>
                <div className="flex gap-2">
                  {productData.sizes.map((size: string) => (
                    <button
                      key={size}
                      onClick={() => setSelectedSize(size)}
                      className={`px-4 py-2 border rounded-md ${
                        selectedSize === size
                          ? 'border-indigo-600 bg-indigo-50 text-indigo-600'
                          : 'border-gray-300 hover:border-gray-400'
                      }`}
                    >
                      {size}
                    </button>
                  ))}
                </div>
              </div>

              {/* Quantity Selection */}
              <div>
                <h3 className="text-sm font-medium text-gray-900 mb-2">Quantity</h3>
                <div className="flex items-center gap-4">
                  <button
                    onClick={() => setQuantity(Math.max(1, quantity - 1))}
                    className="w-10 h-10 rounded-md border border-gray-300 flex items-center justify-center hover:bg-gray-50"
                  >
                    -
                  </button>
                  <span className="w-12 text-center">{quantity}</span>
                  <button
                    onClick={() => setQuantity(quantity + 1)}
                    className="w-10 h-10 rounded-md border border-gray-300 flex items-center justify-center hover:bg-gray-50"
                  >
                    +
                  </button>
                </div>
              </div>

              {/* Action Buttons */}
              <div className="flex gap-4">
                <button
                  onClick={handleAddToCart}
                  disabled={addingToCart || !productData.inStock}
                  className="flex-1 bg-white border-2 border-indigo-600 text-indigo-600 py-3 px-6 rounded-md font-medium hover:bg-indigo-50 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  {addingToCart ? 'Adding...' : 'Add to Cart'}
                </button>
                <button
                  onClick={handleBuyNow}
                  disabled={addingToCart || !productData.inStock}
                  className="flex-1 bg-indigo-600 text-white py-3 px-6 rounded-md font-medium hover:bg-indigo-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  {addingToCart ? 'Processing...' : 'Buy Now'}
                </button>
              </div>

              {/* Stock Status */}
              <div className="flex items-center gap-2">
                {productData.inStock ? (
                  <>
                    <div className="w-3 h-3 bg-green-500 rounded-full"></div>
                    <span className="text-green-600">In Stock ({productData.stockQuantity} available)</span>
                  </>
                ) : (
                  <>
                    <div className="w-3 h-3 bg-red-500 rounded-full"></div>
                    <span className="text-red-600">Out of Stock</span>
                  </>
                )}
              </div>

              {/* Features */}
              <div className="border-t pt-6">
                <h3 className="font-medium text-gray-900 mb-3">Key Features</h3>
                <ul className="space-y-2">
                  {productData.features.map((feature: string, index: number) => (
                    <li key={index} className="flex items-center gap-2">
                      <svg className="w-5 h-5 text-green-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
                      </svg>
                      <span className="text-gray-600">{feature}</span>
                    </li>
                  ))}
                </ul>
              </div>
            </div>
          </div>
        </div>
        </div>
      </div>
    </div>
  );
};

export default ProductDetailPage;