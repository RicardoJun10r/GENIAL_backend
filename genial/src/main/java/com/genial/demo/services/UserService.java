package com.genial.demo.services;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import com.genial.demo.DTO.CreateUserRequest;

import com.genial.demo.DTO.UserDto;
import com.genial.demo.DTO.UserLoginRequest;
import com.genial.demo.DTO.UserUpdateData;
import com.genial.demo.entity.User;
import com.genial.demo.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final ModelMapper mapper;

    public UserDto login(UserLoginRequest dto) {
        Optional<User> user = this.userRepository.findByEmail(dto.email());
        if (user.isPresent()) {
            if (user.get().getPassword().equals(dto.password())) {
                return this.mapper.map(user.get(), UserDto.class);
            }
            throw new RuntimeException("Erro");
        }
        throw new RuntimeException("Erro");
    }

    @Transactional
    public UserDto saveUser(CreateUserRequest user) {
        Optional<User> novo_usuario = this.userRepository.findByEmail(user.email());
        if (!novo_usuario.isPresent()) {
            return mapper.map(this.userRepository.save(new User(user.email(), user.name(), user.password())),
                    UserDto.class);
        }
        throw new RuntimeException("Erro");
    }

    @Transactional(readOnly = true)
    public UserDto findByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            return mapper.map(user, UserDto.class);
        }
        throw new RuntimeException("Erro");
    }

    @Transactional(readOnly = true)
    public UserDto findByUuid(String id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            return mapper.map(user.get(), UserDto.class);
        }
        throw new RuntimeException("Erro");

    }

    @Transactional
    public void delete(String email) {
        Optional<User> user = this.userRepository.findByEmail(email);
        if (user.isPresent()) {
            this.userRepository.deleteByEmail(user.get().getEmail());
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
            return mapper.map(userRepository.save(user.get()), UserDto.class);
        }
        throw new RuntimeException("Erro");
    }

}
