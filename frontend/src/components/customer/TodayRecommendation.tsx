import React from 'react';

interface Product {
  id: number;
  name: string;
  price: number;
  originalPrice?: number;
  image: string;
  discount?: number;
}

const TodayRecommendation: React.FC = () => {
  const recommendedProducts: Product[] = [
    {
      id: 1,
      name: "Wireless Headphones",
      price: 79.99,
      originalPrice: 129.99,
      image: "🎧",
      discount: 38
    },
    {
      id: 2,
      name: "Smart Watch",
      price: 199.99,
      originalPrice: 299.99,
      image: "⌚",
      discount: 33
    },
    {
      id: 3,
      name: "Running Shoes",
      price: 89.99,
      originalPrice: 119.99,
      image: "👟",
      discount: 25
    },
    {
      id: 4,
      name: "Laptop Backpack",
      price: 49.99,
      originalPrice: 69.99,
      image: "🎒",
      discount: 29
    }
  ];

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
          {recommendedProducts.map((product) => (
            <div key={product.id} className="bg-white rounded-lg shadow-md hover:shadow-lg transition-shadow cursor-pointer">
              <div className="p-6">
                <div className="text-6xl text-center mb-4">{product.image}</div>
                {product.discount && (
                  <span className="inline-block bg-red-500 text-white text-sm px-2 py-1 rounded mb-2">
                    -{product.discount}%
                  </span>
                )}
                <h3 className="font-semibold text-lg mb-2">{product.name}</h3>
                <div className="flex items-center gap-2">
                  <span className="text-2xl font-bold text-gray-800">${product.price}</span>
                  {product.originalPrice && (
                    <span className="text-gray-500 line-through">${product.originalPrice}</span>
                  )}
                </div>
                <button className="w-full mt-4 bg-blue-600 text-white py-2 rounded-md hover:bg-blue-700 transition-colors">
                  Add to Cart
                </button>
              </div>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
};

export default TodayRecommendation;