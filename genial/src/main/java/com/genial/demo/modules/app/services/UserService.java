package com.genial.demo.modules.app.services;

import java.util.function.Consumer;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.genial.demo.exceptions.BusinessException;
import com.genial.demo.exceptions.ResourceNotFoundException;
import com.genial.demo.exceptions.UnauthorizedAccessException;
import com.genial.demo.modules.app.model.User;
import com.genial.demo.modules.app.repositories.UserRepository;
import com.genial.demo.modules.auth.service.TokenService;
import com.genial.demo.shared.CreateUserRequest;
import com.genial.demo.shared.JwtResponseDto;
import com.genial.demo.shared.UserDto;
import com.genial.demo.shared.UserLoginRequest;
import com.genial.demo.shared.UserUpdateData;
import com.genial.demo.shared.mapper.UserMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper mapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    @Transactional(readOnly = true)
    public JwtResponseDto login(final UserLoginRequest dto) {
        if (dto == null) {
            throw new BusinessException("Dados de login nao informados.");
        }

        validateRequired(dto.email(), "Email deve ser informado para login.");
        validateRequired(dto.password(), "Senha deve ser informada para login.");

        final String email = dto.email().trim();
        final String password = dto.password();
        try {
            final Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password));
            final Object principal = authentication.getPrincipal();
            if (!(principal instanceof User authenticatedUser)) {
                throw new UnauthorizedAccessException("Credenciais invalidas.");
            }

            final String token = tokenService.generateToken(authenticatedUser);
            return new JwtResponseDto(token);
        } catch (AuthenticationException exception) {
            log.warn(
                    "Falha na operacao [LOGIN] para o Usuario [N/A]. Recurso: [USER] [email={}]. Motivo: credenciais invalidas.",
                    maskEmail(email));
            throw new UnauthorizedAccessException("Credenciais invalidas.");
        }
    }

    @Transactional
    public UserDto saveUser(final CreateUserRequest userRequest) {
        if (userRequest == null) {
            throw new BusinessException("Dados do usuario nao informados.");
        }

        validateRequired(userRequest.email(), "Erro ao cadastrar usuraio: Email deve ser informado.");
        validateRequired(userRequest.name(), "Nome deve ser informado.");
        validateRequired(userRequest.password(), "Senha deve ser informada.");

        userRepository.findByEmail(userRequest.email()).ifPresent(existingUser -> {
            throw new BusinessException("Ja existe usuario cadastrado com este email.");
        });

        final User newUser = mapper.toEntity(userRequest);
        newUser.setPassword(passwordEncoder.encode(userRequest.password()));

        return mapper.toDto(userRepository.save(newUser));
    }

    @Transactional(readOnly = true)
    public UserDto findByEmail(final String email) {
        validateRequired(email, "Email deve ser informado.");

        final User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario nao encontrado para o email informado."));
        return mapper.toDto(user);
    }

    @Transactional(readOnly = true)
    public UserDto findByUuid(final String id) {
        validateRequired(id, "Id deve ser informado.");

        @SuppressWarnings("null")
        final User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario nao encontrado para o id informado."));
        return mapper.toDto(user);
    }

    @SuppressWarnings("null")
    @Transactional
    public void delete(final String email) {
        validateRequired(email, "Email deve ser informado.");

        final User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Nao e possivel deletar: Usuario nao encontrado para o email informado."));
        userRepository.delete(user);
    }

    @SuppressWarnings("null")
    @Transactional
    public void deleteById(final String id) {
        validateRequired(id, "Id deve ser informado.");

        final User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Nao e possivel deletar: Usuario nao encontrado para o id informado."));
        userRepository.delete(user);
    }

    @Transactional
    public UserDto updateById(final String id, final UserUpdateData dto) {
        validateRequired(id, "Id deve ser informado para atualizar o usuario.");
        if (dto == null) {
            throw new BusinessException("Dados de atualizacao nao informados.");
        }

        @SuppressWarnings("null")
        final User updatedUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Nao e possivel atualizar: Usuario nao encontrado para o id informado."));

        if (hasText(dto.email())) {
            validateUniqueEmail(dto.email(), updatedUser.getId());
            updatedUser.setEmail(dto.email().trim());
        }

        updateTextField(dto.name(), updatedUser::setName);

        if (hasText(dto.password())) {
            updatedUser.setPassword(passwordEncoder.encode(dto.password()));
        }

        return mapper.toDto(userRepository.save(updatedUser));
    }

    @Transactional
    public UserDto update(final UserUpdateData dto) {
        if (dto == null) {
            throw new BusinessException("Dados de atualizacao nao informados.");
        }

        validateRequired(dto.email(), "Email deve ser informado para atualizar o usuario.");

        final User updatedUser = userRepository.findByEmail(dto.email())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario nao encontrado para o email informado."));

        updateTextField(dto.name(), updatedUser::setName);

        if (hasText(dto.password())) {
            updatedUser.setPassword(passwordEncoder.encode(dto.password()));
        }

        return mapper.toDto(userRepository.save(updatedUser));
    }

    private void validateUniqueEmail(final String email, final String currentUserId) {
        final String normalizedEmail = email.trim();
        userRepository.findByEmail(normalizedEmail).ifPresent(existingUser -> {
            if (!existingUser.getId().equals(currentUserId)) {
                throw new BusinessException("Ja existe usuario cadastrado com este email.");
            }
        });
    }

    private void updateTextField(final String value, final Consumer<String> updateAction) {
        if (hasText(value)) {
            updateAction.accept(value.trim());
        }
    }

    private void validateRequired(final String value, final String message) {
        if (!hasText(value)) {
            throw new BusinessException(message);
        }
    }

    private boolean hasText(final String value) {
        return value != null && !value.isBlank();
    }

    private String maskEmail(final String email) {
        if (!hasText(email)) {
            return "***";
        }

        final int atIndex = email.indexOf('@');
        if (atIndex <= 1) {
            return "***";
        }

        return email.charAt(0) + "***" + email.substring(atIndex);
    }
}
