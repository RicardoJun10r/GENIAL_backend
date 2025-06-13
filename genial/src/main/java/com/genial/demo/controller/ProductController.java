package com.genial.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.genial.demo.DTO.ProductCreate;
import com.genial.demo.DTO.ProductNestedResponse;
import com.genial.demo.DTO.ProductResponse;
import com.genial.demo.DTO.ProductUpdate;
import com.genial.demo.services.ProductService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/produtos")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/buscar")
    public ResponseEntity<ProductNestedResponse> getByName(@RequestParam("id") String id) {
        return ResponseEntity.ok().body(productService.findById(id));
    }

    @PostMapping("/{id_storage}")
    public ResponseEntity<ProductResponse> addProductOnStorage(@PathVariable String id_storage,
            @RequestBody ProductCreate product) {

        return ResponseEntity.ok().body(productService.addProductOnStorage(id_storage, product));

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable String id) {

        productService.delete(id);
        return ResponseEntity.ok().body("Deletado!");

    }

    @PutMapping("/atualizar")
    public ResponseEntity<ProductResponse> update(@RequestBody ProductUpdate dto) {
        return ResponseEntity.ok().body(productService.update(dto));
    }

}
