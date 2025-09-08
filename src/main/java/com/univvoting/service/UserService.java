package com.univvoting.service;

import com.univvoting.model.User;
import com.univvoting.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

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
