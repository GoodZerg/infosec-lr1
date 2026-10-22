package ru.itmo.infosec.auth;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Service;

@Service
public class TokenService {
    private final JwtEncoder jwtEncoder;
    private final Duration ttl;

    public TokenService(JwtEncoder jwtEncoder, @Value("${security.jwt.ttl}") Duration ttl) {
        this.jwtEncoder = jwtEncoder;
        this.ttl = ttl;
    }

    public AuthResponse issue(String username) {
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(ttl);
        JwtClaimsSet claims = JwtClaimsSet.builder()
            .issuer("infobez-lr1")
            .issuedAt(issuedAt)
            .expiresAt(expiresAt)
            .subject(username)
            .id(UUID.randomUUID().toString())
            .claim("scope", "api")
            .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        String token = jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
        return new AuthResponse(token, "Bearer", expiresAt);
    }
}

