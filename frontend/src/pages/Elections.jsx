import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { electionsApi } from '../services/api';

function Elections() {
  const [elections, setElections] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchElections = async () => {
      let retries = 0;
      const maxRetries = 2;
      
      while (retries <= maxRetries) {
        try {
          console.log(`Attempt ${retries + 1} to fetch elections`);
          const data = await electionsApi.getElections();
          console.log("Received elections data:", data);
          
          // Handle both array and object responses
          if (Array.isArray(data)) {
            setElections(data);
            setError(null);
            break; // Success, exit loop
          } else if (data && typeof data === 'object') {
            // Check if it's an object that might contain elections data
            if (data.length !== undefined) {
              setElections(Array.isArray(data) ? data : []);
              setError(null);
              break; // Success, exit loop
            } else {
              console.warn("Received non-array data:", data);
              setElections([]);
              if (retries === maxRetries) {
                setError(`Data format error: Expected an array of elections`);
              }
            }
          } else {
            console.warn("Received unexpected data format:", data);
            setElections([]);
            if (retries === maxRetries) {
              setError(`Data format error: Expected an array of elections`);
            }
          }
          
          retries++;
          
          if (retries <= maxRetries) {
            // Wait before retry (exponential backoff)
            await new Promise(r => setTimeout(r, 1000 * retries));
          }
        } catch (error) {
          console.error(`Error fetching elections (attempt ${retries + 1}):`, error);
          
          retries++;
          
          if (retries > maxRetries) {
            // Only set the error after all retries have failed
            setError(`Failed to load elections: ${error.message || 'Network error'}`);
          } else {
            // Wait before retry (exponential backoff)
            await new Promise(r => setTimeout(r, 1000 * retries));
          }
        }
      }
      
      setLoading(false);
    };

    fetchElections();
  }, []);

  const handleVote = (electionId) => {
    navigate(`/vote/${electionId}`);
  };

  const isActive = (election) => {
    const now = new Date();
    const start = new Date(election.startTime);
    const end = new Date(election.endTime);
    return start <= now && now <= end;
  };

  if (loading) {
    return <div className="text-center">Loading elections...</div>;
  }

  if (error) {
    return <div className="alert alert-danger">{error}</div>;
  }

  return (
    <div>
      <h2 className="text-center mb-3">Available Elections</h2>
      
      {elections.length === 0 ? (
        <div className="card">
          <p className="text-center">No elections available at this time.</p>
        </div>
      ) : (
        <div className="row">
          {elections.map(election => (
            <div key={election.id} className="col-md-6 mb-3">
              <div className="card">
                <h3>{election.title}</h3>
                <p>
                  <strong>Start:</strong> {new Date(election.startTime).toLocaleString()}
                </p>
                <p>
                  <strong>End:</strong> {new Date(election.endTime).toLocaleString()}
                </p>
                <p>
                  <strong>Status:</strong>{' '}
                  <span className={isActive(election) ? "text-success" : "text-danger"}>
                    {isActive(election) ? 'Active' : 'Inactive'}
                  </span>
                </p>
                <div className="mt-3">
                  <button
                    className={`primary ${!isActive(election) ? 'disabled' : ''}`}
                    onClick={() => isActive(election) && handleVote(election.id)}
                    disabled={!isActive(election)}
                  >
                    {isActive(election) ? 'Vote' : 'Election Closed'}
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

export default Elections;