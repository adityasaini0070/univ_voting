package com.univvoting.model;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
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
@Table(name="users")
public class User {
    @Id @GeneratedValue private UUID id;
    @Column(unique=true,nullable=false) private String universityId;
    private String fullName;
    private String role;
    private String phoneNumber;
    private Long telegramChatId;
    private String passwordHash;
    private Instant createdAt = Instant.now();
}
