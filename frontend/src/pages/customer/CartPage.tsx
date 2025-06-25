import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import Header from '../../components/common/Header';
import { cartAPI } from '../../services/api';
import { useAuth } from '../../contexts/AuthContext';

interface CartItem {
  id: number;
  productId: number;
  quantity: number;
  size: string;
  addedAt: string;
  product: {
    id: number;
    name: string;
    price: number;
    imageUrl: string;
    stockQuantity: number;
    isActive: boolean;
  };
}

interface CartTotals {
  subtotal: number;
  tax: number;
  shippingFee: number;
  total: number;
}

const CartPage: React.FC = () => {
  const navigate = useNavigate();
  const { isAuthenticated } = useAuth();
  const [cartItems, setCartItems] = useState<CartItem[]>([]);
  const [totals, setTotals] = useState<CartTotals>({
    subtotal: 0,
    tax: 0,
    shippingFee: 0,
    total: 0
  });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Fetch cart data on component mount
  useEffect(() => {
    if (isAuthenticated) {
      fetchCart();
    } else {
      setLoading(false);
    }
  }, [isAuthenticated]);

  const fetchCart = async () => {
    try {
      setLoading(true);
      const response = await cartAPI.get();
      setCartItems(response.cart.items || []);
      setTotals(response.totals);
      setError(null);
    } catch (err) {
      console.error('Failed to fetch cart:', err);
      setError('Failed to load cart. Please try again.');
      setCartItems([]);
    } finally {
      setLoading(false);
    }
  };

  const updateQuantity = async (itemId: number, newQuantity: number) => {
    if (newQuantity <= 0) {
      await removeItem(itemId);
      return;
    }

    try {
      await cartAPI.updateItem(itemId, newQuantity);
      // Refresh cart after update
      await fetchCart();
    } catch (err) {
      console.error('Failed to update item quantity:', err);
      setError('Failed to update item. Please try again.');
    }
  };

  const removeItem = async (itemId: number) => {
    try {
      await cartAPI.removeItem(itemId);
      // Refresh cart after removal
      await fetchCart();
    } catch (err) {
      console.error('Failed to remove item:', err);
      setError('Failed to remove item. Please try again.');
    }
  };

  const clearCart = async () => {
    try {
      await cartAPI.clear();
      setCartItems([]);
      setTotals({ subtotal: 0, tax: 0, shippingFee: 0, total: 0 });
    } catch (err) {
      console.error('Failed to clear cart:', err);
      setError('Failed to clear cart. Please try again.');
    }
  };

  const handleCheckout = () => {
    navigate('/checkout');
  };

  // Show login prompt if not authenticated
  if (!isAuthenticated) {
    return (
      <div className="min-h-screen bg-gray-50">
        <Header />
        <div className="py-12">
          <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
            <div className="bg-white rounded-lg shadow p-8 text-center">
              <h2 className="text-2xl font-semibold text-gray-900 mb-2">Please log in to view your cart</h2>
              <p className="text-gray-600 mb-8">You need to be logged in to manage your shopping cart.</p>
              <div className="space-x-4">
                <Link
                  to="/login"
                  className="bg-indigo-600 text-white px-6 py-2 rounded-md hover:bg-indigo-700 transition-colors"
                >
                  Login
                </Link>
                <Link
                  to="/register"
                  className="bg-gray-200 text-gray-800 px-6 py-2 rounded-md hover:bg-gray-300 transition-colors"
                >
                  Register
                </Link>
              </div>
            </div>
          </div>
        </div>
      </div>
    );
  }

  // Show loading state
  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50">
        <Header />
        <div className="py-12">
          <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
            <div className="bg-white rounded-lg shadow p-8 text-center">
              <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-indigo-600 mx-auto"></div>
              <p className="mt-4 text-gray-600">Loading your cart...</p>
            </div>
          </div>
        </div>
      </div>
    );
  }

  // Show empty cart
  if (cartItems.length === 0) {
    return (
      <div className="min-h-screen bg-gray-50">
        <Header />
        <div className="py-12">
          <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
            <div className="bg-white rounded-lg shadow p-8 text-center">
              <svg className="w-24 h-24 text-gray-400 mx-auto mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z" />
              </svg>
              <h2 className="text-2xl font-semibold text-gray-900 mb-2">Your cart is empty</h2>
              <p className="text-gray-600 mb-8">Add some items to your cart to get started!</p>
              <Link
                to="/products"
                className="bg-indigo-600 text-white px-6 py-3 rounded-md hover:bg-indigo-700 transition-colors"
              >
                Continue Shopping
              </Link>
            </div>
          </div>
        </div>
      </div>
    );
  }

  // Show cart with items
  return (
    <div className="min-h-screen bg-gray-50">
      <Header />
      <div className="py-12">
        <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
          <h1 className="text-3xl font-bold text-gray-900 mb-8">Shopping Cart</h1>

          {error && (
            <div className="bg-red-50 border border-red-200 rounded-md p-4 mb-6">
              <p className="text-red-800">{error}</p>
            </div>
          )}

          <div className="bg-white rounded-lg shadow">
            {/* Cart Items */}
            <div className="p-6 space-y-4">
              {cartItems.map(item => (
                <div key={item.id} className="flex items-center space-x-4 border-b pb-4">
                  <img
                    src={item.product.imageUrl || '/images/product-placeholder.svg'}
                    alt={item.product.name}
                    className="w-20 h-20 object-cover rounded-md"
                    onError={(e) => {
                      const img = e.target as HTMLImageElement;
                      if (!img.src.includes('/images/product-placeholder.svg')) {
                        img.src = '/images/product-placeholder.svg';
                      }
                    }}
                  />
                  <div className="flex-1">
                    <h3 className="font-semibold text-lg">{item.product.name}</h3>
                    <p className="text-gray-600">${item.product.price}</p>
                    {item.size && <p className="text-sm text-gray-500">Size: {item.size}</p>}
                  </div>
                  <div className="flex items-center space-x-2">
                    <button
                      onClick={() => updateQuantity(item.id, item.quantity - 1)}
                      className="w-8 h-8 rounded-full border border-gray-300 flex items-center justify-center hover:bg-gray-50"
                      disabled={loading}
                    >
                      -
                    </button>
                    <span className="w-12 text-center">{item.quantity}</span>
                    <button
                      onClick={() => updateQuantity(item.id, item.quantity + 1)}
                      className="w-8 h-8 rounded-full border border-gray-300 flex items-center justify-center hover:bg-gray-50"
                      disabled={loading}
                    >
                      +
                    </button>
                  </div>
                  <div className="text-right">
                    <p className="font-semibold">${(item.product.price * item.quantity).toFixed(2)}</p>
                    <button
                      onClick={() => removeItem(item.id)}
                      className="text-red-600 hover:text-red-800 text-sm"
                      disabled={loading}
                    >
                      Remove
                    </button>
                  </div>
                </div>
              ))}
            </div>

            {/* Cart Summary */}
            <div className="border-t bg-gray-50 p-6">
              <div className="space-y-2">
                <div className="flex justify-between">
                  <span>Subtotal:</span>
                  <span>${totals.subtotal.toFixed(2)}</span>
                </div>
                <div className="flex justify-between">
                  <span>Tax:</span>
                  <span>${totals.tax.toFixed(2)}</span>
                </div>
                <div className="flex justify-between">
                  <span>Shipping:</span>
                  <span>${totals.shippingFee.toFixed(2)}</span>
                </div>
                <div className="border-t pt-2 flex justify-between font-bold text-lg">
                  <span>Total:</span>
                  <span>${totals.total.toFixed(2)}</span>
                </div>
              </div>

              <div className="mt-6 space-y-4">
                <button
                  onClick={handleCheckout}
                  className="w-full bg-indigo-600 text-white py-3 px-4 rounded-md hover:bg-indigo-700 transition-colors"
                  disabled={loading}
                >
                  Proceed to Checkout
                </button>
                <div className="flex space-x-4">
                  <Link
                    to="/products"
                    className="flex-1 bg-gray-200 text-gray-800 py-2 px-4 rounded-md text-center hover:bg-gray-300 transition-colors"
                  >
                    Continue Shopping
                  </Link>
                  <button
                    onClick={clearCart}
                    className="flex-1 bg-red-200 text-red-800 py-2 px-4 rounded-md hover:bg-red-300 transition-colors"
                    disabled={loading}
                  >
                    Clear Cart
                  </button>
                </div>
              </div>
            </div>

            {/* Free Shipping Notice */}
            {totals.subtotal > 0 && totals.subtotal < 100 && (
              <div className="bg-blue-50 border border-blue-200 rounded-b-lg p-4">
                <p className="text-sm text-blue-800">
                  Add ${(100 - totals.subtotal).toFixed(2)} more to your cart for free shipping!
                </p>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default CartPage;