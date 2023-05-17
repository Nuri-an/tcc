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
public class Transicao {
    private String nome;
    private int qtn;
    
    public Transicao() {
        this.nome = "";
        this.qtn = 0;
    }

    /**
     * @return the nome
     */
    public String getNome() {
        return nome;
    }

    /**
     * @param nome the nome to set
     */
    public void setNome(String nome) {
        this.nome = nome;
    }


    /**
     * @return the qtn
     */
    public int getQtn() {
        return qtn;
    }

    /**
     * @param qtn the qtn to set
     */
    public void setQtn(int qtn) {
        this.qtn = qtn;
    }
}
