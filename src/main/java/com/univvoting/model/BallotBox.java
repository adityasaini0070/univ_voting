package com.univvoting.model;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="ballot_box")
public class BallotBox {
    @Id @GeneratedValue private UUID id;
    private UUID electionId;
    private UUID candidateId;
    private UUID ballotUuid;
}
