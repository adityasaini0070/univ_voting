import { useState, useEffect } from 'react';
import { Routes, Route, NavLink, useNavigate } from 'react-router-dom';
import { adminApi } from '../../services/api';
import { useAuth } from '../../context/AuthContext';
import { AdminElectionsRoot, AdminUsers, AdminCandidates } from './AdminPages';

function AdminDashboard() {
  const [stats, setStats] = useState({
    activeElections: 0,
    registeredUsers: 0,
    totalVotes: 0,
  });
  const [loading, setLoading] = useState(true);
  const { isAdmin } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    // Redirect if not admin
    if (!isAdmin) {
      navigate('/');
      return;
    }

    // Fetch dashboard stats
    const fetchStats = async () => {
      try {
        // This would call an API endpoint to get the stats
        // For now, we'll just use dummy data
        setTimeout(() => {
          setStats({
            activeElections: 2,
            registeredUsers: 150,
            totalVotes: 75,
          });
          setLoading(false);
        }, 500);
      } catch (error) {
        console.error('Error fetching stats:', error);
        setLoading(false);
      }
    };

    fetchStats();
  }, [isAdmin, navigate]);

  if (loading) {
    return <div className="text-center">Loading dashboard...</div>;
  }

  return (
    <div>
      <h2 className="text-center mb-3">Admin Dashboard</h2>
      
      <div className="card mb-3">
        <div className="admin-nav">
          <ul className="navbar-nav" style={{ display: 'flex', flexDirection: 'row', gap: '1rem' }}>
            <li>
              <NavLink 
                to="/admin/elections" 
                className={({isActive}) => isActive ? "nav-link active" : "nav-link"}
              >
                Manage Elections
              </NavLink>
            </li>
            <li>
              <NavLink 
                to="/admin/users" 
                className={({isActive}) => isActive ? "nav-link active" : "nav-link"}
              >
                Manage Users
              </NavLink>
            </li>
            <li>
              <NavLink 
                to="/admin/candidates" 
                className={({isActive}) => isActive ? "nav-link active" : "nav-link"}
              >
                Manage Candidates
              </NavLink>
            </li>
          </ul>
        </div>
      </div>
      
      <div className="row mb-4">
        <div className="col-md-6 col-lg-4 mb-3">
          <div className="card text-center">
            <h3>Active Elections</h3>
            <div className="stat-value" style={{ fontSize: '2rem', fontWeight: 'bold' }}>
              {stats.activeElections}
            </div>
          </div>
        </div>
        
        <div className="col-md-6 col-lg-4 mb-3">
          <div className="card text-center">
            <h3>Registered Users</h3>
            <div className="stat-value" style={{ fontSize: '2rem', fontWeight: 'bold' }}>
              {stats.registeredUsers}
            </div>
          </div>
        </div>
        
        <div className="col-md-6 col-lg-4 mb-3">
          <div className="card text-center">
            <h3>Total Votes</h3>
            <div className="stat-value" style={{ fontSize: '2rem', fontWeight: 'bold' }}>
              {stats.totalVotes}
            </div>
          </div>
        </div>
      </div>
      
      <div className="card">
        <Routes>
          <Route path="/" element={<div className="text-center">Select a section to manage</div>} />
          <Route path="elections/*" element={<AdminElectionsRoot />} />
          <Route path="users" element={<AdminUsers />} />
          <Route path="candidates" element={<AdminCandidates />} />
        </Routes>
      </div>
    </div>
  );
}

export default AdminDashboard;