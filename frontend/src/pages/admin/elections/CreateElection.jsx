import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { adminApi } from '../../../services/api';

function CreateElection() {
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [startTime, setStartTime] = useState('');
  const [endTime, setEndTime] = useState('');
  const [error, setError] = useState(null);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    setIsSubmitting(true);

    // Validation
    if (!title || !startTime || !endTime) {
      setError('Title, start time and end time are required');
      setIsSubmitting(false);
      return;
    }

    // Check that end time is after start time
    const start = new Date(startTime);
    const end = new Date(endTime);
    if (end <= start) {
      setError('End time must be after start time');
      setIsSubmitting(false);
      return;
    }

    try {
      // Convert datetime-local values to ISO strings
      const formattedStartTime = new Date(startTime).toISOString();
      const formattedEndTime = new Date(endTime).toISOString();
      
      await adminApi.createElection({
        title,
        description,
        startTime: formattedStartTime,
        endTime: formattedEndTime
      });
      
      // Navigate back to elections list
      navigate('/admin/elections');
    } catch (error) {
      console.error('Error creating election:', error);
      setError('Failed to create election. Please try again later.');
      setIsSubmitting(false);
    }
  };

  return (
    <div>
      <h3>Create New Election</h3>
      
      {error && <div className="alert alert-danger">{error}</div>}
      
      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label htmlFor="title">Title:</label>
          <input
            type="text"
            id="title"
            className="form-control"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            required
          />
        </div>
        
        <div className="form-group">
          <label htmlFor="description">Description:</label>
          <textarea
            id="description"
            className="form-control"
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            rows={3}
            required
          />
        </div>
        
        <div className="form-group">
          <label htmlFor="startTime">Start Time:</label>
          <input
            type="datetime-local"
            id="startTime"
            className="form-control"
            value={startTime}
            onChange={(e) => setStartTime(e.target.value)}
            required
          />
        </div>
        
        <div className="form-group">
          <label htmlFor="endTime">End Time:</label>
          <input
            type="datetime-local"
            id="endTime"
            className="form-control"
            value={endTime}
            onChange={(e) => setEndTime(e.target.value)}
            required
          />
        </div>
        
        <div className="form-group mt-3">
          <button 
            type="submit" 
            className="primary"
            disabled={isSubmitting}
          >
            {isSubmitting ? 'Creating...' : 'Create Election'}
          </button>
          <button 
            type="button" 
            className="btn"
            style={{ marginLeft: '10px' }}
            onClick={() => navigate('/admin/elections')}
            disabled={isSubmitting}
          >
            Cancel
          </button>
        </div>
      </form>
    </div>
  );
}

export default CreateElection;