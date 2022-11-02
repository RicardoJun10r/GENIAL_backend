package com.genial.demo.DTO;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCreateDto {
    
    private UUID uuid;
    private String email;
    private String name;
    private String password;

}
