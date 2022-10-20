package com.genial.demo.services;


import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.genial.demo.entity.Product;
import com.genial.demo.repositories.ProductRepository;

@Service
public class ProductService {
    
    @Autowired
    private ProductRepository repository;
    @Autowired
    private ModelMapper mapper;

    public Product findByName(String Name){
        Product product = repository.findByName(Name);
       
        return product;
    }
    
    @Transactional(propagation=Propagation.REQUIRED,readOnly=false)
    public Product save(Product dto){
        Product product = new Product();
        product = mapper.map(dto,Product.class);
        return mapper.map(repository.save(product), Product.class);
    }

    @Transactional(propagation=Propagation.REQUIRED,readOnly=false)
    public void delete (Product dto){
        repository.deleteByName(dto.getName());
    }
    

}
