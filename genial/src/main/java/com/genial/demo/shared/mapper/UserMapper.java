package com.genial.demo.shared.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.genial.demo.modules.app.model.User;
import com.genial.demo.shared.CreateUserRequest;
import com.genial.demo.shared.UserDto;
import com.genial.demo.shared.UserResponse;
import com.genial.demo.shared.UserUpdateData;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR, uses = StorageMapper.class)
public interface UserMapper {

    UserDto toDto(User entity);

    UserResponse toResponse(User entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "storages", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    User toEntity(CreateUserRequest dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "storages", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    User toEntity(UserUpdateData dto);
}
