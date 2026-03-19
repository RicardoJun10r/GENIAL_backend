package com.genial.demo.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.genial.demo.modules.app.model.User;
import com.genial.demo.modules.app.repositories.UserRepository;
import com.genial.demo.modules.auth.service.AuthorizationService;

@ExtendWith(MockitoExtension.class)
class AuthorizationServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthorizationService authorizationService;

    @Test
    void should_ReturnUserDetails_When_EmailExists() {
        // Given
        final User user = new User("user@genial.com", "User", "encoded");
        given(userRepository.findByEmail("user@genial.com")).willReturn(Optional.of(user));

        // When
        final UserDetails userDetails = authorizationService.loadUserByUsername("user@genial.com");

        // Then
        assertThat(userDetails.getUsername()).isEqualTo("user@genial.com");
    }

    @Test
    void should_ThrowUsernameNotFoundException_When_EmailDoesNotExist() {
        // Given
        given(userRepository.findByEmail("user@genial.com")).willReturn(Optional.empty());

        // When + Then
        assertThatThrownBy(() -> authorizationService.loadUserByUsername("user@genial.com"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("Usuario nao encontrado para o email informado.");
    }
}
