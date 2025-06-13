package com.genial.demo.entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "PRODUTOS")
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "id_storage")
    private Storage storage;

    private String name;
    private String description;
    private String sector;
    private Double value;
    private LocalDate date;
    private Integer quantidade;

    @PrePersist
    void onCreate() {
        this.date = LocalDate.now();
    }

    public Product(String name, String description, String sector, Double value, Integer quantidade) {
        this.name = name;
        this.description = description;
        this.sector = sector;
        this.value = value;
        this.quantidade = quantidade;
    }

}
