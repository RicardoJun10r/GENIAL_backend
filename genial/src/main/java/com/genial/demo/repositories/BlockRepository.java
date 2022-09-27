package com.genial.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.genial.demo.entity.Block;

public interface BlockRepository extends JpaRepository<Block,Long> {
    


}
