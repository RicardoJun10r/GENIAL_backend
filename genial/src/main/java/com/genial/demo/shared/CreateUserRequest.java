package com.genial.demo.shared;

public record CreateUserRequest(
                String email,
                String name,
                String password) {

}
