package com.genial.demo.shared;

public record UserLoginRequest(
        String email,
        String password) {
}