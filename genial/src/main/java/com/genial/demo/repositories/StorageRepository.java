package com.genial.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.genial.demo.entity.Storage;

public interface StorageRepository extends JpaRepository<Storage,Long> {
    


}
