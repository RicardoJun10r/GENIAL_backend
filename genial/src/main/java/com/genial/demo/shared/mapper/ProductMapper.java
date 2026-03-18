package com.genial.demo.shared.mapper;

import java.util.Collections;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.genial.demo.model.Product;
import com.genial.demo.model.Storage;
import com.genial.demo.shared.ProductCreate;
import com.genial.demo.shared.ProductNestedResponse;
import com.genial.demo.shared.ProductResponse;
import com.genial.demo.shared.ProductUpdate;
import com.genial.demo.shared.StorageResponse;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductResponse toResponse(Product product);

    @Mapping(target = "storage", source = "storage")
    ProductNestedResponse toNestedResponse(Product product);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "storage", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "active", ignore = true)
    Product toEntity(ProductCreate request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "storage", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "active", ignore = true)
    void updateProductFromDto(ProductUpdate request, @MappingTarget Product product);

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
