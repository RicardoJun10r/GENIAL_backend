package com.genial.demo.DTO;

public record ProductCreate(
                String name,
                String description,
                String sector,
                Double value,
                Integer quantidade) {

}
