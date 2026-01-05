// ...existing code...
package com.univvoting.service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.univvoting.model.Candidate;
import com.univvoting.model.Election;
import com.univvoting.model.User;
import com.univvoting.repository.CandidateRepository;
import com.univvoting.repository.ElectionRepository;
import com.univvoting.repository.OtpAttemptRepository;
import com.univvoting.repository.UserRepository;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private ElectionRepository electionRepository;

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OtpAttemptRepository otpAttemptRepository;

    @Override
    public Election createElection(String name, Instant startTime, Instant endTime) {
        if (name == null) {
            throw new NullPointerException("Election name cannot be null");
        }
        if (startTime == null) {
            throw new NullPointerException("Start time cannot be null");
        }
        if (endTime == null) {
            throw new NullPointerException("End time cannot be null");
        }

        Election election = new Election();
        election.setTitle(name);
        election.setStartTime(startTime);
        election.setEndTime(endTime);
        return electionRepository.save(election);
    }

    @Override
    public void createCandidate(String name, String description, UUID electionId) {
        if (name == null) {
            throw new NullPointerException("Candidate name cannot be null");
        }
        if (description == null) {
            throw new NullPointerException("Candidate description cannot be null");
        }
        if (electionId == null) {
            throw new NullPointerException("Election ID cannot be null");
        }

        Candidate candidate = new Candidate();
        candidate.setName(name);
        candidate.setManifesto(description);
        candidate.setElectionId(electionId);
        candidateRepository.save(candidate);
    }

    @Override
    public List<Election> getAllElections() {
        return electionRepository.findAll();
    }

    @Override
    public void deleteElection(@NonNull UUID id) {
        electionRepository.deleteById(id);
    }

    @Override
    public void updateElection(@NonNull UUID id, String name, Instant startTime, Instant endTime) {
        if (id == null) {
            throw new NullPointerException("Election ID cannot be null");
        }

        var election = electionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Election not found"));
        election.setTitle(name);
        election.setStartTime(startTime);
        election.setEndTime(endTime);
        electionRepository.save(election);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteUser(@NonNull UUID userId) {
        // Check if user exists
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User not found");
        }

        // First delete all related OTP attempts
        otpAttemptRepository.deleteByUserId(userId);

        // Now delete the user
        userRepository.deleteById(userId);
    }
}
