package com.univvoting.repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.univvoting.model.OtpAttempt;

public interface OtpAttemptRepository extends JpaRepository<OtpAttempt, UUID> {
    Optional<OtpAttempt> findTopByUserIdOrderByCreatedAtDesc(UUID userId);
    void deleteByUserId(UUID userId);
    
    /**
     * Count OTP attempts by a user after a specific timestamp.
     * Used for rate limiting to prevent OTP spam and fraud.
     * 
     * @param userId The user's UUID
     * @param after Timestamp to count from
     * @return Number of OTP attempts
     */
    long countByUserIdAndCreatedAtAfter(UUID userId, Instant after);
}
