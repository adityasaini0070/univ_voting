package com.univvoting.config;

import com.univvoting.model.User;
import com.univvoting.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Check if database is empty
        long userCount = userRepository.count();
        
        if (userCount == 0) {
            System.out.println("Database is empty. Initializing with default admin user...");
            
            // Create default admin user
            User adminUser = new User();
            adminUser.setUniversityId("admin");
            adminUser.setFullName("System Administrator");
            adminUser.setRole("ADMIN");
            adminUser.setPasswordHash(passwordEncoder.encode("admin"));
            adminUser.setCreatedAt(Instant.now());
            
            userRepository.save(adminUser);
            System.out.println("Default admin user created successfully!");
            System.out.println("Username: admin");
            System.out.println("Password: admin");
            System.out.println("Please change the password after first login.");
            
            // Create some sample voter users for testing
            createSampleUser("CSE2021001", "Alice Johnson", "VOTER", "9876543210");
            createSampleUser("CSE2021002", "Bob Smith", "VOTER", "9876543211");
            createSampleUser("CSE2021003", "Charlie Brown", "VOTER", "9876543212");
            createSampleUser("CSE2021004", "Diana Prince", "VOTER", "9876543213");
            createSampleUser("CSE2021005", "Eve Wilson", "VOTER", "9876543214");
            
            System.out.println("Sample voter users created (password: 'password123' for all)");
        } else {
            System.out.println("Database already initialized with " + userCount + " users.");
        }
    }
    
    private void createSampleUser(String universityId, String fullName, String role, String phoneNumber) {
        User user = new User();
        user.setUniversityId(universityId);
        user.setFullName(fullName);
        user.setRole(role);
        user.setPhoneNumber(phoneNumber);
        user.setPasswordHash(passwordEncoder.encode("password123"));
        user.setCreatedAt(Instant.now());
        userRepository.save(user);
    }
}