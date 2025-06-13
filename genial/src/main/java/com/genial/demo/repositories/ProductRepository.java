package com.genial.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.genial.demo.entity.Product;

public interface ProductRepository extends JpaRepository<Product, String> {

    void deleteByName(String name);

}
