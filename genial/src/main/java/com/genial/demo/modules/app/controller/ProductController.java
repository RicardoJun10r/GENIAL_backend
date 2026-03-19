package com.genial.demo.modules.app.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.genial.demo.modules.app.services.ProductService;
import com.genial.demo.shared.ProductCreate;
import com.genial.demo.shared.ProductResponse;
import com.genial.demo.shared.ProductUpdate;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok().body(productService.findById(id));
    }

    @PostMapping("/storages/{storageId}")
    public ResponseEntity<ProductResponse> addProductOnStorage(@PathVariable String storageId,
            @Valid @RequestBody ProductCreate product) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.addProductOnStorage(storageId, product));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> update(@PathVariable String id,
            @Valid @RequestBody ProductUpdate dto) {
        final ProductUpdate updateRequest = withPathId(id, dto);
        return ResponseEntity.ok().body(productService.update(updateRequest));
    }

    private ProductUpdate withPathId(final String id, final ProductUpdate dto) {
        return new ProductUpdate(id, dto.name(), dto.description(), dto.sector(), dto.value(), dto.quantidade());
    }
}
