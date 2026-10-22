package ru.itmo.infosec.auth;

import java.time.Instant;

public record AuthResponse(String accessToken, String tokenType, Instant expiresAt) {
}

