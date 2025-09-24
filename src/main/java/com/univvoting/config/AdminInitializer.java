package com.univvoting.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.univvoting.model.User;
import com.univvoting.repository.UserRepository;

@Component
public class AdminInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${admin.universityId:admin}")
    private String adminUniversityId;

    @Value("${admin.password:admin123}")
    private String adminPassword;

    @Value("${admin.fullName:System Administrator}")
    private String adminFullName;

    @Override
    public void run(String... args) {
        // Check if admin account already exists
        if (!userRepository.findByUniversityId(adminUniversityId).isPresent()) {
            // Create admin account if it doesn't exist
            User adminUser = new User();
            adminUser.setUniversityId(adminUniversityId);
            adminUser.setFullName(adminFullName);
            adminUser.setRole("ADMIN");
            adminUser.setPasswordHash(passwordEncoder.encode(adminPassword));
            userRepository.save(adminUser);
            
            System.out.println("Admin account created with ID: " + adminUniversityId);
        } else {
            System.out.println("Admin account already exists");
        }
    }
}