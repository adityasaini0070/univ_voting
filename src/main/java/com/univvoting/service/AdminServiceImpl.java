// ...existing code...
package com.univvoting.service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.univvoting.model.Candidate;
import com.univvoting.model.Election;
import com.univvoting.repository.CandidateRepository;
import com.univvoting.repository.ElectionRepository;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private ElectionRepository electionRepository;

    @Autowired
    private CandidateRepository candidateRepository;

    @Override
    public Election createElection(String name, Instant startTime, Instant endTime) {
        Election election = new Election();
        election.setTitle(name);
        election.setStartTime(startTime);
        election.setEndTime(endTime);
        return electionRepository.save(election);
    }

    @Override
    public void createCandidate(String name, String description, UUID electionId) {
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
    public void deleteElection(UUID id) {
        electionRepository.deleteById(id);
    }

    @Override
    public void updateElection(UUID id, String name, Instant startTime, Instant endTime) {
        var election = electionRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Election not found"));
        election.setTitle(name);
        election.setStartTime(startTime);
        election.setEndTime(endTime);
        electionRepository.save(election);
    }
}
