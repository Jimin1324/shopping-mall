import React from 'react';
import Header from '../../components/common/Header';
import MainBanner from '../../components/customer/MainBanner';
import TodayRecommendation from '../../components/customer/TodayRecommendation';
import EventSection from '../../components/customer/EventSection';

const LandingPage: React.FC = () => {
  return (
    <div className="min-h-screen bg-gray-50">
      <Header />
      <main>
        <MainBanner />
        <TodayRecommendation />
        <EventSection />
      </main>
      <footer className="bg-gray-800 text-white py-8">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="grid grid-cols-1 md:grid-cols-4 gap-8">
            <div>
              <h3 className="text-lg font-semibold mb-4">About ShopMall</h3>
              <ul className="space-y-2 text-gray-300">
                <li><a href="#about" className="hover:text-white">About Us</a></li>
                <li><a href="#careers" className="hover:text-white">Careers</a></li>
                <li><a href="#press" className="hover:text-white">Press</a></li>
              </ul>
            </div>
            <div>
              <h3 className="text-lg font-semibold mb-4">Customer Service</h3>
              <ul className="space-y-2 text-gray-300">
                <li><a href="#help" className="hover:text-white">Help Center</a></li>
                <li><a href="#returns" className="hover:text-white">Returns</a></li>
                <li><a href="#shipping" className="hover:text-white">Shipping Info</a></li>
              </ul>
            </div>
            <div>
              <h3 className="text-lg font-semibold mb-4">Partner</h3>
              <ul className="space-y-2 text-gray-300">
                <li><a href="#sell" className="hover:text-white">Sell on ShopMall</a></li>
                <li><a href="#affiliate" className="hover:text-white">Affiliates</a></li>
                <li><a href="#advertise" className="hover:text-white">Advertise</a></li>
              </ul>
            </div>
            <div>
              <h3 className="text-lg font-semibold mb-4">Connect</h3>
              <ul className="space-y-2 text-gray-300">
                <li><a href="#facebook" className="hover:text-white">Facebook</a></li>
                <li><a href="#twitter" className="hover:text-white">Twitter</a></li>
                <li><a href="#instagram" className="hover:text-white">Instagram</a></li>
              </ul>
            </div>
          </div>
          <div className="mt-8 pt-8 border-t border-gray-700 text-center text-gray-400">
            <p>&copy; 2024 ShopMall. All rights reserved.</p>
          </div>
        </div>
      </footer>
    </div>
  );
};

export default LandingPage;