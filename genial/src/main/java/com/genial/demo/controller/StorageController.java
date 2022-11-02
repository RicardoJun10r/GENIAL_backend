package com.genial.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.genial.demo.DTO.StorageDto;
import com.genial.demo.entity.Storage;
import com.genial.demo.repositories.StorageRepository;
import com.genial.demo.services.StorageService;

@RestController
@RequestMapping("/storage")
public class StorageController {
    
    @Autowired
    StorageRepository repository;
    @Autowired
    StorageService service;

    @GetMapping("/search/byName")
    public StorageDto getByName(@Param("name") String name){
        return service.findByName(name);
    }

   @PostMapping
    public ResponseEntity<StorageDto> save(@RequestBody Storage dto){
        
        StorageDto storage = service.save(dto);

        if(storage == null){
            return new ResponseEntity<>(null,HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(storage,HttpStatus.CREATED);
        
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<String> delete(@PathVariable String name){
        
        Storage user = new Storage();
        user.setName(name);
        

        try {
            service.delete(user);
            return new ResponseEntity<>(name,HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>("",HttpStatus.NOT_FOUND);

        }
        
    }

}
