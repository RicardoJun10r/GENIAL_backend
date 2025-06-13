package com.genial.demo.DTO;

public record UserLoginRequest(
        String email,
        String password) {
}