package com.agenda.plataform.security;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class InMemoryTokenBlacklistService implements TokenBlacklistService {
    private final Map<String, Instant> revoked = new ConcurrentHashMap<>();

    @Override
    public void revoke(String token, Instant expiresAt) {
        revoked.put(token, expiresAt);
    }

    @Override
    public boolean isRevoked(String token) {
        Instant exp = revoked.get(token);
        return exp != null && exp.isAfter(Instant.now());
    }

    @Scheduled(fixedDelay = 60000)
    public void cleanup() {
        Instant now = Instant.now();
        revoked.entrySet().removeIf(e -> e.getValue().isBefore(now));
    }
}
