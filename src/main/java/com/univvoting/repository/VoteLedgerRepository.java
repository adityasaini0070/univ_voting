package com.univvoting.repository;

import com.univvoting.model.VoteLedger;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;


public interface VoteLedgerRepository extends JpaRepository<VoteLedger, UUID> {
    Optional<VoteLedger> findByElectionIdAndUserId(UUID electionId, UUID userId);
}
