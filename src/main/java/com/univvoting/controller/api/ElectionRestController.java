package com.univvoting.controller.api;

import java.util.List;
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
@RequestMapping("/api/elections")
public class ElectionRestController {

    @Autowired
    private ElectionRepository electionRepository;
    
    @Autowired
    private CandidateRepository candidateRepository;
    
    @GetMapping
    public List<Election> getAllElections() {
        return electionRepository.findAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Election> getElectionById(@PathVariable UUID id) {
        return electionRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/{id}/candidates")
    public List<Candidate> getCandidatesByElectionId(@PathVariable UUID id) {
        return candidateRepository.findByElectionId(id);
    }
}