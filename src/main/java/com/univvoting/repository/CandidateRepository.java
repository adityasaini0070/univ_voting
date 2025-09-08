package com.univvoting.repository;

import com.univvoting.model.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface CandidateRepository extends JpaRepository<Candidate, UUID> {
    List<Candidate> findByElectionId(UUID electionId);
}
