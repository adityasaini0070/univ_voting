package com.univvoting.service;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.univvoting.model.OtpAttempt;
import com.univvoting.model.User;
import com.univvoting.repository.OtpAttemptRepository;
import com.univvoting.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class OtpService {
    private final PasswordEncoder encoder = new BCryptPasswordEncoder();
    private final SecureRandom rnd = new SecureRandom();

    private final OtpAttemptRepository otpAttemptRepository;
    private final UserRepository userRepository;
    private final TelegramServiceReal telegramService;

    @Transactional
    public void generateAndSendOtp(UUID userId) {
        Optional<User> uotp = userRepository.findById(userId);
        if (uotp.isEmpty())
            throw new IllegalArgumentException("User not found");
        User u = uotp.get();
        if (u.getTelegramChatId() == null)
            throw new IllegalStateException("Telegram not linked");

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

    @Transactional
    public boolean verifyOtp(UUID userId, String providedOtp) {
        Optional<OtpAttempt> maybe = otpAttemptRepository.findTopByUserIdOrderByCreatedAtDesc(userId);
        if (maybe.isEmpty())
            return false;
        OtpAttempt a = maybe.get();
        if (a.getVerifiedAt() != null)
            return false;
        if (a.getExpiresAt().isBefore(Instant.now()))
            return false;
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
