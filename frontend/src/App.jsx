import { BrowserRouter as Router, Route, Routes, Link, Navigate } from 'react-router-dom'
import { useAuth } from './context/AuthContext'
import './App.css'

// Import pages
import Home from './pages/Home'
import Login from './pages/Login'
import Register from './pages/Register'
import Elections from './pages/Elections'
import Vote from './pages/Vote'
import Profile from './pages/Profile'
import AdminDashboard from './pages/admin/Dashboard'
import NotFound from './pages/NotFound'

function App() {
  const { user, loading, isAdmin, logout } = useAuth()

  if (loading) {
    return <div className="loading-container">Loading...</div>
  }

  return (
    <Router>
      <div className="app">
        <nav className="navbar">
          <div className="navbar-container container">
            <Link to="/" className="navbar-brand">University Voting System</Link>
            <ul className="navbar-nav">
              <li>
                <Link to="/elections" className="nav-link">Elections</Link>
              </li>
              {user ? (
                <>
                  <li>
                    <Link to="/profile" className="nav-link">Profile</Link>
                  </li>
                  {isAdmin && (
                    <li>
                      <Link to="/admin/dashboard" className="nav-link">Admin</Link>
                    </li>
                  )}
                  <li>
                    <button 
                      onClick={(e) => {
                        e.preventDefault();
                        logout();
                        // Force reload after logout to clear any state
                        setTimeout(() => window.location.href = '/login', 100);
                      }} 
                      className="nav-link btn-link">
                      Logout
                    </button>
                  </li>
                </>
              ) : (
                <>
                  <li>
                    <Link to="/login" className="nav-link">Login</Link>
                  </li>
                  <li>
                    <Link to="/register" className="nav-link">Register</Link>
                  </li>
                </>
              )}
            </ul>
          </div>
        </nav>

        <div className="container">
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/login" element={user ? <Navigate to="/elections" /> : <Login />} />
            <Route path="/register" element={user ? <Navigate to="/elections" /> : <Register />} />
            <Route path="/elections" element={<Elections />} />
            <Route path="/vote/:electionId" element={user ? <Vote /> : <Navigate to="/login" />} />
            <Route path="/profile" element={user ? <Profile /> : <Navigate to="/login" />} />
            <Route path="/admin/*" element={isAdmin ? <AdminDashboard /> : <Navigate to="/" />} />
            <Route path="*" element={<NotFound />} />
          </Routes>
        </div>
      </div>
    </Router>
  )
}

export default App
