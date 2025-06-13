package com.genial.demo.DTO;

import java.util.List;

import com.genial.demo.entity.Product;

public record StorageResponse(
        String name,
        String description,
        List<Product> products,
        UserDto user) {

}
