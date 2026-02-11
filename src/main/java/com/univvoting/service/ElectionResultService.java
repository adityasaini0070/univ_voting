package com.univvoting.service;

import com.univvoting.model.BallotBox;
import com.univvoting.model.Candidate;
import com.univvoting.model.Election;
import com.univvoting.repository.BallotBoxRepository;
import com.univvoting.repository.CandidateRepository;
import com.univvoting.repository.ElectionRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ElectionResultService {

    private final BallotBoxRepository ballotBoxRepository;
    private final CandidateRepository candidateRepository;
    private final ElectionRepository electionRepository;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CandidateResult {
        private UUID candidateId;
        private String candidateName;
        private String manifesto;
        private long voteCount;
        private boolean isWinner;

        public CandidateResult(UUID candidateId, String candidateName, String manifesto, long voteCount) {
            this.candidateId = candidateId;
            this.candidateName = candidateName;
            this.manifesto = manifesto;
            this.voteCount = voteCount;
            this.isWinner = false;
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ElectionResult {
        private UUID electionId;
        private String electionTitle;
        private List<CandidateResult> candidateResults;
        private long totalVotes;
        private boolean hasResults;

        public ElectionResult(UUID electionId, String electionTitle) {
            this.electionId = electionId;
            this.electionTitle = electionTitle;
            this.candidateResults = new ArrayList<>();
            this.totalVotes = 0;
            this.hasResults = false;
        }
    }

    // ---------------- SERVICE METHODS ----------------

    public ElectionResult getElectionResults(@NonNull UUID electionId) {

        Election election = electionRepository.findById(electionId)
                .orElseThrow(() -> new IllegalArgumentException("Election not found"));

        ElectionResult result = new ElectionResult(electionId, election.getTitle());

        List<Candidate> candidates = candidateRepository.findByElectionId(electionId);
        if (candidates.isEmpty()) {
            return result;
        }

        List<BallotBox> votes = ballotBoxRepository.findByElectionId(electionId);

        Map<UUID, Long> voteCountMap = votes.stream()
                .collect(Collectors.groupingBy(
                        BallotBox::getCandidateId,
                        Collectors.counting()));

        List<CandidateResult> candidateResults = candidates.stream()
                .map(candidate -> {
                    long voteCount = voteCountMap.getOrDefault(candidate.getId(), 0L);
                    return new CandidateResult(
                            candidate.getId(),
                            candidate.getName(),
                            candidate.getManifesto(),
                            voteCount);
                })
                .sorted((a, b) -> Long.compare(b.getVoteCount(), a.getVoteCount()))
                .collect(Collectors.toList());

        // Mark winners (tie-safe)
        if (!candidateResults.isEmpty()) {
            long maxVotes = candidateResults.getFirst().getVoteCount();

            candidateResults.stream()
                    .filter(cr -> cr.getVoteCount() == maxVotes && maxVotes > 0)
                    .forEach(cr -> cr.setWinner(true));
        }

        result.setCandidateResults(candidateResults);
        result.setTotalVotes(votes.size());
        result.setHasResults(!votes.isEmpty());

        return result;
    }

    @SuppressWarnings("null")
    public List<ElectionResult> getAllElectionResults() {
        return electionRepository.findAll()
                .stream()
                .map(election -> getElectionResults(election.getId()))
                .collect(Collectors.toList());
    }
}
