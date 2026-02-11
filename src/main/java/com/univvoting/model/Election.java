package com.univvoting.model;

import java.time.Instant;
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
@Table(name="elections")
public class Election {
    @Id @GeneratedValue private UUID id;
    private String title;
    private String description;
    private Instant startTime;
    private Instant endTime;
    private UUID createdBy;
    private Instant createdAt = Instant.now();
}
