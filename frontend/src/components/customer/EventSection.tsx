import React from 'react';

interface Event {
  id: number;
  title: string;
  description: string;
  endDate: string;
  bgColor: string;
  icon: string;
}

const EventSection: React.FC = () => {
  const events: Event[] = [
    {
      id: 1,
      title: "Flash Sale",
      description: "Extra 20% off on electronics",
      endDate: "Ends in 2 days",
      bgColor: "bg-gradient-to-r from-orange-400 to-red-500",
      icon: "⚡"
    },
    {
      id: 2,
      title: "Free Shipping Weekend",
      description: "No minimum purchase required",
      endDate: "This weekend only",
      bgColor: "bg-gradient-to-r from-green-400 to-blue-500",
      icon: "🚚"
    },
    {
      id: 3,
      title: "New Customer Special",
      description: "Get $10 off your first order",
      endDate: "Limited time offer",
      bgColor: "bg-gradient-to-r from-purple-400 to-pink-500",
      icon: "🎁"
    }
  ];

  return (
    <section className="py-16 bg-white">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="text-center mb-12">
          <h2 className="text-3xl md:text-4xl font-bold text-gray-800 mb-4">
            Special Events
          </h2>
          <p className="text-lg text-gray-600">
            Don't miss out on these limited-time offers
          </p>
        </div>
        
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          {events.map((event) => (
            <div 
              key={event.id} 
              className={`${event.bgColor} rounded-lg p-6 text-white transform hover:scale-105 transition-transform cursor-pointer`}
            >
              <div className="text-4xl mb-4">{event.icon}</div>
              <h3 className="text-2xl font-bold mb-2">{event.title}</h3>
              <p className="text-lg mb-4 opacity-95">{event.description}</p>
              <p className="text-sm font-medium opacity-90">{event.endDate}</p>
              <button className="mt-4 bg-white/20 backdrop-blur-sm border border-white/30 px-6 py-2 rounded-md hover:bg-white/30 transition-colors">
                Learn More
              </button>
            </div>
          ))}
        </div>
        
        <div className="mt-12 bg-blue-50 rounded-lg p-8 text-center">
          <h3 className="text-2xl font-semibold text-gray-800 mb-4">
            Subscribe to Our Newsletter
          </h3>
          <p className="text-gray-600 mb-6">
            Get exclusive deals and be the first to know about new events
          </p>
          <form className="flex flex-col sm:flex-row gap-4 max-w-md mx-auto">
            <input
              type="email"
              placeholder="Enter your email"
              className="flex-1 px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:border-blue-500"
            />
            <button 
              type="submit"
              className="px-6 py-2 bg-blue-600 text-white font-medium rounded-md hover:bg-blue-700 transition-colors"
            >
              Subscribe
            </button>
          </form>
        </div>
      </div>
    </section>
  );
};

export default EventSection;