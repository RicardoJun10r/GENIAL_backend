package com.genial.demo.services;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.genial.demo.modules.app.model.User;
import com.genial.demo.modules.auth.service.TokenService;

class TokenServiceTest {

    private static final String SECRET = "very-strong-secret-key-with-at-least-32-characters";

    private final TokenService tokenService = new TokenService(SECRET, 3_600_000L);

    @Test
    void should_GenerateToken_When_UserIsValid() {
        // Given
        final User user = new User("user@genial.com", "User", "encoded");
        user.setId("user-1");

        // When
        final String token = tokenService.generateToken(user);

        // Then
        assertThat(token).isNotNull();
        assertThat(token).isNotBlank();
    }

    @Test
    void should_ValidateTokenAndReturnSubject_When_TokenIsValid() {
        // Given
        final User user = new User("user@genial.com", "User", "encoded");
        user.setId("user-1");
        final String token = tokenService.generateToken(user);

        // When
        final var subject = tokenService.validateToken(token);

        // Then
        assertThat(subject).contains("user@genial.com");
    }

    @Test
    void should_ReturnEmpty_When_TokenIsInvalid() {
        // Given
        final String invalidToken = "invalid-token";

        // When
        final var subject = tokenService.validateToken(invalidToken);

        // Then
        assertThat(subject).isEmpty();
    }
}
