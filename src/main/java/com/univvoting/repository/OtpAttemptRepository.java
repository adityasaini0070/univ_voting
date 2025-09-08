package com.univvoting.repository;

import com.univvoting.model.OtpAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface OtpAttemptRepository extends JpaRepository<OtpAttempt, UUID> {
    Optional<OtpAttempt> findTopByUserIdOrderByCreatedAtDesc(UUID userId);
}
