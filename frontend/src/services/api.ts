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
    sortBy?: string;
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
    return apiRequest<{ products: any[]; total: number }>(
      `/products${queryParams.toString() ? `?${queryParams.toString()}` : ''}`
    );
  },

  getById: (id: number) =>
    apiRequest<any>(`/products/${id}`),

  search: (query: string) =>
    apiRequest<{ products: any[] }>(`/products/search?q=${encodeURIComponent(query)}`),

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