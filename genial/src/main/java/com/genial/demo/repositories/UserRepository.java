package com.genial.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.genial.demo.entity.User;

@RepositoryRestResource(exported = false)
public interface UserRepository extends JpaRepository<User,Long> { 
    User findByEmail(String email);
    public void deleteByEmail(String email);

}
