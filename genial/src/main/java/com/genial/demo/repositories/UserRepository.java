package com.genial.demo.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.genial.demo.model.User;

public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByEmail(String email);

}
