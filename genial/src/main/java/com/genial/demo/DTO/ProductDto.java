package com.genial.demo.DTO;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDto {
    
    private Long id;
    private StorageDto storage;
    private String name;
    private String description;
    private String sector;
    private Float value;
    private Date date;  
    private int quantidade;

}
