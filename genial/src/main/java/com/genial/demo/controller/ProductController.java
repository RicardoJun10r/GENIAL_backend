package com.genial.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.genial.demo.entity.Product;
import com.genial.demo.repositories.ProductRepository;
import com.genial.demo.services.ProductService;

@RestController
@RequestMapping("/product")
public class ProductController {
    
    @Autowired
    ProductRepository repository;
    @Autowired
    ProductService service;

    @GetMapping("/search/byName")
    public Product getByName(@Param("name") String name){
        return service.findByName(name);
    }

   @PostMapping
    public ResponseEntity<Product> save(@RequestBody Product dto){
        
        Product product = service.save(dto);

        if(product == null){
            return new ResponseEntity<>(null,HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(product,HttpStatus.CREATED);
        
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<String> delete(@PathVariable String name){
        
        Product product = new Product();
        product.setName(name);
        

        try {
            service.delete(product);
            return new ResponseEntity<>(name,HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>("",HttpStatus.NOT_FOUND);

        }
        
    }

}
