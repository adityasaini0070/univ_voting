package com.univvoting.repository;

import com.univvoting.model.BallotBox;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;


public interface BallotBoxRepository extends JpaRepository<BallotBox, UUID> {
    List<BallotBox> findByElectionId(UUID electionId);
}
