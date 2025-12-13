package com.univvoting.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.univvoting.model.BallotBox;
import com.univvoting.model.VoteLedger;
import com.univvoting.repository.BallotBoxRepository;
import com.univvoting.repository.VoteLedgerRepository;

/**
 * Service responsible for managing the voting process.
 * Implements the decoupled ballot architecture to ensure vote anonymity
 * while maintaining one-person-one-vote integrity.
 * 
 * <p>Key Features:
 * <ul>
 *   <li>Vote ledger tracks WHO voted (identity)</li>
 *   <li>Ballot box tracks WHAT was voted (choice)</li>
 *   <li>No linkage between identity and choice</li>
 * </ul>
 * 
 * @author Your Name
 * @version 1.0
 * @since 2025-12-14
 */
@Service
public class VoteService {
    @Autowired
    private VoteLedgerRepository voteLedgerRepository;

    @Autowired
    private BallotBoxRepository ballotBoxRepository;

    /**
     * Casts a vote in an election while maintaining ballot anonymity.
     * 
     * <p>This method performs two decoupled operations:
     * <ol>
     *   <li>Records in vote_ledger that user voted (with OTP verification)</li>
     *   <li>Records in ballot_box what was voted (anonymous ballot)</li>
     * </ol>
     * 
     * <p>Security features:
     * <ul>
     *   <li>Enforces one-person-one-vote via unique constraint</li>
     *   <li>No linkage between user identity and candidate choice</li>
     *   <li>Transaction ensures both records or neither</li>
     * </ul>
     * 
     * @param electionId UUID of the election
     * @param userId UUID of the voter
     * @param candidateId UUID of the chosen candidate
     * @throws NullPointerException if any parameter is null
     * @throws IllegalStateException if user already voted in this election
     */
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
        
        // enforce one-person-one-vote at DB level (unique constraint) and check beforehand
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
