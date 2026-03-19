package com.genial.demo.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.genial.demo.exceptions.BusinessException;
import com.genial.demo.exceptions.UnauthorizedAccessException;
import com.genial.demo.modules.app.model.User;
import com.genial.demo.modules.app.repositories.UserRepository;
import com.genial.demo.modules.app.services.UserService;
import com.genial.demo.modules.auth.service.TokenService;
import com.genial.demo.shared.CreateUserRequest;
import com.genial.demo.shared.JwtResponseDto;
import com.genial.demo.shared.UserDto;
import com.genial.demo.shared.UserLoginRequest;
import com.genial.demo.shared.UserUpdateData;
import com.genial.demo.shared.mapper.UserMapper;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private UserService userService;

    @Test
    void should_ReturnJwtResponse_When_LoginIsValid() {
        // Given
        final UserLoginRequest request = new UserLoginRequest(" user@genial.com ", "plain-password");
        final User authenticatedUser = new User("user@genial.com", "User", "encoded");
        authenticatedUser.setId("user-1");
        final Authentication authentication = new UsernamePasswordAuthenticationToken(authenticatedUser, null,
                Collections.emptyList());

        given(authenticationManager.authenticate(any(Authentication.class))).willReturn(authentication);
        given(tokenService.generateToken(authenticatedUser)).willReturn("jwt-token");

        // When
        final JwtResponseDto response = userService.login(request);

        // Then
        assertThat(response.token()).isEqualTo("jwt-token");

        final ArgumentCaptor<UsernamePasswordAuthenticationToken> captor = ArgumentCaptor
                .forClass(UsernamePasswordAuthenticationToken.class);
        then(authenticationManager).should().authenticate(captor.capture());
        assertThat(captor.getValue().getPrincipal()).isEqualTo("user@genial.com");
        assertThat(captor.getValue().getCredentials()).isEqualTo("plain-password");
    }

    @Test
    void should_ThrowUnauthorizedAccessException_When_LoginIsInvalid() {
        // Given
        final UserLoginRequest request = new UserLoginRequest("user@genial.com", "wrong-password");
        given(authenticationManager.authenticate(any(Authentication.class)))
                .willThrow(new BadCredentialsException("invalid"));

        // When + Then
        assertThatThrownBy(() -> userService.login(request))
                .isInstanceOf(UnauthorizedAccessException.class)
                .hasMessage("Credenciais invalidas.");
    }

    @Test
    void should_SaveUser_When_EmailIsUnique() {
        // Given
        final CreateUserRequest request = new CreateUserRequest("user@genial.com", "User", "plain-password");
        final User mappedUser = new User();
        final User savedUser = new User("user@genial.com", "User", "encoded-password");
        savedUser.setId("user-1");
        final UserDto expectedResponse = new UserDto("user-1", "user@genial.com", "User", Collections.emptyList());

        given(userRepository.findByEmail(request.email())).willReturn(Optional.empty());
        given(userMapper.toEntity(request)).willReturn(mappedUser);
        given(passwordEncoder.encode(request.password())).willReturn("encoded-password");
        given(userRepository.save(mappedUser)).willReturn(savedUser);
        given(userMapper.toDto(savedUser)).willReturn(expectedResponse);

        // When
        final UserDto response = userService.saveUser(request);

        // Then
        assertThat(response.getId()).isEqualTo("user-1");
        assertThat(response.getEmail()).isEqualTo("user@genial.com");

        then(userRepository).should().findByEmail("user@genial.com");
        then(passwordEncoder).should().encode("plain-password");
        then(userRepository).should().save(mappedUser);
        assertThat(mappedUser.getPassword()).isEqualTo("encoded-password");
    }

    @Test
    void should_ThrowBusinessException_When_EmailAlreadyExistsOnSave() {
        // Given
        final CreateUserRequest request = new CreateUserRequest("user@genial.com", "User", "plain-password");
        final User existingUser = new User("user@genial.com", "Existing", "encoded-password");
        given(userRepository.findByEmail(request.email())).willReturn(Optional.of(existingUser));

        // When + Then
        assertThatThrownBy(() -> userService.saveUser(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Ja existe usuario cadastrado com este email.");
    }

    @SuppressWarnings("null")
    @Test
    void should_UpdateUser_When_DataIsValid() {
        // Given
        final String userId = "user-1";
        final User existingUser = new User("old@genial.com", "Old Name", "old-encoded");
        existingUser.setId(userId);

        final UserUpdateData request = new UserUpdateData("new@genial.com", "New Name", "new-plain-password");

        given(userRepository.findById(userId)).willReturn(Optional.of(existingUser));
        given(userRepository.findByEmail("new@genial.com")).willReturn(Optional.empty());
        given(passwordEncoder.encode("new-plain-password")).willReturn("new-encoded-password");
        given(userRepository.save(any(User.class))).willAnswer(invocation -> invocation.getArgument(0));
        given(userMapper.toDto(any(User.class))).willAnswer(invocation -> {
            final User saved = invocation.getArgument(0);
            return new UserDto(saved.getId(), saved.getEmail(), saved.getName(), Collections.emptyList());
        });

        // When
        final UserDto response = userService.updateById(userId, request);

        // Then
        assertThat(response.getId()).isEqualTo("user-1");
        assertThat(response.getEmail()).isEqualTo("new@genial.com");
        assertThat(response.getName()).isEqualTo("New Name");

        final ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        then(userRepository).should().save(captor.capture());
        assertThat(captor.getValue().getEmail()).isEqualTo("new@genial.com");
        assertThat(captor.getValue().getName()).isEqualTo("New Name");
        assertThat(captor.getValue().getPassword()).isEqualTo("new-encoded-password");
    }

    @Test
    void should_ThrowBusinessException_When_LoginRequestIsNull() {
        // Given + When + Then
        assertThatThrownBy(() -> userService.login(null))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Dados de login nao informados.");
    }
}
