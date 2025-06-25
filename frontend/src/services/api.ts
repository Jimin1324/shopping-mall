const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

// Generic API request function
async function apiRequest<T>(
  endpoint: string,
  options: RequestInit = {}
): Promise<T> {
  const token = localStorage.getItem('authToken');
  
  const config: RequestInit = {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...(token && { Authorization: `Bearer ${token}` }),
      ...options.headers,
    },
  };

  const response = await fetch(`${API_BASE_URL}${endpoint}`, config);

  if (!response.ok) {
    const error = await response.json().catch(() => ({ message: 'An error occurred' }));
    throw new Error(error.message || `HTTP error! status: ${response.status}`);
  }

  return response.json();
}

// Auth API
export const authAPI = {
  login: (email: string, password: string) =>
    apiRequest<{ token: string; user: any }>('/auth/login', {
      method: 'POST',
      body: JSON.stringify({ email, password }),
    }),

  register: (userData: {
    firstName: string;
    lastName: string;
    email: string;
    password: string;
  }) =>
    apiRequest<{ token: string; user: any }>('/auth/register', {
      method: 'POST',
      body: JSON.stringify(userData),
    }),

  forgotPassword: (email: string) =>
    apiRequest<{ message: string }>('/auth/forgot-password', {
      method: 'POST',
      body: JSON.stringify({ email }),
    }),

  resetPassword: (token: string, newPassword: string) =>
    apiRequest<{ message: string }>('/auth/reset-password', {
      method: 'POST',
      body: JSON.stringify({ token, newPassword }),
    }),

  logout: () => {
    localStorage.removeItem('authToken');
    // Additional logout logic if needed
  },
};

// Products API
export const productsAPI = {
  getAll: (params?: {
    category?: string;
    minPrice?: number;
    maxPrice?: number;
    sort?: string;
    order?: string;
    page?: number;
    limit?: number;
  }) => {
    const queryParams = new URLSearchParams();
    if (params) {
      Object.entries(params).forEach(([key, value]) => {
        if (value !== undefined) {
          queryParams.append(key, value.toString());
        }
      });
    }
    return apiRequest<any>(
      `/products${queryParams.toString() ? `?${queryParams.toString()}` : ''}`
    );
  },

  getById: (id: number) =>
    apiRequest<any>(`/products/${id}`),

  search: (query: string) =>
    apiRequest<{ products: any[] }>(`/products/search?q=${encodeURIComponent(query)}`),

  searchElasticsearch: async (query: string) => {
    try {
      // Direct call to Elasticsearch with proper query structure
      const elasticsearchQuery = {
        query: {
          multi_match: {
            query: query,
            fields: ["name^2", "description"],
            type: "best_fields",
            fuzziness: "AUTO"
          }
        },
        sort: [
          "_score"
        ],
        size: 50
      };

      const response = await fetch('http://localhost:9200/products/_search', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(elasticsearchQuery),
      });

      if (!response.ok) {
        throw new Error(`Elasticsearch search failed: ${response.status}`);
      }

      const elasticResult = await response.json();
      
      // Convert Elasticsearch response to our expected format
      const products = elasticResult.hits.hits.map((hit: any) => ({
        ...hit._source,
        _score: hit._score
      }));

      return {
        products,
        total: elasticResult.hits.total.value,
        elasticsearch: true
      };
    } catch (error) {
      console.error('Elasticsearch search failed, falling back to regular search:', error);
      // Fallback to regular search
      return apiRequest<{ products: any[] }>(`/products/search?q=${encodeURIComponent(query)}`);
    }
  },

  searchAdvanced: (params: {
    query: string;
    filters?: {
      category?: string[];
      brand?: string[];
      minPrice?: number;
      maxPrice?: number;
      minRating?: number;
      inStock?: boolean;
    };
    from?: number;
    size?: number;
    sortBy?: 'RELEVANCE' | 'PRICE_ASC' | 'PRICE_DESC' | 'RATING_DESC' | 'NEWEST';
  }) =>
    apiRequest<{ products: any[]; total: number; facets?: any }>('/products/search/advanced', {
      method: 'POST',
      body: JSON.stringify(params),
    }),

  getSuggestions: (prefix: string) =>
    apiRequest<{ suggestions: string[] }>(`/products/search/suggestions?prefix=${encodeURIComponent(prefix)}`),

  getCategories: () =>
    apiRequest<{ categories: string[] }>('/products/categories'),

  getByCategory: (category: string) =>
    apiRequest<{ products: any[]; category: string; count: number }>(`/products/category/${encodeURIComponent(category)}`),
};

// Cart API
export const cartAPI = {
  get: () =>
    apiRequest<{ cart: any; totals: any }>('/cart'),

  addItem: (productId: number, quantity: number, size?: string) =>
    apiRequest<{ message: string; cartItem: any }>('/cart/items', {
      method: 'POST',
      body: JSON.stringify({ productId, quantity, size }),
    }),

  updateItem: (itemId: number, quantity: number) =>
    apiRequest<{ message: string; cartItem?: any }>(`/cart/items/${itemId}`, {
      method: 'PUT',
      body: JSON.stringify({ quantity }),
    }),

  removeItem: (itemId: number) =>
    apiRequest<{ message: string }>(`/cart/items/${itemId}`, {
      method: 'DELETE',
    }),

  clear: () =>
    apiRequest<{ message: string }>('/cart', {
      method: 'DELETE',
    }),

  getCount: () =>
    apiRequest<{ count: number }>('/cart/count'),
};

// Orders API
export const ordersAPI = {
  create: (orderData: {
    shippingAddress: any;
    paymentMethod: string;
    items: any[];
  }) =>
    apiRequest<{ orderId: string; message: string }>('/orders', {
      method: 'POST',
      body: JSON.stringify(orderData),
    }),

  getAll: () =>
    apiRequest<{ orders: any[] }>('/orders'),

  getById: (orderId: string) =>
    apiRequest<any>(`/orders/${orderId}`),

  cancel: (orderId: string) =>
    apiRequest<{ message: string }>(`/orders/${orderId}/cancel`, {
      method: 'POST',
    }),
};

// User API
export const userAPI = {
  getProfile: () =>
    apiRequest<any>('/user/profile'),

  updateProfile: (userData: any) =>
    apiRequest<{ message: string; user: any }>('/user/profile', {
      method: 'PUT',
      body: JSON.stringify(userData),
    }),

  changePassword: (currentPassword: string, newPassword: string) =>
    apiRequest<{ message: string }>('/user/change-password', {
      method: 'POST',
      body: JSON.stringify({ currentPassword, newPassword }),
    }),

  getAddresses: () =>
    apiRequest<{ addresses: any[] }>('/user/addresses'),

  addAddress: (address: any) =>
    apiRequest<{ message: string; address: any }>('/user/addresses', {
      method: 'POST',
      body: JSON.stringify(address),
    }),

  updateAddress: (addressId: number, address: any) =>
    apiRequest<{ message: string }>(`/user/addresses/${addressId}`, {
      method: 'PUT',
      body: JSON.stringify(address),
    }),

  deleteAddress: (addressId: number) =>
    apiRequest<{ message: string }>(`/user/addresses/${addressId}`, {
      method: 'DELETE',
    }),
};

export default {
  auth: authAPI,
  products: productsAPI,
  cart: cartAPI,
  orders: ordersAPI,
  user: userAPI,
};