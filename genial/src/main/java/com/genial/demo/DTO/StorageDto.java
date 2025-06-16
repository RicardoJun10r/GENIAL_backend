package com.genial.demo.DTO;

import java.util.List;

import com.genial.demo.entity.Product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StorageDto {
    private String name;
    private String description;
    private List<Product> products;
}
