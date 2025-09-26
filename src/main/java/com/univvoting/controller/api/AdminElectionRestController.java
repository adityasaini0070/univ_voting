package com.univvoting.controller.api;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
@RequestMapping("/api/admin/elections")
@PreAuthorize("hasRole('ADMIN')")
public class AdminElectionRestController {

    @Autowired
    private AdminService adminService;

    @GetMapping
    public ResponseEntity<?> getAllElections() {
        try {
            List<Election> elections = adminService.getAllElections();
            return ResponseEntity.ok(elections);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", "Failed to retrieve elections: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getElectionById(@PathVariable UUID id) {
        try {
            Election election = adminService.getAllElections().stream()
                .filter(e -> e.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new Exception("Election not found"));
            
            return ResponseEntity.ok(election);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> createElection(@RequestBody Map<String, Object> electionData) {
        try {
            String title = (String) electionData.get("title");
            String startTimeStr = (String) electionData.get("startTime");
            String endTimeStr = (String) electionData.get("endTime");
            
            // Parse ISO timestamp strings to Instant
            Instant startTime = Instant.parse(startTimeStr);
            Instant endTime = Instant.parse(endTimeStr);
            
            Election election = adminService.createElection(title, startTime, endTime);
            return ResponseEntity.ok(election);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateElection(@PathVariable UUID id, @RequestBody Map<String, Object> electionData) {
        try {
            String title = (String) electionData.get("title");
            String startTimeStr = (String) electionData.get("startTime");
            String endTimeStr = (String) electionData.get("endTime");
            
            // Parse ISO timestamp strings to Instant
            Instant startTime = Instant.parse(startTimeStr);
            Instant endTime = Instant.parse(endTimeStr);
            
            adminService.updateElection(id, title, startTime, endTime);
            return ResponseEntity.ok(Map.of("message", "Election updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteElection(@PathVariable UUID id) {
        try {
            adminService.deleteElection(id);
            return ResponseEntity.ok(Map.of("message", "Election deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}