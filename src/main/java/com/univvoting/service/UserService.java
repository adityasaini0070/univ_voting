package com.univvoting.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.univvoting.model.User;
import com.univvoting.repository.UserRepository;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public boolean deleteUserByUniversityId(String universityId) {
        return userRepository.findByUniversityId(universityId)
                .map(u -> { userRepository.delete(u); return true; })
                .orElse(false);
    }
    
    public boolean setRoleByUniversityId(String universityId, String newRole) {
        Optional<User> userOpt = userRepository.findByUniversityId(universityId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setRole(newRole);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    public User registerUser(User user) {
        // This method expects the password to be set in the request body
        // but not mapped to a field in the User entity
        return register(
            user.getUniversityId(), 
            user.getFullName(), 
            user.getRole() == null ? "USER" : user.getRole(),
            user.getPasswordHash(), // In this case, passwordHash actually contains the plain text password from the request
            user.getPhoneNumber()
        );
    }

    public User register(String universityId, String fullName, String role, String plainPassword, String phoneNumber) {
        Optional<User> ex = userRepository.findByUniversityId(universityId);
        if (ex.isPresent()) throw new IllegalArgumentException("University ID already registered");
        User u = new User();
        u.setUniversityId(universityId);
        u.setFullName(fullName);
        u.setRole(role);
        u.setPasswordHash(passwordEncoder.encode(plainPassword));
        u.setPhoneNumber(phoneNumber);
        return userRepository.save(u);
    }
}
