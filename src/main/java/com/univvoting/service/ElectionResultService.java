package com.univvoting.service;

import com.univvoting.model.BallotBox;
import com.univvoting.model.Candidate;
import com.univvoting.model.Election;
import com.univvoting.repository.BallotBoxRepository;
import com.univvoting.repository.CandidateRepository;
import com.univvoting.repository.ElectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ElectionResultService {

    @Autowired
    private BallotBoxRepository ballotBoxRepository;

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private ElectionRepository electionRepository;

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

        // Getters and setters
        public UUID getCandidateId() { return candidateId; }
        public void setCandidateId(UUID candidateId) { this.candidateId = candidateId; }
        
        public String getCandidateName() { return candidateName; }
        public void setCandidateName(String candidateName) { this.candidateName = candidateName; }
        
        public String getManifesto() { return manifesto; }
        public void setManifesto(String manifesto) { this.manifesto = manifesto; }
        
        public long getVoteCount() { return voteCount; }
        public void setVoteCount(long voteCount) { this.voteCount = voteCount; }
        
        public boolean isWinner() { return isWinner; }
        public void setWinner(boolean winner) { this.isWinner = winner; }
    }

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

        // Getters and setters
        public UUID getElectionId() { return electionId; }
        public void setElectionId(UUID electionId) { this.electionId = electionId; }
        
        public String getElectionTitle() { return electionTitle; }
        public void setElectionTitle(String electionTitle) { this.electionTitle = electionTitle; }
        
        public List<CandidateResult> getCandidateResults() { return candidateResults; }
        public void setCandidateResults(List<CandidateResult> candidateResults) { this.candidateResults = candidateResults; }
        
        public long getTotalVotes() { return totalVotes; }
        public void setTotalVotes(long totalVotes) { this.totalVotes = totalVotes; }
        
        public boolean isHasResults() { return hasResults; }
        public void setHasResults(boolean hasResults) { this.hasResults = hasResults; }
    }

    public ElectionResult getElectionResults(UUID electionId) {
        // Get election details
        Optional<Election> electionOpt = electionRepository.findById(electionId);
        if (electionOpt.isEmpty()) {
            throw new IllegalArgumentException("Election not found");
        }
        
        Election election = electionOpt.get();
        ElectionResult result = new ElectionResult(electionId, election.getTitle());

        // Get all candidates for this election
        List<Candidate> candidates = candidateRepository.findByElectionId(electionId);
        if (candidates.isEmpty()) {
            return result; // No candidates, no results
        }

        // Get all votes for this election
        List<BallotBox> votes = ballotBoxRepository.findByElectionId(electionId);
        
        // Count votes by candidate
        Map<UUID, Long> voteCountMap = votes.stream()
                .collect(Collectors.groupingBy(
                    BallotBox::getCandidateId,
                    Collectors.counting()
                ));

        // Create candidate results
        List<CandidateResult> candidateResults = candidates.stream()
                .map(candidate -> {
                    long voteCount = voteCountMap.getOrDefault(candidate.getId(), 0L);
                    return new CandidateResult(
                        candidate.getId(), 
                        candidate.getName(), 
                        candidate.getManifesto(), 
                        voteCount
                    );
                })
                .sorted((a, b) -> Long.compare(b.getVoteCount(), a.getVoteCount())) // Sort by vote count descending
                .collect(Collectors.toList());

        // Mark winner(s) - handle ties
        if (!candidateResults.isEmpty()) {
            long maxVotes = candidateResults.get(0).getVoteCount();
            candidateResults.stream()
                    .filter(cr -> cr.getVoteCount() == maxVotes && maxVotes > 0)
                    .forEach(cr -> cr.setWinner(true));
        }

        result.setCandidateResults(candidateResults);
        result.setTotalVotes(votes.size());
        result.setHasResults(!votes.isEmpty());

        return result;
    }

    public List<ElectionResult> getAllElectionResults() {
        List<Election> elections = electionRepository.findAll();
        return elections.stream()
                .map(election -> getElectionResults(election.getId()))
                .collect(Collectors.toList());
    }
}