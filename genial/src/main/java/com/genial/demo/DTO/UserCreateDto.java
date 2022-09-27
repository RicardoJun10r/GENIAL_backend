package com.genial.demo.DTO;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCreateDto {
    
    private String email;
    private UUID uuid;
    private String name;
    private String password;

}
