import { Link } from 'react-router-dom';

function NotFound() {
  return (
    <div className="card text-center">
      <h2>404 - Page Not Found</h2>
      <p>The page you are looking for does not exist.</p>
      <Link to="/" className="primary">Return to Home</Link>
    </div>
  );
}

export default NotFound;