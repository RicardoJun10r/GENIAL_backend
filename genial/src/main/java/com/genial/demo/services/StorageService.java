package com.genial.demo.services;


import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.genial.demo.DTO.StorageDto;
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

    public StorageDto findByName(String Name){
        Storage storage = repository.findByName(Name);
        StorageDto dto = mapper.map(storage, StorageDto.class);
        return dto;
    }

    @Transactional(propagation=Propagation.REQUIRED,readOnly=false)
    public StorageDto save(Storage dto){
        Storage storage = new Storage();
        storage = mapper.map(dto,Storage.class);
        return mapper.map(repository.save(storage), StorageDto.class);
    }

    @Transactional(propagation=Propagation.REQUIRED,readOnly=false)
    public void delete (Storage dto){
        repository.deleteByName(dto.getName());
    }
    

}
