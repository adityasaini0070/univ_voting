import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

function Home() {
  const { user } = useAuth();

  return (
    <div>
      <div className="text-center">
        <h1>University Voting System</h1>
        <p className="mb-3">
          Welcome to the University Voting System, a secure platform for conducting elections
          within the university community.
        </p>
      </div>
      
      <div className="row mb-4">
        <div className="col-md-6 col-lg-4 mb-3">
          <div className="card">
            <h3>Secure Voting</h3>
            <p>
              Our system ensures that each eligible voter can only vote once per election,
              maintaining the integrity of the electoral process.
            </p>
          </div>
        </div>
        
        <div className="col-md-6 col-lg-4 mb-3">
          <div className="card">
            <h3>Easy Access</h3>
            <p>
              Access the voting system using your university credentials to participate in
              various elections throughout the academic year.
            </p>
          </div>
        </div>
        
        <div className="col-md-6 col-lg-4 mb-3">
          <div className="card">
            <h3>Real-time Results</h3>
            <p>
              Once an election concludes, results are promptly tabulated and made available
              to all participants.
            </p>
          </div>
        </div>
      </div>
      
      <div className="text-center mb-3">
        <Link to="/elections" className="primary">View Active Elections</Link>
        {!user && (
          <Link to="/register" className="btn" style={{marginLeft: '10px'}}>Register Now</Link>
        )}
      </div>
    </div>
  );
}

export default Home;