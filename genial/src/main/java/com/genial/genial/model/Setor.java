package com.genial.genial.model;

public class Setor<T> {

    private String nome;

    private T valor;
    
    private Setor<T> proximo;

    public Setor(T valor)
    {
        this.valor = valor;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public T getValor() {
        return valor;
    }

    public void setValor(T valor) {
        this.valor = valor;
    }

    public Setor<T> getProximo() {
        return proximo;
    }

    public void setProximo(Setor<T> proximo) {
        this.proximo = proximo;
    }


}
