package com.genial.demo.entity;

import java.util.Date;
import java.util.GregorianCalendar;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "tb_product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_storage")
    private Storage storage;

    private String name;
    private String description;
    private String sector;
    private Float value;
    private Date date;  // data que o produto foi cadatrado 
    private int quantidade; // quantidade de produto no estoque

    // gerar data automaticamente cada vez que se cadastra um produto
    public Product(){
        GregorianCalendar gc=new GregorianCalendar(); 
        date =gc.getTime(); //"Pega" a data do GregorianCalendar para uma variável Date
        gc.setTime(date); //Armazena a data de d1 para o GregorianCalendar gc.
    }
    
}
