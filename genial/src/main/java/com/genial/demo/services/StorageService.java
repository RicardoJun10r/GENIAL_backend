package com.genial.demo.services;


import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.genial.demo.entity.Storage;
import com.genial.demo.repositories.StorageRepository;

@Service
public class StorageService {
    
    @Autowired
    private StorageRepository repository;

    @Autowired
    private UserService userService;

    @Autowired
    private ModelMapper mapper;

    public Storage findByName(String Name){
        Storage storage = repository.findByName(Name);
       
        return storage;
    }

    @Transactional(propagation=Propagation.REQUIRED,readOnly=false)
    public Storage save(Storage dto){
        Storage storage = new Storage();
        storage = mapper.map(dto,Storage.class);
        return mapper.map(repository.save(storage), Storage.class);
    }

    @Transactional(propagation=Propagation.REQUIRED,readOnly=false)
    public void delete (Storage dto){
        repository.deleteByName(dto.getName());
    }
    

}
