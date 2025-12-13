package com.univvoting.service;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.univvoting.model.OtpAttempt;
import com.univvoting.model.User;
import com.univvoting.repository.OtpAttemptRepository;
import com.univvoting.repository.UserRepository;

/**
 * Service for managing One-Time Password (OTP) generation and verification.
 * Implements secure OTP flow with Telegram integration for vote authentication.
 * 
 * <p>Security Features:
 * <ul>
 *   <li>6-digit OTP generated using SecureRandom</li>
 *   <li>BCrypt hashing for OTP storage (never stored in plaintext)</li>
 *   <li>2-minute expiry window</li>
 *   <li>Rate limiting: 3 requests per 5 minutes</li>
 *   <li>Attempt tracking for fraud detection</li>
 * </ul>
 * 
 * @author Your Name
 * @version 1.0
 * @since 2025-12-14
 */
@Service
public class OtpService {
    private final PasswordEncoder encoder = new BCryptPasswordEncoder();
    private final SecureRandom rnd = new SecureRandom();

    @Autowired
    private OtpAttemptRepository otpAttemptRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TelegramServiceReal telegramService;

    /**
     * Generates and sends OTP to user's linked Telegram account.
     * Implements rate limiting: maximum 3 OTP requests per 5 minutes per user.
     * 
     * @param userId The user's UUID
     * @throws IllegalArgumentException if user not found
     * @throws IllegalStateException if Telegram not linked or rate limit exceeded
     */
    @Transactional
    public void generateAndSendOtp(UUID userId) {
        Optional<User> uopt = userRepository.findById(userId);
        if (uopt.isEmpty()) throw new IllegalArgumentException("User not found");
        User u = uopt.get();
        if (u.getTelegramChatId() == null) throw new IllegalStateException("Telegram not linked");

        // Rate limiting: Check recent OTP attempts (fraud prevention)
        Instant fiveMinutesAgo = Instant.now().minus(5, ChronoUnit.MINUTES);
        long recentAttempts = otpAttemptRepository.countByUserIdAndCreatedAtAfter(userId, fiveMinutesAgo);
        if (recentAttempts >= 3) {
            throw new IllegalStateException("Too many OTP requests. Please wait 5 minutes before trying again.");
        }

        int code = rnd.nextInt(1_000_000);
        String otp = String.format("%06d", code);
        String hashed = encoder.encode(otp);

        OtpAttempt attempt = new OtpAttempt();
        attempt.setUserId(userId);
        attempt.setOtpHash(hashed);
        attempt.setExpiresAt(Instant.now().plus(2, ChronoUnit.MINUTES));
        otpAttemptRepository.save(attempt);

        telegramService.sendOtp(u.getTelegramChatId(), otp);
    }

    /**
     * Verifies the OTP provided by the user.
     * Implements attempt tracking for security monitoring.
     * 
     * @param userId The user's UUID
     * @param providedOtp The OTP code entered by user
     * @return true if OTP is valid and not expired, false otherwise
     */
    @Transactional
    public boolean verifyOtp(UUID userId, String providedOtp) {
        Optional<OtpAttempt> maybe = otpAttemptRepository.findTopByUserIdOrderByCreatedAtDesc(userId);
        if (maybe.isEmpty()) return false;
        OtpAttempt a = maybe.get();
        if (a.getVerifiedAt() != null) return false;
        if (a.getExpiresAt().isBefore(Instant.now())) return false;
        if (!encoder.matches(providedOtp, a.getOtpHash())) {
            a.setAttempts(a.getAttempts() + 1);
            otpAttemptRepository.save(a);
            return false;
        }
        a.setVerifiedAt(Instant.now());
        otpAttemptRepository.save(a);
        return true;
    }
}
