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
 */
public class MatrizTransicao {

    private double[][] matriz;

    public MatrizTransicao(int qtnEstados) {
        this.matriz = new double[qtnEstados][qtnEstados];
        for (int i = 0; i < qtnEstados; i++) {
            for (int j = 0; i < qtnEstados; i++) {
                this.matriz[i][j] = 0.00;
            }

        }
    }

    public MatrizTransicao(double matriz[][]) {
        this.matriz = matriz;
    }
    
    /**
     * Em uma matriz de tamanho estado.size() X estados.size(), adiciona em cada posição a probabiliddae de transição de i para j
     * @param estados 
     */
    public void criaMatrizInicial(ArrayList<Estado> estados) {
        for (int i = 0; i < estados.size(); i++) {
            for (int j = 0; j < estados.size(); j++) {
                double qtnTransicoes = estados.get(i).getTransicao().get(j).getQtn();
                double qtnItensEstados = estados.get(i).getQtnItens();
                
                if (qtnItensEstados == 0) {
                    this.matriz[i][j] = 0.00;
                    return;
                }
                double probTransicao = qtnTransicoes / qtnItensEstados;
                this.matriz[i][j] = probTransicao;
            }
        }
    }

    /**
     * @return the matriz
     */
    public double[][] getMatriz() {
        return matriz;
    }

    /**
     * @param matriz the matriz to set
     */
    public void setMatriz(double[][] matriz) {
        this.matriz = matriz;
    }

    public void imprime(double matriz[][]) {
        System.out.println("Matriz de transição");
        for (int i = 0; i < matriz.length; i++) {
            System.out.print("\n");
            for (int j = 0; j < matriz.length; j++) {
                System.out.print(Math.round(matriz[i][j] * 1000.0) / 1000.0 + "\t");
            }
        }
        System.out.print("\n");

    }

    /**
     * Eleva uma matriz a um determinado expoente
     *
     * @param expoente: é o numero de passos
     * @return
     */
    public double[][] pontencia(int expoente) {
        double newMatriz[][];
        newMatriz = new double[this.matriz.length][this.matriz.length];
        for (int i = 0; i < this.matriz.length; i++) {
            for (int j = 0; j < this.matriz.length; j++) {
                newMatriz[i][j] = this.matriz[i][j];
            }
        }

        for (int k = 1; k < expoente; k++) {
            double matrizResultado[][] = produto(newMatriz, this.matriz);
            newMatriz = matrizResultado;
        }
        
        // System.out.println("\n\n________________________________________________");
        // System.out.println("Matriz " + passos + " passos:");
        // this.printMatriz(newMatriz);
        return newMatriz;

    }

    public double[][] produto(double matrizA[][], double matrizB[][]) {
        double matrizResultado[][];
        matrizResultado = new double[matrizB.length][matrizB.length];
        double valorPosicao = 0.00;
        
        for (int linha = 0; linha < matrizB.length; linha++) {
            for (int coluna = 0; coluna < matrizB.length; coluna++) {
                for (int j = 0; j < matrizB.length; j++) {
                    double valorMatiz = matrizA[linha][j];
                    double valorThisMatriz = matrizB[j][coluna];
                    valorPosicao +=  valorMatiz * valorThisMatriz;
                    
                }
                   
                matrizResultado[linha][coluna] = valorPosicao;
                valorPosicao = 0.00;
            }
        }
        
        //this.imprime(matrizResultado);
        
        return matrizResultado;
    }
}
