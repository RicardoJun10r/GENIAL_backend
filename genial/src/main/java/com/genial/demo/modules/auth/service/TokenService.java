package com.genial.demo.modules.auth.service;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.genial.demo.modules.app.model.User;

@Service
public class TokenService {

    private static final String ISSUER = "genial-api";
    private static final String USER_ID_CLAIM = "userId";

    private final String secret;
    private final long expirationInMillis;

    public TokenService(
            @Value("${api.security.token.secret}") final String secret,
            @Value("${api.security.token.expiration}") final long expirationInMillis) {
        this.secret = secret;
        this.expirationInMillis = expirationInMillis;
    }

    public String generateToken(final User user) {
        final Instant now = Instant.now();
        return JWT.create()
                .withIssuer(ISSUER)
                .withSubject(user.getEmail())
                .withClaim(USER_ID_CLAIM, user.getId())
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(now.plusMillis(expirationInMillis)))
                .sign(Algorithm.HMAC256(secret));
    }

    public Optional<String> validateToken(final String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }

        try {
            final String subject = JWT.require(Algorithm.HMAC256(secret))
                    .withIssuer(ISSUER)
                    .build()
                    .verify(token)
                    .getSubject();
            return Optional.ofNullable(subject);
        } catch (JWTVerificationException exception) {
            return Optional.empty();
        }
    }
}
