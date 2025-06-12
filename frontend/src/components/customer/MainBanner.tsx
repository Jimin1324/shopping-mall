import React from 'react';

const MainBanner: React.FC = () => {
  return (
    <section className="relative bg-gradient-to-r from-blue-600 to-purple-600 text-white">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-20 md:py-32">
        <div className="flex flex-col md:flex-row items-center justify-between">
          <div className="md:w-1/2 mb-8 md:mb-0">
            <h2 className="text-4xl md:text-5xl lg:text-6xl font-bold mb-4">
              Summer Sale
            </h2>
            <p className="text-xl md:text-2xl mb-6">
              Up to 50% off on selected items
            </p>
            <p className="text-lg mb-8 opacity-90">
              Discover amazing deals on fashion, electronics, home & more
            </p>
            <button className="bg-white text-blue-600 px-8 py-3 rounded-md font-semibold text-lg hover:bg-gray-100 transition-colors">
              Shop Now
            </button>
          </div>
          <div className="md:w-1/2 flex justify-center">
            <div className="w-80 h-80 bg-white/20 rounded-full flex items-center justify-center">
              <span className="text-4xl">🛍️</span>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
};

export default MainBanner;