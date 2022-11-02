package com.genial.demo.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.genial.demo.entity.User;

@RepositoryRestResource(exported = false)
public interface UserRepository extends JpaRepository<User,Long> { 
    
    User findByEmail(String email);
    public Optional<User> findByUuid(UUID uuid);
    public void deleteByEmail(String email);

}
