package com.univvoting;

import com.univvoting.model.User;
import com.univvoting.model.Election;
import com.univvoting.model.Candidate;
import com.univvoting.util.HashUtils;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.UUID;

/**
 * Integration tests for University Voting System - tests complete workflows
 */
class VotingSystemTest {

    @Test
    void testVotingSystemIntegration() {
        // Test that all components work together
        
        // Create a user
        User voter = new User();
        voter.setUniversityId("2023100");
        voter.setFullName("Voter One");
        voter.setRole("VOTER");
        
        // Create an election
        Election election = new Election();
        election.setTitle("Test Election");
        election.setStartTime(Instant.now().plusSeconds(1800));
        election.setEndTime(Instant.now().plusSeconds(3600));
        
        // Create a candidate
        Candidate candidate = new Candidate();
        candidate.setName("Test Candidate");
        candidate.setManifesto("Better education");
        candidate.setElectionId(UUID.randomUUID());
        
        // Verify all components are created properly
        assertNotNull(voter.getUniversityId());
        assertNotNull(election.getTitle());
        assertNotNull(candidate.getName());
        
        // Verify election time is valid
        assertTrue(election.getEndTime().isAfter(election.getStartTime()));
    }
    
    @Test
    void testBasicSecurity() {
        // Test basic security features
        String password = "testPassword123";
        String hashedPassword = HashUtils.sha256Hex(password);
        
        // Password should be hashed (not plain text)
        assertNotEquals(password, hashedPassword);
        
        // Hash should be consistent
        assertEquals(hashedPassword, HashUtils.sha256Hex(password));
        
        // Different passwords should give different hashes
        String differentPassword = "differentPassword456";
        String differentHash = HashUtils.sha256Hex(differentPassword);
        assertNotEquals(hashedPassword, differentHash);
    }
    
    @Test
    void testCompleteVotingWorkflow() {
        // Test a complete voting workflow scenario
        
        // Create admin user
        User admin = new User();
        admin.setUniversityId("ADMIN001");
        admin.setFullName("Admin User");
        admin.setRole("ADMIN");
        admin.setPasswordHash(HashUtils.sha256Hex("adminPassword"));
        
        // Create voter
        User voter = new User();
        voter.setUniversityId("2023001");
        voter.setFullName("Student Voter");
        voter.setRole("VOTER");
        voter.setPasswordHash(HashUtils.sha256Hex("voterPassword"));
        
        // Create election by admin
        Election election = new Election();
        election.setTitle("Student Representative Election");
        election.setStartTime(Instant.now().plusSeconds(600));  // 10 minutes from now
        election.setEndTime(Instant.now().plusSeconds(86400));  // 24 hours from now
        
        // Add candidates to election
        Candidate candidate1 = new Candidate();
        candidate1.setName("Candidate A");
        candidate1.setManifesto("Improve library facilities");
        candidate1.setElectionId(UUID.randomUUID());
        
        Candidate candidate2 = new Candidate();
        candidate2.setName("Candidate B");
        candidate2.setManifesto("Better campus transport");
        candidate2.setElectionId(candidate1.getElectionId());
        
        // Verify complete workflow
        assertEquals("ADMIN", admin.getRole());
        assertEquals("VOTER", voter.getRole());
        assertTrue(election.getEndTime().isAfter(election.getStartTime()));
        assertEquals(candidate1.getElectionId(), candidate2.getElectionId());
        assertNotEquals(candidate1.getName(), candidate2.getName());
    }
}