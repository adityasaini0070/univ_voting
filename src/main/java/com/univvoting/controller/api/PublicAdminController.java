package com.univvoting.controller.api;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.univvoting.model.Election;
import com.univvoting.service.AdminService;

@RestController
@RequestMapping("/api/public/admin")
public class PublicAdminController {

    @Autowired
    private AdminService adminService;

    // This is only for testing purposes - in a production environment, these endpoints would require authentication
    
    @GetMapping("/elections")
    public ResponseEntity<?> getAllElections() {
        try {
            System.out.println("Public admin controller called - fetching elections");
            List<Election> elections = adminService.getAllElections();
            System.out.println("Retrieved " + elections.size() + " elections from database");
            for (Election election : elections) {
                System.out.println("Election: " + election.getId() + " - " + election.getTitle());
            }
            return ResponseEntity.ok(elections);
        } catch (Exception e) {
            System.err.println("Error retrieving elections: " + e.getMessage());
            return ResponseEntity.status(500)
                .body(Map.of("error", "Failed to retrieve elections: " + e.getMessage()));
        }
    }
    
    @PostMapping("/elections")
    public ResponseEntity<?> createElection(@RequestBody Map<String, Object> electionData) {
        try {
            System.out.println("Public admin controller called - creating election");
            String title = (String) electionData.get("title");
            String startTimeStr = (String) electionData.get("startTime");
            String endTimeStr = (String) electionData.get("endTime");
            
            // Parse ISO timestamp strings to Instant
            Instant startTime = Instant.parse(startTimeStr);
            Instant endTime = Instant.parse(endTimeStr);
            
            Election election = adminService.createElection(title, startTime, endTime);
            return ResponseEntity.ok(election);
        } catch (Exception e) {
            System.err.println("Error creating election: " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @DeleteMapping("/elections/{id}")
    public ResponseEntity<?> deleteElection(@PathVariable UUID id) {
        try {
            System.out.println("Public admin controller called - deleting election " + id);
            adminService.deleteElection(id);
            return ResponseEntity.ok(Map.of("message", "Election deleted successfully"));
        } catch (Exception e) {
            System.err.println("Error deleting election: " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PutMapping("/elections/{id}")
    public ResponseEntity<?> updateElection(@PathVariable UUID id, @RequestBody Map<String, Object> electionData) {
        try {
            System.out.println("Public admin controller called - updating election " + id);
            String title = (String) electionData.get("title");
            String startTimeStr = (String) electionData.get("startTime");
            String endTimeStr = (String) electionData.get("endTime");
            
            // Parse ISO timestamp strings to Instant
            Instant startTime = Instant.parse(startTimeStr);
            Instant endTime = Instant.parse(endTimeStr);
            
            adminService.updateElection(id, title, startTime, endTime);
            return ResponseEntity.ok(Map.of("message", "Election updated successfully"));
        } catch (Exception e) {
            System.err.println("Error updating election: " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}