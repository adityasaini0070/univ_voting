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
        // Remove all admins except the configured one
        userRepository.findAll().stream()
            .filter(u -> "ADMIN".equalsIgnoreCase(u.getRole()) && !adminUniversityId.equals(u.getUniversityId()))
            .forEach(u -> userRepository.delete(u));

        // Check if configured admin exists
        User adminUser = userRepository.findByUniversityId(adminUniversityId)
            .filter(u -> "ADMIN".equalsIgnoreCase(u.getRole()))
            .orElse(null);

        if (adminUser == null) {
            // Create admin account if it doesn't exist
            adminUser = new User();
            adminUser.setUniversityId(adminUniversityId);
            adminUser.setFullName(adminFullName);
            adminUser.setRole("ADMIN");
            adminUser.setPasswordHash(passwordEncoder.encode(adminPassword));
            userRepository.save(adminUser);
            System.out.println("Admin account created with ID: " + adminUniversityId);
        } else {
            // Update admin details if needed
            boolean updated = false;
            if (!adminUser.getFullName().equals(adminFullName)) {
                adminUser.setFullName(adminFullName);
                updated = true;
            }
            if (!passwordEncoder.matches(adminPassword, adminUser.getPasswordHash())) {
                adminUser.setPasswordHash(passwordEncoder.encode(adminPassword));
                updated = true;
            }
            if (updated) {
                userRepository.save(adminUser);
                System.out.println("Admin account updated with latest properties.");
            } else {
                System.out.println("Admin account already exists and is up to date.");
            }
        }
    }
}