package com.univvoting.service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.univvoting.model.Election;
import com.univvoting.model.User;

public interface AdminService {
    Election createElection(String name, Instant startTime, Instant endTime);
    void createCandidate(String name, String description, UUID electionId);
    List<Election> getAllElections();
    void deleteElection(UUID id);
    void updateElection(UUID id, String name, Instant startTime, Instant endTime);
    
    // User management methods
    List<User> getAllUsers();
    void deleteUser(UUID userId);
}
