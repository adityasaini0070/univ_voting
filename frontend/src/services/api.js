/**
 * API service for making HTTP requests to the backend
 */

const API_BASE_URL = 'http://localhost:8080/api';

// Get CSRF token from cookies
const getCsrfToken = () => {
  const match = document.cookie.match(/XSRF-TOKEN=([^;]+)/);
  const token = match ? match[1] : '';
  
  // Decode the token if it's URI encoded
  try {
    return token ? decodeURIComponent(token) : '';
  } catch (e) {
    console.warn("Error decoding CSRF token:", e);
    return token;
  }
};

// Helper function for handling API responses
const handleResponse = async (response) => {
  if (!response.ok) {
    // If unauthorized, redirect to login
    if (response.status === 401 || response.status === 403) {
      console.error('Authentication error:', response.status);
      // If it's an API call, throw an error
      if (response.url.includes('/api/')) {
        throw new Error('Your session has expired. Please log in again.');
      }
    }
    
    // Check if the response is JSON
    const contentType = response.headers.get('content-type');
    if (contentType && contentType.includes('application/json')) {
      const data = await response.json();
      const error = data.error || 'An error occurred';
      throw new Error(error);
    } else {
      throw new Error(`HTTP error! Status: ${response.status}`);
    }
  }
  
  try {
    const data = await response.json();
    return data;
  } catch (err) {
    console.error('Error parsing response:', err);
    return null; // Return null for empty responses
  }
};

// Auth API
export const authApi = {
  /**
   * Login user
   * @param {Object} credentials - User credentials
   * @returns {Promise<Object>} - User data
   */
  login: async (credentials) => {
    console.log('Logging in with credentials:', credentials.universityId);
    
    try {
      // Try the new auth endpoint first
      const response = await fetch(`${API_BASE_URL}/auth/login`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        credentials: 'include',
        body: JSON.stringify(credentials),
      });
      
      console.log('Login response status:', response.status);
      console.log('Login response cookies:', document.cookie);
      
      if (response.ok) {
        const result = await handleResponse(response);
        console.log('Login response data:', result);
        
        // Let's force a CSRF token refresh by calling a simple GET endpoint
        try {
          await fetch(`${API_BASE_URL}/auth/current`, {
            credentials: 'include'
          });
        } catch (e) {
          console.log('Error refreshing CSRF token, continuing anyway');
        }
        
        return result;
      }
      
      console.log('New auth endpoint failed, trying legacy endpoint');
      
      // Fall back to the old endpoint if the new one fails
      const legacyResponse = await fetch(`${API_BASE_URL}/user/login`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        credentials: 'include',
        body: JSON.stringify(credentials),
      });
      
      const result = await handleResponse(legacyResponse);
      console.log('Login response data:', result);
      
      // Let's force a CSRF token refresh by calling a simple GET endpoint
      try {
        await fetch(`${API_BASE_URL}/user/current`, {
          credentials: 'include'
        });
      } catch (e) {
        console.log('Error refreshing CSRF token, continuing anyway');
      }
      
      return result;
    } catch (error) {
      console.error('Error during login:', error);
      throw error;
    }
  },

  /**
   * Register new user
   * @param {Object} userData - User registration data
   * @returns {Promise<Object>} - Registration result
   */
  register: async (userData) => {
    const response = await fetch(`${API_BASE_URL}/user/register`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(userData),
    });
    return handleResponse(response);
  },

  /**
   * Get current logged in user
   * @returns {Promise<Object>} - User data
   */
  getCurrentUser: async () => {
    // Try the new auth endpoint first
    try {
      console.log('Fetching current user and refreshing CSRF token');
      
      const response = await fetch(`${API_BASE_URL}/auth/current`, {
        credentials: 'include',
        headers: {
          'Accept': 'application/json',
          'Cache-Control': 'no-cache, no-store'
        }
      });
      
      if (response.ok) {
        // Successfully got user and refreshed CSRF token
        const userData = await handleResponse(response);
        console.log('Current CSRF token after refresh:', getCsrfToken());
        return userData;
      }
      
      console.log('New auth endpoint failed, trying legacy endpoint');
      
      // Fall back to the old endpoint if the new one fails
      const legacyResponse = await fetch(`${API_BASE_URL}/user/current`, {
        credentials: 'include',
        headers: {
          'Accept': 'application/json',
          'Cache-Control': 'no-cache, no-store'
        }
      });
      
      return handleResponse(legacyResponse);
    } catch (error) {
      console.error('Error getting current user:', error);
      throw error;
    }
  },

  /**
   * Logout user
   * @returns {Promise<void>}
   */
  logout: async () => {
    try {
      // Try the new auth endpoint first
      const response = await fetch(`${API_BASE_URL}/auth/logout`, {
        method: 'POST',
        credentials: 'include',
        headers: {
          'Content-Type': 'application/json',
        }
      });
      
      if (response.ok) {
        return true;
      }
      
      // Try the legacy API endpoint next
      const legacyResponse = await fetch(`${API_BASE_URL}/user/logout`, {
        method: 'POST',
        credentials: 'include',
        headers: {
          'Content-Type': 'application/json',
        }
      });
      
      if (legacyResponse.ok) {
        return true;
      }
      
      // Fall back to Spring Security's default logout endpoint
      const fallbackResponse = await fetch(`http://localhost:8080/logout`, {
        method: 'POST',
        credentials: 'include',
        headers: {
          'Content-Type': 'application/json',
          // Include CSRF token if it exists
          ...(document.cookie.includes('XSRF-TOKEN') && {
            'X-XSRF-TOKEN': document.cookie
              .split('; ')
              .find(row => row.startsWith('XSRF-TOKEN'))
              ?.split('=')[1]
          })
        }
      });
      
      // Consider it successful no matter what to ensure frontend state is cleared
      return true;
    } catch (error) {
      console.error('Error during logout request:', error);
      // Return true anyway to ensure frontend state is cleared
      return true;
    }
  },
};

