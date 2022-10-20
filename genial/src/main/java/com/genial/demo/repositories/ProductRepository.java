package com.genial.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.genial.demo.entity.Product;

@RepositoryRestResource(exported = false)
public interface ProductRepository extends JpaRepository<Product,Long> { 
    Product findByName(String name);
    public void deleteByName(String name);

}
