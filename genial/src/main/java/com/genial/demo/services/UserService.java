package com.genial.demo.services;

import java.util.Optional;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import com.genial.demo.model.User;
import com.genial.demo.repositories.UserRepository;
import com.genial.demo.shared.CreateUserRequest;
import com.genial.demo.shared.UserDto;
import com.genial.demo.shared.UserLoginRequest;
import com.genial.demo.shared.UserUpdateData;
import com.genial.demo.shared.mapper.UserMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final UserMapper mapper;

    public UserDto login(UserLoginRequest dto) {
        Optional<User> user = this.userRepository.findByEmail(dto.email());
        if (user.isPresent()) {
            if (user.get().getPassword().equals(dto.password())) {
                return this.mapper.toDto(user.get());
            }
            throw new RuntimeException("Erro: credenciais erradas");
        }
        throw new RuntimeException("Erro: Nao achado");
    }

    @Transactional
    public UserDto saveUser(CreateUserRequest user) {
        Optional<User> novo_usuario = this.userRepository.findByEmail(user.email());
        if (!novo_usuario.isPresent()) {
            User newUser = mapper.toEntity(user);
            return mapper.toDto(this.userRepository.save(newUser));
        }
        throw new RuntimeException("Erro");
    }

    @Transactional(readOnly = true)
    public UserDto findByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            UserDto dto = mapper.toDto(user.get());
            if (dto.getStorages() != null) {
                dto.getStorages().forEach(s -> {
                    if (s != null) {
                        System.out.println(s.getName() + " " + s.getDescription());
                    }
                });
            }
            return dto;
        }
        throw new RuntimeException("Erro");
    }

    @Transactional(readOnly = true)
    public UserDto findByUuid(String id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            return mapper.toDto(user.get());
        }
        throw new RuntimeException("Erro");

    }

    @Transactional
    public void delete(String email) {
        Optional<User> user = this.userRepository.findByEmail(email);
        if (user.isPresent()) {
            this.userRepository.delete(user.get());
        }
    }

    @Transactional
    public UserDto update(UserUpdateData dto) {
        Optional<User> user = userRepository.findByEmail(dto.email());

        if (user.isPresent()) {
            User usuario_atualizado = user.get();
            if (!dto.email().isEmpty() && !dto.email().isBlank()) {
                usuario_atualizado.setEmail(dto.email());
            }
            if (!dto.name().isEmpty() && !dto.name().isBlank()) {
                usuario_atualizado.setName(dto.name());
            }
            if (!dto.password().isEmpty() && !dto.password().isBlank()) {
                usuario_atualizado.setPassword(dto.password());
            }
            return mapper.toDto(userRepository.save(usuario_atualizado));
        }
        throw new RuntimeException("Erro");
    }

}
