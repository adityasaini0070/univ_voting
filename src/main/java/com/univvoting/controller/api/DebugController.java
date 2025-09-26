package com.univvoting.controller.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.univvoting.model.Election;
import com.univvoting.service.AdminService;

@RestController
@RequestMapping("/api/debug")
public class DebugController {
    
    @Autowired
    private jakarta.servlet.http.HttpServletRequest request;

    @Autowired
    private AdminService adminService;
    
    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("pong");
    }
    
    @GetMapping("/status")
    public ResponseEntity<?> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("status", "OK");
        status.put("timestamp", java.time.Instant.now().toString());
        status.put("server", "Running");
        
        return ResponseEntity.ok(status);
    }
    
    @GetMapping("/admin-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAdminStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("status", "OK");
        status.put("timestamp", java.time.Instant.now().toString());
        status.put("is_admin_endpoint", true);
        
        try {
            status.put("elections_count", adminService.getAllElections().size());
            status.put("admin_api_working", true);
        } catch (Exception e) {
            status.put("admin_api_working", false);
            status.put("error", e.getMessage());
        }
        
        return ResponseEntity.ok(status);
    }
    
    @GetMapping("/elections")
    public ResponseEntity<?> getAllElections() {
        try {
            System.out.println("Debug controller called - fetching elections");
            // Log authentication details
            System.out.println("Authentication: " + 
                (org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication() != null ?
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName() : "none"));
            List<Election> elections = adminService.getAllElections();
            System.out.println("Retrieved " + elections.size() + " elections from database");
            for (Election election : elections) {
                System.out.println("Election: " + election.getId() + " - " + election.getTitle());
            }
            return ResponseEntity.ok(elections);
        } catch (Exception e) {
            System.err.println("Error in debug controller: " + e.getMessage());
            return ResponseEntity.status(500)
                .body(Map.of("error", "Failed to retrieve elections: " + e.getMessage()));
        }
    }
    
    @GetMapping("/headers")
    public ResponseEntity<?> getHeaders() {
        Map<String, String> headers = new HashMap<>();
        java.util.Enumeration<String> headerNames = request.getHeaderNames();
        
        while(headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headers.put(headerName, request.getHeader(headerName));
        }
        
        return ResponseEntity.ok(headers);
    }
    
    @GetMapping("/auth")
    public ResponseEntity<?> getAuthInfo() {
        Map<String, Object> authInfo = new HashMap<>();
        
        try {
            org.springframework.security.core.Authentication auth = 
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
            
            if (auth != null) {
                authInfo.put("authenticated", auth.isAuthenticated());
                authInfo.put("principal", auth.getPrincipal().toString());
                authInfo.put("name", auth.getName());
                authInfo.put("authorities", auth.getAuthorities().toString());
                authInfo.put("details", auth.getDetails() != null ? auth.getDetails().toString() : "null");
            } else {
                authInfo.put("authenticated", false);
                authInfo.put("error", "No authentication object found");
            }
        } catch (Exception e) {
            authInfo.put("error", "Error getting auth info: " + e.getMessage());
        }
        
        return ResponseEntity.ok(authInfo);
    }
}