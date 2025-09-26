import { useState, useEffect } from 'react';
import { Routes, Route, useNavigate } from 'react-router-dom';
import ElectionsList from './elections/ElectionsList';
import CreateElection from './elections/CreateElection';
import EditElection from './elections/EditElection';

function AdminElectionsRoot() {
  const navigate = useNavigate();

  return (
    <div>
      <Routes>
        <Route path="/" element={<ElectionsList />} />
        <Route path="/create" element={<CreateElection />} />
        <Route path="/edit/:id" element={<EditElection />} />
      </Routes>
    </div>
  );
}

function AdminUsers() {
  return (
    <div>
      <h3>User Management</h3>
      <p>This feature is coming soon.</p>
    </div>
  );
}

function AdminCandidates() {
  return (
    <div>
      <h3>Candidate Management</h3>
      <p>This feature is coming soon.</p>
    </div>
  );
}

export { AdminElectionsRoot, AdminUsers, AdminCandidates };