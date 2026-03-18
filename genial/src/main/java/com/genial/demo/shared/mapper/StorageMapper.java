package com.genial.demo.shared.mapper;

import java.util.ArrayList;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.genial.demo.model.Storage;
import com.genial.demo.model.User;
import com.genial.demo.shared.StorageCreate;
import com.genial.demo.shared.StorageDto;
import com.genial.demo.shared.StorageResponse;
import com.genial.demo.shared.StorageUpdate;
import com.genial.demo.shared.UserResponse;

@Mapper(componentModel = "spring", uses = ProductMapper.class, imports = ArrayList.class)
public interface StorageMapper {

    StorageDto toDto(Storage storage);

    StorageResponse toResponse(Storage storage);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "products", expression = "java(new ArrayList<>())")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "active", ignore = true)
    Storage toEntity(StorageCreate request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "products", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "active", ignore = true)
    void updateStorageFromDto(StorageUpdate request, @MappingTarget Storage storage);

    UserResponse toUserResponse(User user);
}
