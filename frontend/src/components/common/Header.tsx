import React, { useState } from 'react';

const Header: React.FC = () => {
  const [searchQuery, setSearchQuery] = useState('');

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    console.log('Searching for:', searchQuery);
  };

  return (
    <header className="bg-white shadow-md sticky top-0 z-50">
      <div className="border-b border-gray-200">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex items-center justify-between py-4 gap-4">
            <div className="flex-shrink-0">
              <h1 className="text-2xl md:text-3xl font-bold text-gray-800">ShopMall</h1>
            </div>
            
            <form className="flex-1 max-w-2xl" onSubmit={handleSearch}>
              <div className="relative flex">
                <input
                  type="text"
                  placeholder="Search products, brands, and more..."
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
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
              <button className="px-4 py-2 text-sm font-medium text-gray-700 bg-gray-100 rounded-md hover:bg-gray-200 transition-colors">
                Sign In
              </button>
              <button className="px-4 py-2 text-sm font-medium text-gray-700 bg-gray-100 rounded-md hover:bg-gray-200 transition-colors">
                Cart (0)
              </button>
            </div>
          </div>
        </div>
      </div>
      
      <nav className="bg-gray-50">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <ul className="flex flex-wrap items-center gap-6 py-3">
            <li><a href="#categories" className="text-gray-700 font-medium hover:text-blue-600 transition-colors">Categories</a></li>
            <li><a href="#new" className="text-gray-700 font-medium hover:text-blue-600 transition-colors">New Arrivals</a></li>
            <li><a href="#deals" className="text-gray-700 font-medium hover:text-blue-600 transition-colors">Today's Deals</a></li>
            <li><a href="#brands" className="text-gray-700 font-medium hover:text-blue-600 transition-colors">Brands</a></li>
            <li><a href="#customer-service" className="text-gray-700 font-medium hover:text-blue-600 transition-colors">Customer Service</a></li>
            <li><a href="#seller" className="text-gray-700 font-medium hover:text-blue-600 transition-colors">Become a Seller</a></li>
          </ul>
        </div>
      </nav>
    </header>
  );
};

export default Header;