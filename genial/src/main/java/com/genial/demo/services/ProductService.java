package com.genial.demo.services;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.genial.demo.DTO.ProductCreate;
import com.genial.demo.DTO.ProductResponse;
import com.genial.demo.DTO.ProductUpdate;
import com.genial.demo.entity.Product;
import com.genial.demo.entity.Storage;
import com.genial.demo.repositories.ProductRepository;
import com.genial.demo.repositories.StorageRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    private final StorageRepository storageRepository;

    private final ModelMapper mapper;

    @Transactional
    public ProductResponse addProductOnStorage(String id_storage, ProductCreate product) {
        Optional<Storage> storage = this.storageRepository.findById(id_storage);
        if (storage.isPresent()) {
            Product novo_produto = new Product(product.name(), product.description(), product.sector(), product.value(),
                    product.quantidade());
            novo_produto.setStorage(storage.get());
            storage.get().getProducts().add(this.productRepository.save(novo_produto));
            this.storageRepository.save(storage.get());
            return new ProductResponse(
                    novo_produto.getId(), novo_produto.getName(), novo_produto.getDescription(),
                    novo_produto.getSector(), novo_produto.getValue(), novo_produto.getDate(),
                    novo_produto.getQuantidade());
        }
        throw new RuntimeException("Erro");
    }

    @Transactional(readOnly = true)
    public ProductResponse findById(String id) {
        Optional<Product> product = productRepository.findById(id);
        if (product.isPresent()) {
            return new ProductResponse(
                    product.get().getId(),
                    product.get().getName(), product.get().getDescription(), product.get().getSector(),
                    product.get().getValue(), product.get().getDate(), product.get().getQuantidade());
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

        return mapper.map(updatedProduct, ProductResponse.class);
    }

}
