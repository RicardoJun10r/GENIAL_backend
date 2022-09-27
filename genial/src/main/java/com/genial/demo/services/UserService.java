package com.genial.demo.services;


import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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

    public UserDto findByEmail(String email){
        User user = repository.findByEmail(email);
        UserDto dto = mapper.map(user,UserDto.class);
        return dto;
    }

    @Transactional(propagation=Propagation.REQUIRED,readOnly=false)
    public UserDto save(UserCreateDto dto){
        User user = new User();
        user = mapper.map(dto,User.class);
        return mapper.map(repository.save(user), UserDto.class);
    }

    @Transactional(propagation=Propagation.REQUIRED,readOnly=false)
    public void delete (UserDto dto){
        repository.deleteByEmail(dto.getEmail());
    }
    

}
