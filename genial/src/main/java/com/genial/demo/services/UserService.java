package com.genial.demo.services;


import java.util.Optional;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.genial.demo.DTO.UserCreateDto;
import com.genial.demo.DTO.UserDto;
import com.genial.demo.entity.User;
import com.genial.demo.repositories.UserRepository;

@Service
public class UserService {
    
    @Autowired
    private UserRepository repository;
    @Autowired
    private ModelMapper mapper;


    public User currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getPrincipal().toString();

        User user = repository.findByEmail(email);

        return user;
    }

    public UserDto findByEmail(String email){
        User user = repository.findByEmail(email);
        UserDto dto = mapper.map(user,UserDto.class);
        return dto;
    }

    public UserCreateDto findUserByEmail(String email) {
		User user = repository.findByEmail(email);
		UserCreateDto dto = mapper.map(user, UserCreateDto.class);
		return dto;
	}

    public Optional<UserDto> findByUuid(String uuid){
        Optional<User> user = repository.findByUuid(UUID.fromString(uuid));
        if (user.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(mapper.map(user.get(),UserDto.class));
    }

    @Transactional(propagation=Propagation.REQUIRED,readOnly=false)
    public UserDto save(User dto){
        User user = new User();
        
        user.setEmail(dto.getEmail());
        user.setName(dto.getName());
        String pass = (new BCryptPasswordEncoder()).encode(dto.getPassword());
        user.setPassword(pass);

        return mapper.map(repository.save(user), UserDto.class);
    }

    @Transactional(propagation=Propagation.REQUIRED,readOnly=false)
    public void delete (UserDto dto){
        repository.deleteByEmail(dto.getEmail());
    }

    public Optional<UserDto> update(String uuid, UserCreateDto dto) {
        Optional<User> user = repository.findByUuid(UUID.fromString(uuid));
        if (user.isEmpty()) {
          return Optional.empty();
        }

        user.get().setName(dto.getName());
        user.get().setPassword(dto.getPassword());
    
        return Optional.of(mapper.map(repository.save(user.get()), UserDto.class));
    }

}
