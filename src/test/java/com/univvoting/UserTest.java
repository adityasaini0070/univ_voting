package com.univvoting;

import com.univvoting.model.User;
import com.univvoting.util.HashUtils;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for User functionality in University Voting System
 */
class UserTest {

    @Test
    void testUserCreation() {
        // Test creating a user
        User user = new User();
        user.setUniversityId("CSE2021001");
        user.setFullName("John Doe");
        user.setRole("VOTER");
        user.setPhoneNumber("9876543210");
        user.setPasswordHash("password123");
        
        // Verify user details
        assertEquals("CSE2021001", user.getUniversityId());
        assertEquals("John Doe", user.getFullName());
        assertEquals("VOTER", user.getRole());
        assertEquals("9876543210", user.getPhoneNumber());
        assertNotNull(user.getPasswordHash());
    }
    
    @Test
    void testPasswordHashing() {
        // Test password hashing utility
        String password = "mySecurePassword123";
        String hashedPassword = HashUtils.sha256Hex(password);
        
        // Verify hash properties
        assertNotNull(hashedPassword);
        assertEquals(64, hashedPassword.length()); // SHA-256 produces 64 char hex
        
        // Same password should produce same hash
        String hashedPassword2 = HashUtils.sha256Hex(password);
        assertEquals(hashedPassword, hashedPassword2);
        
        // Different passwords should produce different hashes
        String differentHash = HashUtils.sha256Hex("differentPassword");
        assertNotEquals(hashedPassword, differentHash);
    }
    
    @Test
    void testUserRoles() {
        // Test different user roles
        User voter = new User();
        voter.setRole("VOTER");
        assertEquals("VOTER", voter.getRole());
        
        User admin = new User();
        admin.setRole("ADMIN");
        assertEquals("ADMIN", admin.getRole());
    }
    
    @Test
    void testUserValidation() {
        // Test user field validation
        User user = new User();
        user.setUniversityId("CSE2021001");
        user.setFullName("Jane Smith");
        user.setRole("VOTER");
        user.setPhoneNumber("9876543210");
        
        // Verify all required fields are set
        assertNotNull(user.getUniversityId());
        assertNotNull(user.getFullName());
        assertNotNull(user.getRole());
        assertNotNull(user.getPhoneNumber());
        
        // Verify field lengths
        assertTrue(user.getUniversityId().length() > 0);
        assertTrue(user.getFullName().length() > 0);
        assertEquals(10, user.getPhoneNumber().length());
    }
}