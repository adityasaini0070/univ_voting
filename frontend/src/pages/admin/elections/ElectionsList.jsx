import { useState, useEffect } from 'react';
import { adminApi, authApi } from '../../../services/api';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../../context/AuthContext';

function AdminElections() {
  const [elections, setElections] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const { user, isAdmin } = useAuth();
  const navigate = useNavigate();

  const fetchElections = async () => {
    try {
      setLoading(true);
      console.log("Fetching elections from admin API...");
      const data = await adminApi.getElections();
      console.log("Elections data received:", data);
      setElections(Array.isArray(data) ? data : []);
      setError(null);
      setLoading(false);
    } catch (error) {
      console.error('Error fetching elections:', error);
      setError('Failed to load elections. Please try again later. Error: ' + error.message);
      setLoading(false);
    }
  };

  useEffect(() => {
    // First check if user is authenticated and is an admin
    if (!user) {
      setError('Please log in to access the admin area.');
      setLoading(false);
      return;
    }

    if (!isAdmin) {
      setError('You do not have permission to access the admin area.');
      setLoading(false);
      return;
    }

    fetchElections();
  }, [user, isAdmin]);

  const handleEdit = (id) => {
    // Navigate to edit page
    window.location.href = `/admin/elections/edit/${id}`;
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Are you sure you want to delete this election?')) {
      return;
    }

    try {
      await adminApi.deleteElection(id);
      // Remove from state
      setElections(elections.filter(election => election.id !== id));
    } catch (error) {
      console.error('Error deleting election:', error);
      setError('Failed to delete election. Please try again later.');
    }
  };

  if (loading) {
    return <div className="text-center">Loading elections...</div>;
  }

  if (error) {
    // If the error is about authentication, offer a login button
    if (error.includes('Please log in')) {
      return (
        <div>
          <div className="alert alert-warning">{error}</div>
          <button className="primary" onClick={() => navigate('/login')}>
            Go to Login
          </button>
        </div>
      );
    }
    return <div className="alert alert-danger">{error}</div>;
  }

  return (
    <div>
      <div className="d-flex justify-content-between align-items-center mb-3">
        <h3>Manage Elections</h3>
        <div>
          <button className="secondary mr-2" onClick={fetchElections} disabled={loading}>
            {loading ? 'Refreshing...' : 'Refresh Elections'}
          </button>
          <button className="primary" onClick={() => window.location.href = '/admin/elections/create'}>
            Create New Election
          </button>
        </div>
      </div>

      {elections.length === 0 ? (
        <p>No elections available.</p>
      ) : (
        <div>
          {elections.map(election => (
            <div key={election.id} className="card mb-3">
              <div className="d-flex justify-content-between">
                <div>
                  <h4>{election.title}</h4>
                  <p>{election.description}</p>
                  <p>
                    <strong>Period:</strong>{' '}
                    {new Date(election.startTime).toLocaleDateString()} - {new Date(election.endTime).toLocaleDateString()}
                  </p>
                  <p>
                    <strong>Status:</strong>{' '}
                    <span className={election.active ? "text-success" : "text-danger"}>
                      {election.active ? 'Active' : 'Inactive'}
                    </span>
                  </p>
                </div>
                <div>
                  <button className="btn" onClick={() => handleEdit(election.id)}>Edit</button>
                  <button 
                    style={{ marginLeft: '5px', color: '#dc3545' }} 
                    onClick={() => handleDelete(election.id)}
                  >
                    Delete
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

export default AdminElections;