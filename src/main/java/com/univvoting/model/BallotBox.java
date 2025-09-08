package com.univvoting.model;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="ballot_box")
public class BallotBox {
    @Id @GeneratedValue private UUID id;
    private UUID electionId;
    private UUID candidateId;
    private UUID ballotUuid;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getElectionId() {
        return electionId;
    }

    public void setElectionId(UUID electionId) {
        this.electionId = electionId;
    }

    public UUID getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(UUID candidateId) {
        this.candidateId = candidateId;
    }

    public UUID getBallotUuid() {
        return ballotUuid;
    }

    public void setBallotUuid(UUID ballotUuid) {
        this.ballotUuid = ballotUuid;
    }
}
