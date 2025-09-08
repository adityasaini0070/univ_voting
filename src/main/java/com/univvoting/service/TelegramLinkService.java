package com.univvoting.service;

import com.univvoting.model.TelegramLink;
import com.univvoting.repository.TelegramLinkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

@Service
public class TelegramLinkService {
    @Autowired
    private TelegramLinkRepository repo;

    private final SecureRandom rnd = new SecureRandom();

    public TelegramLink createTokenForUser(UUID userId) {
        byte[] b = new byte[24];
        rnd.nextBytes(b);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(b);
        TelegramLink t = new TelegramLink();
        t.setUserId(userId);
        t.setToken(token);
        return repo.save(t);
    }

    public Optional<TelegramLink> findByToken(String token) {
        return repo.findByToken(token);
    }

    public void delete(TelegramLink t) { repo.delete(t); }
}
