package com.genial.demo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.genial.demo.entity.Storage;
import com.genial.demo.entity.User;

@RepositoryRestResource(exported = false)
public interface StorageRepository extends JpaRepository<Storage,Long> { 

    public List<Storage> findByUser(User user);
    Storage findByName(String name);
    public void deleteByName(String name);

}
