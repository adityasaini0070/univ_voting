package com.univvoting.controller.api;

import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.univvoting.service.AdminService;

@RestController
@RequestMapping("/api/admin/candidates")
@PreAuthorize("hasRole('ADMIN')")
public class AdminCandidateRestController {

    @Autowired
    private AdminService adminService;

    @PostMapping
    public ResponseEntity<?> createCandidate(@RequestBody Map<String, Object> candidateData) {
        try {
            String name = (String) candidateData.get("name");
            String description = (String) candidateData.get("description");
            String electionIdStr = (String) candidateData.get("electionId");
            
            UUID electionId = UUID.fromString(electionIdStr);
            
            adminService.createCandidate(name, description, electionId);
            return ResponseEntity.ok(Map.of("message", "Candidate created successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}