package com.univvoting.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.univvoting.model.TelegramLink;


public interface TelegramLinkRepository extends JpaRepository<TelegramLink, UUID> {
    Optional<TelegramLink> findByToken(String token);
}
