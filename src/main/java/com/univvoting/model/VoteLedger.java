package com.univvoting.model;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;


@Entity
@Table(name="vote_ledger", uniqueConstraints=@UniqueConstraint(columnNames={"electionId","userId"}))
public class VoteLedger {
    @Id @GeneratedValue private UUID id;
    private UUID electionId;
    private UUID userId;
    private boolean otpVerified;
    private Instant castAt;

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

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public boolean isOtpVerified() {
        return otpVerified;
    }

    public void setOtpVerified(boolean otpVerified) {
        this.otpVerified = otpVerified;
    }

    public Instant getCastAt() {
        return castAt;
    }

    public void setCastAt(Instant castAt) {
        this.castAt = castAt;
    }
}