// Elections API
export const electionsApi = {
  /**
   * Get all elections
   * @returns {Promise<Array>} - List of elections
   */
  getElections: async () => {
    try {
      console.log('Fetching elections from API...');
      
      // Use a direct fetch with no CSRF for this public endpoint
      // This makes it simpler and avoids potential CSRF issues
      const response = await fetch(`${API_BASE_URL}/public/election-list`, {
        method: 'GET',
        mode: 'cors',  // Ensure CORS mode is explicitly set
        cache: 'no-cache',  // Avoid caching issues
        headers: {
          'Accept': 'application/json',
          'Content-Type': 'application/json'
        }
      });
      
      console.log('Elections API response status:', response.status);
      
      if (response.ok) {
        const data = await response.json();
        console.log('Elections data received:', data);
        return data;
      }
      
      // If the new endpoint fails, try the original endpoint as fallback
      console.log('New endpoint failed, falling back to original endpoint');
      
      const fallbackResponse = await fetch(`${API_BASE_URL}/public/elections`, {
        method: 'GET',
        mode: 'cors',  // Ensure CORS mode is explicitly set
        cache: 'no-cache',  // Avoid caching issues
        headers: {
          'Accept': 'application/json',
          'Content-Type': 'application/json'
        }
      });
      
      console.log('Fallback Elections API response status:', fallbackResponse.status);
      
      if (!fallbackResponse.ok) {
        const errorText = await fallbackResponse.text();
        console.error('Error response:', errorText);
        throw new Error(`Failed to fetch elections: ${fallbackResponse.status} ${fallbackResponse.statusText}`);
      }
      
      const data = await fallbackResponse.json();
      console.log('Elections data received from fallback:', data);
      return data;
    } catch (error) {
      console.error('Error in getElections:', error);
      throw error;
    }
  },

  /**
   * Get election by ID
   * @param {string} id - Election ID
   * @returns {Promise<Object>} - Election data
   */
  getElectionById: async (id) => {
    const response = await fetch(`${API_BASE_URL}/elections/${id}`, {
      credentials: 'include',
    });
    return handleResponse(response);
  },

  /**
   * Cast vote
   * @param {Object} voteData - Vote data
   * @returns {Promise<Object>} - Vote result
   */
  castVote: async (voteData) => {
    const response = await fetch(`${API_BASE_URL}/vote`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      credentials: 'include',
      body: JSON.stringify(voteData),
    });
    return handleResponse(response);
  },
};

