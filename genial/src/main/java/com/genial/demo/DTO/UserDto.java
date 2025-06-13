package com.genial.demo.DTO;

import java.util.List;

import com.genial.demo.entity.Storage;

public record UserDto(
                String id,
                String email,
                String name,
                List<Storage> storages) {
}
