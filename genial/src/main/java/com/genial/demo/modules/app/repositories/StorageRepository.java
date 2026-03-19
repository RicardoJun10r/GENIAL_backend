package com.genial.demo.modules.app.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.genial.demo.modules.app.model.Storage;

public interface StorageRepository extends JpaRepository<Storage, String> {

    Optional<Storage> findByIdAndUserId(String id, String userId);

    Optional<Storage> findByNameAndUserId(String name, String userId);

    Optional<Storage> findByNameAndUserEmail(String name, String email);

    Page<Storage> findByUserId(String userId, Pageable pageable);
}
