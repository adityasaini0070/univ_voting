import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { adminApi } from '../../../services/api';

function EditElection() {
  const { id } = useParams();
  const navigate = useNavigate();
  
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [startTime, setStartTime] = useState('');
  const [endTime, setEndTime] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  // Fetch election data
  useEffect(() => {
    const fetchElection = async () => {
      try {
        const data = await adminApi.getElectionById(id);
        
        setTitle(data.title);
        setDescription(data.description || '');
        
        // Format date for datetime-local input
        const formatDateForInput = (dateString) => {
          const date = new Date(dateString);
          return new Date(date.getTime() - (date.getTimezoneOffset() * 60000))
            .toISOString()
            .slice(0, 16);
        };
        
        setStartTime(formatDateForInput(data.startTime));
        setEndTime(formatDateForInput(data.endTime));
        setLoading(false);
      } catch (error) {
        console.error('Error fetching election:', error);
        setError('Failed to load election. Please try again later.');
        setLoading(false);
      }
    };

    fetchElection();
  }, [id]);

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
      
      await adminApi.updateElection(id, {
        title,
        description,
        startTime: formattedStartTime,
        endTime: formattedEndTime
      });
      
      // Navigate back to elections list
      navigate('/admin/elections');
    } catch (error) {
      console.error('Error updating election:', error);
      setError('Failed to update election. Please try again later.');
      setIsSubmitting(false);
    }
  };

  if (loading) {
    return <div className="text-center">Loading election details...</div>;
  }

  if (error && !isSubmitting) {
    return <div className="alert alert-danger">{error}</div>;
  }

  return (
    <div>
      <h3>Edit Election</h3>
      
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
            {isSubmitting ? 'Updating...' : 'Update Election'}
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

export default EditElection;