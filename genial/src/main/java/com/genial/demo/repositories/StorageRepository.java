package com.genial.demo.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.genial.demo.model.Storage;

public interface StorageRepository extends JpaRepository<Storage, String> {

    Optional<Storage> findByIdAndUserId(String id, String userId);

    Page<Storage> findByUserId(String userId, Pageable pageable);

}
