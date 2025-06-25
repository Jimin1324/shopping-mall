import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext';
import { cartAPI } from '../../services/api';

const Header: React.FC = () => {
  const [searchQuery, setSearchQuery] = useState('');
  const [cartCount, setCartCount] = useState(0);
  const { user, isAuthenticated, logout } = useAuth();
  const navigate = useNavigate();

  // Fetch cart count when user is authenticated
  useEffect(() => {
    if (isAuthenticated) {
      fetchCartCount();
    } else {
      setCartCount(0);
    }
  }, [isAuthenticated]);

  const fetchCartCount = async () => {
    try {
      const response = await cartAPI.getCount();
      setCartCount(response.count || 0);
    } catch (err) {
      console.error('Failed to fetch cart count:', err);
      setCartCount(0);
    }
  };

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    console.log('Search submitted:', searchQuery);
    if (searchQuery.trim()) {
      const searchUrl = `/products?search=${encodeURIComponent(searchQuery.trim())}`;
      console.log('Navigating to:', searchUrl);
      navigate(searchUrl);
    }
  };

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  return (
    <header className="bg-white shadow-md sticky top-0 z-50">
      <div className="border-b border-gray-200">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex items-center justify-between py-4 gap-4">
            <div className="flex-shrink-0">
              <Link to="/">
                <h1 className="text-2xl md:text-3xl font-bold text-gray-800">ShopMall</h1>
              </Link>
            </div>
            
            <form className="flex-1 max-w-2xl" onSubmit={handleSearch}>
              <div className="relative flex">
                <input
                  type="text"
                  placeholder="Search products, brands, and more..."
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  onKeyPress={(e) => {
                    if (e.key === 'Enter') {
                      e.preventDefault();
                      handleSearch(e as any);
                    }
                  }}
                  className="w-full px-4 py-2 border-2 border-gray-300 rounded-l-md focus:outline-none focus:border-blue-500 text-sm"
                />
                <button 
                  type="submit"
                  className="px-4 py-2 bg-blue-600 text-white font-medium rounded-r-md hover:bg-blue-700 transition-colors"
                >
                  Search
                </button>
              </div>
            </form>
            
            <div className="flex items-center gap-2">
              {isAuthenticated && user ? (
                <>
                  <div className="text-sm text-gray-700 px-3">
                    Hi, {user.firstName}!
                  </div>
                  <Link
                    to="/profile"
                    className="px-4 py-2 text-sm font-medium text-gray-700 bg-gray-100 rounded-md hover:bg-gray-200 transition-colors"
                  >
                    My Account
                  </Link>
                  <button
                    onClick={handleLogout}
                    className="px-4 py-2 text-sm font-medium text-gray-700 bg-gray-100 rounded-md hover:bg-gray-200 transition-colors"
                  >
                    Sign Out
                  </button>
                </>
              ) : (
                <Link 
                  to="/login"
                  className="px-4 py-2 text-sm font-medium text-gray-700 bg-gray-100 rounded-md hover:bg-gray-200 transition-colors"
                >
                  Sign In
                </Link>
              )}
              <Link 
                to="/cart"
                className="px-4 py-2 text-sm font-medium text-gray-700 bg-gray-100 rounded-md hover:bg-gray-200 transition-colors"
              >
                Cart ({cartCount})
              </Link>
            </div>
          </div>
        </div>
      </div>
      
      <nav className="bg-gray-50">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <ul className="flex flex-wrap items-center gap-6 py-3">
            <li><Link to="/products" className="text-gray-700 font-medium hover:text-blue-600 transition-colors">All Products</Link></li>
            <li><Link to="/products?category=electronics" className="text-gray-700 font-medium hover:text-blue-600 transition-colors">Electronics</Link></li>
            <li><Link to="/products?category=sports" className="text-gray-700 font-medium hover:text-blue-600 transition-colors">Sports</Link></li>
            <li><Link to="/products?category=home" className="text-gray-700 font-medium hover:text-blue-600 transition-colors">Home</Link></li>
            <li><Link to="/products?category=accessories" className="text-gray-700 font-medium hover:text-blue-600 transition-colors">Accessories</Link></li>
            <li><a href="#customer-service" className="text-gray-700 font-medium hover:text-blue-600 transition-colors">Customer Service</a></li>
          </ul>
        </div>
      </nav>
    </header>
  );
};

export default Header;