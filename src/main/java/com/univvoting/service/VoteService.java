package com.univvoting.service;

import java.time.Instant;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.univvoting.model.BallotBox;
import com.univvoting.model.VoteLedger;
import com.univvoting.repository.BallotBoxRepository;
import com.univvoting.repository.VoteLedgerRepository;

@Service
@RequiredArgsConstructor
public class VoteService {
    private final VoteLedgerRepository voteLedgerRepository;
    private final BallotBoxRepository ballotBoxRepository;

    @Transactional
    public void castVote(UUID electionId, UUID userId, UUID candidateId) {
        if (electionId == null) {
            throw new NullPointerException("Election ID cannot be null");
        }
        if (userId == null) {
            throw new NullPointerException("User ID cannot be null");
        }
        if (candidateId == null) {
            throw new NullPointerException("Candidate ID cannot be null");
        }

        if (voteLedgerRepository.findByElectionIdAndUserId(electionId, userId).isPresent()) {
            throw new IllegalStateException("User already voted in this election");
        }

        VoteLedger ledger = new VoteLedger();
        ledger.setElectionId(electionId);
        ledger.setUserId(userId);
        ledger.setOtpVerified(true);
        ledger.setCastAt(Instant.now());

        voteLedgerRepository.save(ledger);

        BallotBox b = new BallotBox();
        b.setElectionId(electionId);
        b.setCandidateId(candidateId);
        b.setBallotUuid(UUID.randomUUID());

        ballotBoxRepository.save(b);
    }

    public boolean hasUserVoted(UUID electionId, UUID userId) {
        return voteLedgerRepository.findByElectionIdAndUserId(electionId, userId).isPresent();
    }
}
