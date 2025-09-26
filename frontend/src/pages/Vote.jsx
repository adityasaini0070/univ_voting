import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { electionsApi } from '../services/api';
import { useAuth } from '../context/AuthContext';

function Vote() {
  const { electionId } = useParams();
  const [election, setElection] = useState(null);
  const [candidates, setCandidates] = useState([]);
  const [selectedCandidate, setSelectedCandidate] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(null);
  const [hasVoted, setHasVoted] = useState(false);
  const { user } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    const fetchElection = async () => {
      try {
        const data = await electionsApi.getElectionById(electionId);
        setElection(data);
        setCandidates(data.candidates || []);
        
        // Check if user has already voted
        setHasVoted(data.hasUserVoted || false);
      } catch (error) {
        console.error('Error fetching election:', error);
        setError('Failed to load election details. Please try again later.');
      } finally {
        setLoading(false);
      }
    };

    if (electionId) {
      fetchElection();
    }
  }, [electionId]);

  const handleVote = async () => {
    if (!selectedCandidate) {
      setError('Please select a candidate to vote for');
      return;
    }

    try {
      await electionsApi.castVote({
        electionId,
        candidateId: selectedCandidate,
        voterId: user.id
      });
      
      setSuccess('Your vote has been recorded successfully!');
      setHasVoted(true);
      
      // Redirect after a short delay
      setTimeout(() => {
        navigate('/elections');
      }, 3000);
    } catch (error) {
      console.error('Error casting vote:', error);
      setError('Failed to cast your vote. Please try again later.');
    }
  };

  if (loading) {
    return <div className="text-center">Loading election details...</div>;
  }

  if (error) {
    return <div className="alert alert-danger">{error}</div>;
  }

  if (!election) {
    return <div className="alert alert-danger">Election not found</div>;
  }

  if (hasVoted) {
    return (
      <div className="card">
        <div className="alert alert-success">
          {success || 'You have already voted in this election.'}
        </div>
        <div className="text-center">
          <button className="primary" onClick={() => navigate('/elections')}>
            Back to Elections
          </button>
        </div>
      </div>
    );
  }

  return (
    <div>
      <h2 className="text-center mb-3">Vote: {election.title}</h2>
      
      <div className="card mb-3">
        <p><strong>Description:</strong> {election.description}</p>
        <p><strong>Starts:</strong> {new Date(election.startTime).toLocaleString()}</p>
        <p><strong>Ends:</strong> {new Date(election.endTime).toLocaleString()}</p>
      </div>

      {candidates.length === 0 ? (
        <div className="alert alert-warning">No candidates available for this election.</div>
      ) : (
        <div className="card mb-3">
          <h3>Candidates</h3>
          {candidates.map(candidate => (
            <div key={candidate.id} className="form-group">
              <label className="radio-label">
                <input
                  type="radio"
                  name="candidate"
                  value={candidate.id}
                  checked={selectedCandidate === candidate.id}
                  onChange={() => setSelectedCandidate(candidate.id)}
                />
                <span style={{ marginLeft: '10px' }}>{candidate.name}</span>
                {candidate.description && (
                  <p className="text-muted" style={{ marginLeft: '25px' }}>
                    {candidate.description}
                  </p>
                )}
              </label>
            </div>
          ))}
        </div>
      )}

      <div className="text-center">
        <button
          className="primary"
          onClick={handleVote}
          disabled={!selectedCandidate || candidates.length === 0}
        >
          Submit Vote
        </button>
      </div>
    </div>
  );
}

export default Vote;