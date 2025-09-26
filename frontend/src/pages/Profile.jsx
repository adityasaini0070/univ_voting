import { useAuth } from '../context/AuthContext';

function Profile() {
  const { user } = useAuth();

  if (!user) {
    return <div>Please log in to view your profile</div>;
  }

  return (
    <div className="card">
      <h2 className="text-center">Your Profile</h2>
      
      <div className="mb-3">
        <div className="form-group">
          <label className="form-label">University ID:</label>
          <div className="form-control">{user.username}</div>
        </div>
        
        <div className="form-group">
          <label className="form-label">Role:</label>
          <div className="form-control">
            {user.roles?.map(role => role.authority.replace('ROLE_', '')).join(', ') || 'VOTER'}
          </div>
        </div>

        {user.fullName && (
          <div className="form-group">
            <label className="form-label">Full Name:</label>
            <div className="form-control">{user.fullName}</div>
          </div>
        )}

        {user.phoneNumber && (
          <div className="form-group">
            <label className="form-label">Phone Number:</label>
            <div className="form-control">{user.phoneNumber}</div>
          </div>
        )}
      </div>
      
      <div className="mb-3">
        <button className="primary">Update Profile</button>
        <button style={{ marginLeft: '10px' }}>Change Password</button>
      </div>
      
      <div className="mt-3">
        <h3>Your Voting History</h3>
        <p>You have not participated in any elections yet.</p>
      </div>
    </div>
  );
}

export default Profile;