// Admin API
export const adminApi = {
  /**
   * Get all users
   * @returns {Promise<Array>} - List of users
   */
  getUsers: async () => {
    const csrfToken = getCsrfToken();
    const response = await fetch(`${API_BASE_URL}/admin/users`, {
      credentials: 'include',
      headers: {
        ...(csrfToken && { 'X-XSRF-TOKEN': csrfToken })
      },
    });
    return handleResponse(response);
  },
  
  /**
   * Get all elections (admin view)
   * @returns {Promise<Array>} - List of elections
   */
  getElections: async () => {
    // First ensure we have an updated CSRF token by refreshing the user session
    try {
      // Try to refresh the user session first to get a fresh CSRF token
      await authApi.getCurrentUser().catch(e => console.log('Refreshing user session failed, continuing anyway'));
      
      // Get the latest token after refresh
      const csrfToken = getCsrfToken();
      console.log('Using CSRF token:', csrfToken);
      
      // Go directly to the secured admin endpoint
      const adminResponse = await fetch(`${API_BASE_URL}/admin/elections`, {
        credentials: 'include',
        cache: 'no-cache',
        headers: {
          'Accept': 'application/json',
          'X-XSRF-TOKEN': csrfToken || ''  // Always include the header, even if empty
        },
      });
      
      console.log('Admin elections API response status:', adminResponse.status);
      
      if (adminResponse.status === 401 || adminResponse.status === 403) {
        console.error('Authentication error:', adminResponse.status);
        throw new Error('Session expired or not authorized. Please log in again.');
      }
      
      const data = await handleResponse(adminResponse);
      console.log('Admin elections data received:', data);
      return data;
    } catch (error) {
      console.error("Error fetching admin elections:", error);
      throw error;
    }
  },

  /**
   * Create new election
   * @param {Object} electionData - Election data
   * @returns {Promise<Object>} - Created election
   */
  createElection: async (electionData) => {
    console.log('Creating election with data:', electionData);
    const csrfToken = getCsrfToken();
    
    try {
      // Go directly to admin endpoint
      const adminResponse = await fetch(`${API_BASE_URL}/admin/elections`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          ...(csrfToken && { 'X-XSRF-TOKEN': csrfToken })
        },
        credentials: 'include',
        body: JSON.stringify(electionData),
      });
      
      console.log('Admin create election response status:', adminResponse.status);
      
      if (adminResponse.status === 401 || adminResponse.status === 403) {
        console.error('Authentication error:', adminResponse.status);
        throw new Error('Session expired or not authorized. Please log in again.');
      }
      
      return handleResponse(adminResponse);
    } catch (error) {
      console.error('Error creating election:', error);
      throw error;
    }
  },

  /**
   * Update election
   * @param {string} id - Election ID
   * @param {Object} electionData - Election data
   * @returns {Promise<Object>} - Updated election
   */
  updateElection: async (id, electionData) => {
    console.log(`Updating election ${id} with data:`, electionData);
    const csrfToken = getCsrfToken();
    
    try {
      // Go directly to admin endpoint
      const adminResponse = await fetch(`${API_BASE_URL}/admin/elections/${id}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          ...(csrfToken && { 'X-XSRF-TOKEN': csrfToken })
        },
        credentials: 'include',
        body: JSON.stringify(electionData),
      });
      
      console.log('Admin update election response status:', adminResponse.status);
      
      if (adminResponse.status === 401 || adminResponse.status === 403) {
        console.error('Authentication error:', adminResponse.status);
        throw new Error('Session expired or not authorized. Please log in again.');
      }
      
      return handleResponse(adminResponse);
    } catch (error) {
      console.error(`Error updating election ${id}:`, error);
      throw error;
    }
  },

  /**
   * Get election by ID
   * @param {string} id - Election ID
   * @returns {Promise<Object>} - Election data
   */
  getElectionById: async (id) => {
    const csrfToken = getCsrfToken();
    const response = await fetch(`${API_BASE_URL}/admin/elections/${id}`, {
      credentials: 'include',
      headers: {
        ...(csrfToken && { 'X-XSRF-TOKEN': csrfToken })
      },
    });
    return handleResponse(response);
  },

  /**
   * Delete election
   * @param {string} id - Election ID
   * @returns {Promise<void>}
   */
  deleteElection: async (id) => {
    console.log(`Deleting election ${id}`);
    const csrfToken = getCsrfToken();
    
    try {
      // Go directly to admin endpoint
      const adminResponse = await fetch(`${API_BASE_URL}/admin/elections/${id}`, {
        method: 'DELETE',
        credentials: 'include',
        headers: {
          ...(csrfToken && { 'X-XSRF-TOKEN': csrfToken })
        },
      });
      
      console.log('Admin delete election response status:', adminResponse.status);
      
      if (adminResponse.status === 401 || adminResponse.status === 403) {
        console.error('Authentication error:', adminResponse.status);
        throw new Error('Session expired or not authorized. Please log in again.');
      }
      
      return handleResponse(adminResponse);
    } catch (error) {
      console.error(`Error deleting election ${id}:`, error);
      throw error;
    }
  },

  /**
   * Add candidate to election
   * @param {Object} candidateData - Candidate data
   * @returns {Promise<Object>} - Created candidate
   */
  addCandidate: async (candidateData) => {
    const csrfToken = getCsrfToken();
    const response = await fetch(`${API_BASE_URL}/admin/candidates`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        ...(csrfToken && { 'X-XSRF-TOKEN': csrfToken })
      },
      credentials: 'include',
      body: JSON.stringify(candidateData),
    });
    return handleResponse(response);
  },
};