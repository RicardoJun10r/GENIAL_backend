package com.genial.demo.shared.mapper;

import java.util.Collections;
import java.util.List;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.genial.demo.modules.app.model.Product;
import com.genial.demo.modules.app.model.Storage;
import com.genial.demo.shared.ProductCreate;
import com.genial.demo.shared.ProductNestedResponse;
import com.genial.demo.shared.ProductResponse;
import com.genial.demo.shared.ProductUpdate;
import com.genial.demo.shared.StorageResponse;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ProductMapper {

    ProductResponse toResponse(Product entity);

    @Mapping(target = "storage", source = "storage")
    ProductNestedResponse toNestedResponse(Product entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "storage", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "active", ignore = true)
    Product toEntity(ProductCreate dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "storage", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "active", ignore = true)
    void updateEntityFromDto(ProductUpdate dto, @MappingTarget Product entity);

    default StorageResponse toStorageResponse(Storage storage) {
        if (storage == null) {
            return null;
        }

        List<ProductResponse> products = storage.getProducts() == null
                ? Collections.emptyList()
                : storage.getProducts().stream().map(this::toResponse).toList();

        StorageResponse response = new StorageResponse(
                storage.getName(),
                storage.getDescription(),
                products,
                storage.getUser() == null ? null
                        : new com.genial.demo.shared.UserResponse(
                                storage.getUser().getId(),
                                storage.getUser().getEmail(),
                                storage.getUser().getName()));
        response.setId(storage.getId());
        return response;
    }
}
