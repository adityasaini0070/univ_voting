package com.univvoting.controller.api;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.univvoting.model.Candidate;
import com.univvoting.model.Election;
import com.univvoting.repository.CandidateRepository;
import com.univvoting.repository.ElectionRepository;

@RestController
@RequestMapping("/api/public")
public class PublicRestController {

    @Autowired
    private ElectionRepository electionRepository;
    
    @Autowired
    private CandidateRepository candidateRepository;

    @GetMapping("/elections")
    public ResponseEntity<?> getAllElections() {
        try {
            System.out.println("Public controller called - fetching elections");
            List<Election> elections = electionRepository.findAll();
            System.out.println("Retrieved " + elections.size() + " elections from database");
            for (Election election : elections) {
                System.out.println("Election: " + election.getId() + " - " + election.getTitle());
            }
            return ResponseEntity.ok().body(elections);
        } catch (Exception e) {
            // Log the error instead of printStackTrace
            System.err.println("Error retrieving elections in PublicRestController: " + e.getMessage());
            return ResponseEntity.status(500)
                .body(Map.of("error", "Failed to retrieve elections: " + e.getMessage()));
        }
    }
    
    @GetMapping("/elections/{id}")
    public ResponseEntity<?> getElectionById(@PathVariable UUID id) {
        try {
            return electionRepository.findById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            System.err.println("Error retrieving election: " + e.getMessage());
            return ResponseEntity.status(500)
                .body(Map.of("error", "Failed to retrieve election: " + e.getMessage()));
        }
    }
    
    @GetMapping("/elections/{id}/candidates")
    public ResponseEntity<?> getCandidatesByElectionId(@PathVariable UUID id) {
        try {
            List<Candidate> candidates = candidateRepository.findByElectionId(id);
            return ResponseEntity.ok(candidates);
        } catch (Exception e) {
            System.err.println("Error retrieving candidates: " + e.getMessage());
            return ResponseEntity.status(500)
                .body(Map.of("error", "Failed to retrieve candidates: " + e.getMessage()));
        }
    }
}