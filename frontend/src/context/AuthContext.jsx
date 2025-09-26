import { createContext, useContext, useState, useEffect } from 'react';
import { authApi } from '../services/api';

// Create the Auth Context
const AuthContext = createContext(null);

// Auth Provider Component
export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Check if user is already logged in when component mounts
  useEffect(() => {
    const checkLoginStatus = async () => {
      try {
        const user = await authApi.getCurrentUser();
        setUser(user);
      } catch (error) {
        console.error('Error checking login status:', error);
        setUser(null);
      } finally {
        setLoading(false);
      }
    };

    checkLoginStatus();
  }, []);

  // Login function
  const login = async (credentials) => {
    try {
      setError(null);
      const userData = await authApi.login(credentials);
      setUser(userData);
      return userData;
    } catch (error) {
      setError(error.message);
      throw error;
    }
  };

  // Logout function
  const logout = async () => {
    try {
      await authApi.logout();
      // Always clear user state regardless of response
      setUser(null);
      // Clear any stored tokens or session data
      localStorage.removeItem('user');
      sessionStorage.removeItem('user');
    } catch (error) {
      console.error('Error logging out:', error);
      setError(error.message);
      // Still clear user state even if there's an error
      setUser(null);
    }
  };

  // Register function
  const register = async (userData) => {
    try {
      setError(null);
      return await authApi.register(userData);
    } catch (error) {
      setError(error.message);
      throw error;
    }
  };

  // Check if user is admin
  const isAdmin = user && user.roles && user.roles.some(role => role.authority === 'ROLE_ADMIN');

  // Context value
  const value = {
    user,
    loading,
    error,
    isAdmin,
    login,
    logout,
    register,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

// Custom hook for using auth context
export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};