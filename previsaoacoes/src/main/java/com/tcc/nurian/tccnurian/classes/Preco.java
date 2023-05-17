/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tcc.nurian.tccnurian.classes;

/**
 *
 * @author nuria
 */
public class Preco {
    private double preco;
    private Estado estado;
    
    public Preco() {
        this.preco = 0;
        this.estado = new Estado();
    }
    
    public Preco(double preco, Estado estado) {
        this.preco = preco;
        this.estado = estado;
    }
    
    public Preco(double preco) {
        this.preco = preco;
        this.estado = new Estado();
    }

    /**
     * @return the preco
     */
    public double getPreco() {
        return preco;
    }

    /**
     * @param preco the preco to set
     */
    public void setPreco(double preco) {
        this.preco = preco;
    }

    /**
     * @return the estado
     */
    public Estado getEstado() {
        return estado;
    }

    /**
     * @param estado the estado to set
     */
    public void setEstado(Estado estado) {
        this.estado = estado;
    }
}
