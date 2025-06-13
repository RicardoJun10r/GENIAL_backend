package com.genial.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.genial.demo.DTO.CreateUserRequest;
import com.genial.demo.DTO.UserDto;
import com.genial.demo.DTO.UserUpdateData;
import com.genial.demo.services.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/buscar")
    public UserDto getByEmail(@RequestParam("email") String email) {
        return userService.findByEmail(email);
    }

    @PostMapping
    public ResponseEntity<UserDto> save(@RequestBody CreateUserRequest dto) {
        return ResponseEntity.ok().body(userService.saveUser(dto));
    }

    @DeleteMapping("/{email}")
    public ResponseEntity<String> delete(@PathVariable String email) {
        userService.delete(email);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/atualizar")
    public ResponseEntity<UserDto> update(@RequestBody UserUpdateData dto) {
        return ResponseEntity.ok().body(userService.update(dto));
    }

}
