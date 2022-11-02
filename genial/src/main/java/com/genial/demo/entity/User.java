package com.genial.demo.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="tb_users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private UUID uuid;

    @OneToMany(mappedBy = "user")
    private List<Storage> storageList = new ArrayList<Storage>();

    @Column(unique = true)
    private String email;

    private String name;
    private String password;

    public User() {
        uuid = UUID.randomUUID();
    }

}