package com.univvoting.controller.api;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.univvoting.model.Election;
import com.univvoting.repository.ElectionRepository;

@RestController
@RequestMapping("/api/public/election-list")
@CrossOrigin(origins = {"http://localhost:5173"}, allowCredentials = "true")
public class ElectionListController {

    @Autowired
    private ElectionRepository electionRepository;

    @GetMapping
    public ResponseEntity<?> getAllElections() {
        try {
            System.out.println("ElectionListController - fetching all elections");
            List<Election> elections = electionRepository.findAll();
            System.out.println("Retrieved " + elections.size() + " elections from database");
            return ResponseEntity.ok().body(elections);
        } catch (Exception e) {
            // Log the error instead of printing the stack trace
            System.err.println("Error retrieving elections in ElectionListController: " + e.getMessage());
            return ResponseEntity.status(500)
                .body(Map.of("error", "Failed to retrieve elections: " + e.getMessage()));
        }
    }
}