package com.univvoting.service;

import com.univvoting.model.TelegramLink;
import com.univvoting.repository.TelegramLinkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TelegramLinkService {
    private final TelegramLinkRepository repo;

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

    public void delete(TelegramLink t) {
        repo.delete(t);
    }
}
