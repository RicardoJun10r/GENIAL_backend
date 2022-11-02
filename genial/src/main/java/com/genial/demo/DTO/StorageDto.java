package com.genial.demo.DTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StorageDto {
    
    private Long id;
    private UserDto user;
    private String name;
    private String description;


}
