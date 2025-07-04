import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { AuthProvider } from './contexts/AuthContext';
import PrivateRoute from './components/common/PrivateRoute';
import LandingPage from './pages/customer/LandingPage';
import LoginPage from './pages/customer/LoginPage';
import RegisterPage from './pages/customer/RegisterPage';
import ForgotPasswordPage from './pages/customer/ForgotPasswordPage';
import ProductsPage from './pages/customer/ProductsPage';
import ProductDetailPage from './pages/customer/ProductDetailPage';
import CartPage from './pages/customer/CartPage';
import CheckoutPage from './pages/customer/CheckoutPage';
import UserProfilePage from './pages/customer/UserProfilePage';

function App() {
  return (
    <AuthProvider>
      <Router>
        <Routes>
          <Route path="/" element={<LandingPage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
          <Route path="/forgot-password" element={<ForgotPasswordPage />} />
          <Route path="/products" element={<ProductsPage />} />
          <Route path="/product/:id" element={<ProductDetailPage />} />
          <Route path="/cart" element={<CartPage />} />
          <Route path="/checkout" element={
            <PrivateRoute>
              <CheckoutPage />
            </PrivateRoute>
          } />
          <Route path="/profile" element={
            <PrivateRoute>
              <UserProfilePage />
            </PrivateRoute>
          } />
        </Routes>
      </Router>
    </AuthProvider>
  );
}

export default App;
