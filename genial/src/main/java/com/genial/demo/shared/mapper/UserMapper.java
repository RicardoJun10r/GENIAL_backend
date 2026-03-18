package com.genial.demo.shared.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.genial.demo.model.User;
import com.genial.demo.shared.CreateUserRequest;
import com.genial.demo.shared.UserDto;
import com.genial.demo.shared.UserResponse;
import com.genial.demo.shared.UserUpdateData;

@Mapper(componentModel = "spring", uses = StorageMapper.class)
public interface UserMapper {

    UserDto toDto(User user);

    UserResponse toResponse(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "storages", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "active", ignore = true)
    User toEntity(CreateUserRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "storages", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "active", ignore = true)
    void updateUserFromDto(UserUpdateData request, @MappingTarget User user);
}
