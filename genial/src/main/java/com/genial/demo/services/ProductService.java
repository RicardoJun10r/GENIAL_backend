package com.genial.demo.services;

import java.util.Optional;

import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.genial.demo.model.Product;
import com.genial.demo.model.Storage;
import com.genial.demo.repositories.ProductRepository;
import com.genial.demo.repositories.StorageRepository;
import com.genial.demo.shared.ProductCreate;
import com.genial.demo.shared.ProductResponse;
import com.genial.demo.shared.ProductUpdate;
import com.genial.demo.shared.mapper.ProductMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    private final StorageRepository storageRepository;

    private final ProductMapper mapper;

    @Transactional
    public ProductResponse addProductOnStorage(String id_storage, ProductCreate product) {
        Optional<Storage> storage = this.storageRepository.findById(id_storage);
        if (storage.isPresent()) {
            Product novo_produto = mapper.toEntity(product);
            novo_produto.setStorage(storage.get());
            Product savedProduct = this.productRepository.save(novo_produto);
            storage.get().getProducts().add(savedProduct);
            this.storageRepository.save(storage.get());
            return mapper.toResponse(savedProduct);
        }
        throw new RuntimeException("Erro");
    }

    @Transactional(readOnly = true)
    public ProductResponse findById(String id) {
        Optional<Product> product = productRepository.findById(id);
        if (product.isPresent()) {
            return mapper.toResponse(product.get());
        }
        throw new RuntimeException("Erro");
    }

    public void delete(String id) {
        productRepository.deleteById(id);
    }

    @Transactional
    public ProductResponse update(ProductUpdate dto) {
        Product productToUpdate = productRepository.findById(dto.id())
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com o ID: " + dto.id()));

        if (dto.name() != null && !dto.name().isBlank()) {
            productToUpdate.setName(dto.name());
        }

        if (dto.description() != null && !dto.description().isBlank()) {
            productToUpdate.setDescription(dto.description());
        }

        if (dto.sector() != null && !dto.sector().isBlank()) {
            productToUpdate.setSector(dto.sector());
        }

        if (dto.value() != null) {
            productToUpdate.setValue(dto.value());
        }

        if (dto.quantidade() != null) {
            productToUpdate.setQuantidade(dto.quantidade());
        }

        Product updatedProduct = productRepository.save(productToUpdate);

        return mapper.toResponse(updatedProduct);
    }

}
