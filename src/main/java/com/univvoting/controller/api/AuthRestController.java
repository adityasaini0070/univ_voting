package com.univvoting.controller.api;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.univvoting.model.User;
import com.univvoting.repository.UserRepository;
import com.univvoting.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/auth") // Changed from "/api/user" to avoid conflicts
public class AuthRestController {

    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private UserRepository userRepository;
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials, HttpSession session) {
        try {
            String universityId = credentials.get("universityId");
            String password = credentials.get("password");
            
            System.out.println("API Login attempt for user: " + universityId);
            
            // Authenticate the user
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(universityId, password)
            );
            
            // Set the authentication in the security context
            SecurityContextHolder.getContext().setAuthentication(authentication);
            System.out.println("Authentication successful for user: " + universityId);
            System.out.println("Session ID: " + session.getId());
            
            // Get the user details from the userRepository
            Optional<User> userOpt = userRepository.findByUniversityId(universityId);
            
            if (!userOpt.isPresent()) {
                return ResponseEntity.badRequest().body(Map.of("error", "User not found"));
            }
            
            User user = userOpt.get();
            
            Map<String, Object> response = new HashMap<>();
            response.put("universityId", user.getUniversityId());
            response.put("fullName", user.getFullName());
            response.put("roles", authentication.getAuthorities());
            
            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            System.out.println("Authentication failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid credentials"));
        }
    }
    
    @GetMapping("/current")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated() && 
            !authentication.getName().equals("anonymousUser")) {
            System.out.println("Getting current user: " + authentication.getName());
            try {
                Optional<User> userOpt = userRepository.findByUniversityId(authentication.getName());
                
                if (!userOpt.isPresent()) {
                    return ResponseEntity.status(404).body(Map.of("error", "User not found"));
                }
                
                User user = userOpt.get();
                
                Map<String, Object> response = new HashMap<>();
                response.put("universityId", user.getUniversityId());
                response.put("fullName", user.getFullName());
                response.put("roles", authentication.getAuthorities());
                
                return ResponseEntity.ok(response);
            } catch (Exception e) {
                System.out.println("Error getting current user: " + e.getMessage());
                return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
            }
        }
        
        System.out.println("No authenticated user found");
        return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
    }
    
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        try {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            SecurityContextHolder.clearContext();
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            System.out.println("Error during logout: " + e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}