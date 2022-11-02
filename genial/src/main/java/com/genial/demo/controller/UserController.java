package com.genial.demo.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.genial.demo.DTO.UserCreateDto;
import com.genial.demo.DTO.UserDto;
import com.genial.demo.entity.User;
import com.genial.demo.repositories.UserRepository;
import com.genial.demo.services.UserService;

@RestController
@RequestMapping("/users")
public class UserController {
    
    @Autowired
    UserRepository repository;
    @Autowired
    UserService service;

    @GetMapping("/search/byEmail")
    public UserDto getByEmail(@Param("email") String email){
        return service.findByEmail(email);
    }

   @PostMapping
    public ResponseEntity<UserDto> save(@RequestBody User dto){
        
        UserDto user = service.save(dto);

        if(user == null){
            return new ResponseEntity<>(null,HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(user,HttpStatus.CREATED);
        
    }

    @DeleteMapping("/{email}")
    public ResponseEntity<String> delete(@PathVariable String email){
        
        UserDto user = new UserDto();
        user.setEmail(email);
        

        try {
            service.delete(user);
            return new ResponseEntity<>(email,HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>("",HttpStatus.NOT_FOUND);

        }
        
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<UserDto> update(@PathVariable String uuid, @RequestBody UserCreateDto dto) {
        Optional<UserDto> user = service.update(uuid, dto);
        if (user.isPresent()){
            return ResponseEntity.ok(user.get());
        }else{
            return ResponseEntity.badRequest().build();
        }
    }

}
