package com.univvoting.service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.lang.NonNull;

import com.univvoting.model.Election;
import com.univvoting.model.User;

public interface AdminService {
    Election createElection(String name, Instant startTime, Instant endTime);

    void createCandidate(String name, String description, UUID electionId);

    List<Election> getAllElections();

    void deleteElection(@NonNull UUID id);

    void updateElection(@NonNull UUID id, String name, Instant startTime, Instant endTime);

    // User management methods
    List<User> getAllUsers();

    void deleteUser(@NonNull UUID userId);
}
