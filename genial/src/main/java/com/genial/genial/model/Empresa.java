package com.genial.genial.model;

public class Empresa {
    
    private String nome;

    private String descricao;
    
    private Long id;

    private Inventario inventario;

    public Empresa(String nome, String descricao)
    {
        this.nome = nome;
        this.descricao = descricao;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


}
