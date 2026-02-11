package com.univvoting.model;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="vote_ledger", uniqueConstraints=@UniqueConstraint(columnNames={"electionId","userId"}))
public class VoteLedger {
    @Id @GeneratedValue private UUID id;
    private UUID electionId;
    private UUID userId;
    private boolean otpVerified;
    private Instant castAt;
}
