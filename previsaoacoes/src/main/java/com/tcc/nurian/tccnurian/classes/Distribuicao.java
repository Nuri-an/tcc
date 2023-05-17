/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tcc.nurian.tccnurian.classes;

import java.util.ArrayList;

/**
 *
 * @author nuria
 * 
 * Distribuição é o vetor Mi
 */
public class Distribuicao {

    private double[] distribuicaoM0;

    public Distribuicao(int tamanho) {
        this.distribuicaoM0 = new double[tamanho];
        for (int i = 0; i < tamanho; i++) {
            this.distribuicaoM0[i] = 0.0;
        }
    }

    /**
     * @return the distribuicao
     */
    public double[] getDistribuicaoM0() {
        return distribuicaoM0;
    }

    /**
     * @param estados
     * @param qtnTotal
     */
    public void criarDistribuicaoM0(ArrayList<Estado> estados, int qtnTotal) {
        for (int i = 0; i < estados.size(); i++) {
            this.distribuicaoM0[i] = this.distribuicaoPorEstado(estados.get(i).getQtnItens(), qtnTotal);
        }
    }

    public double distribuicaoPorEstado(int qtnIntervalo, int qtnTotal) {
        return qtnIntervalo / (1.0f * qtnTotal);
    }


    /**
     * Esta função basicamente multiplica um vetor x matriz
     * Especificamente ele multiplica o Mi * Matriz^Passos
     * @param matriz: é a matrix_i
     * @return 
     */
    public double[] getDistribuicaoMi(double matriz[][]) {
        //double distribuicaoResultado[] = multVetorMatriz(matriz);
        //System.out.println("\n\n________________________________________________");
        //System.out.println("Vetor Distribuicao " + passos + " passos:");
        //this.printDistribuicao(distribuicaoResultado);

        double distribuicaoResultado[];
        distribuicaoResultado = new double[this.distribuicaoM0.length];
        double valorPosicao = 0.00;

        // multiplicaçaõ da matriz_n pelo M0
        for (int linha = 0; linha < this.distribuicaoM0.length; linha++) {
            for (int coluna = 0; coluna < this.distribuicaoM0.length; coluna++) {
                valorPosicao += this.distribuicaoM0[coluna] * matriz[coluna][linha];
            }
            distribuicaoResultado[linha] = valorPosicao;
            valorPosicao = 0.00;
        }

        return distribuicaoResultado;
    }
    
    /**
     * Esta função pega o vetor Mi qualquer e 
     * faz a acumulaçaõ dele 
     * @param distribuicaoMi
     * @return 
     */
    public ArrayList<Double> getDistribuicaoAcc(double[] distribuicaoMi) {
        ArrayList<Double> distribuicaoAcc = new ArrayList<>();
        double soma = 0;
        //System.out.println("M_acc[");
        for (int i = 0; i < distribuicaoMi.length; i++) {
            soma += distribuicaoMi[i];
            distribuicaoAcc.add(soma);
            //System.out.println(soma + ", ");            
        }

        //System.out.println("]");
        return distribuicaoAcc;
    }
    
    
    public void imprime(double distribuicao[]) {
        System.out.print("\n \n");
        System.out.print("Vetor de distribuicao: [");
        for (int i = 0; i < distribuicao.length; i++) {
            System.out.print("\t");
            System.out.print( Math.round(distribuicao[i] * 100.0) / 100.0);

        }
        System.out.print("]\n \n");
    }
}
