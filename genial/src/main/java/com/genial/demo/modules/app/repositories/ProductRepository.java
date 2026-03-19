package com.genial.demo.modules.app.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.genial.demo.modules.app.model.Product;

public interface ProductRepository extends JpaRepository<Product, String> {

    Optional<Product> findByIdAndStorageUserId(String id, String userId);

    Page<Product> findAllByStorageUserId(String userId, Pageable pageable);

}
