package com.univvoting;

import com.univvoting.model.Election;
import com.univvoting.model.Candidate;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.UUID;

/**
 * Tests for Election functionality in University Voting System
 */
class ElectionTest {

    @Test
    void testElectionCreation() {
        // Test creating an election
        Election election = new Election();
        election.setTitle("Student Council Election 2024");
        election.setStartTime(Instant.now().plusSeconds(3600)); // starts in 1 hour
        election.setEndTime(Instant.now().plusSeconds(7200));   // ends in 2 hours
        
        // Verify election details
        assertEquals("Student Council Election 2024", election.getTitle());
        assertNotNull(election.getStartTime());
        assertNotNull(election.getEndTime());
        assertTrue(election.getEndTime().isAfter(election.getStartTime()));
    }
    
    @Test
    void testCandidateCreation() {
        // Test creating a candidate
        Candidate candidate = new Candidate();
        candidate.setName("Alice Smith");
        candidate.setManifesto("I will improve campus WiFi and extend library hours");
        candidate.setElectionId(UUID.randomUUID());
        
        // Verify candidate details
        assertEquals("Alice Smith", candidate.getName());
        assertEquals("I will improve campus WiFi and extend library hours", candidate.getManifesto());
        assertNotNull(candidate.getElectionId());
    }
    
    @Test
    void testElectionTimeValidation() {
        // Test that election has valid time range
        Election election = new Election();
        Instant now = Instant.now();
        election.setStartTime(now.plusSeconds(1800)); // 30 minutes from now
        election.setEndTime(now.plusSeconds(3600));   // 1 hour from now
        
        // Verify time logic
        assertTrue(election.getStartTime().isAfter(now));
        assertTrue(election.getEndTime().isAfter(election.getStartTime()));
    }
    
    @Test
    void testCandidateValidation() {
        // Test candidate field validation
        Candidate candidate = new Candidate();
        candidate.setName("Bob Johnson");
        candidate.setManifesto("Better food options in canteen and more sports facilities");
        candidate.setElectionId(UUID.randomUUID());
        
        // Verify all required fields are set
        assertNotNull(candidate.getName());
        assertNotNull(candidate.getManifesto());
        assertNotNull(candidate.getElectionId());
        
        // Verify field content
        assertTrue(candidate.getName().length() > 0);
        assertTrue(candidate.getManifesto().length() > 0);
    }
    
    @Test
    void testElectionScheduling() {
        // Test election scheduling scenarios
        Election election = new Election();
        election.setTitle("Class Representative Election");
        
        Instant now = Instant.now();
        Instant futureStart = now.plusSeconds(7200);  // 2 hours from now
        Instant futureEnd = now.plusSeconds(14400);   // 4 hours from now
        
        election.setStartTime(futureStart);
        election.setEndTime(futureEnd);
        
        // Verify scheduling
        assertTrue(election.getStartTime().isAfter(now));
        assertTrue(election.getEndTime().isAfter(election.getStartTime()));
        
        // Verify election duration (should be 2 hours = 7200 seconds)
        long durationSeconds = election.getEndTime().getEpochSecond() - election.getStartTime().getEpochSecond();
        assertEquals(7200, durationSeconds);
    }
}