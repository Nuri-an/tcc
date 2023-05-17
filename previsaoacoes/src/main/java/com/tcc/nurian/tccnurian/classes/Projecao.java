/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tcc.nurian.tccnurian.classes;

import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author nuria
 */
public class Projecao {

    private Random aleatorio;

    public Projecao() {
        long semente = 3;
        this.aleatorio = new Random(Math.round(semente));
    }

    public ArrayList<Double> criarListaPrecoSintetico(ArrayList<Double> distribuicaoAccMi, ArrayList<Estado> estados, double desvPred, int qtnDados) {
        ArrayList<Double> lstSintetica = new ArrayList<>();

        for (int i = 0; i < qtnDados - 1; i++) {
            double aleatorioGerado = this.aleatorio.nextDouble();
            for (int j = 0; j < distribuicaoAccMi.size() - 1; j++) {
                if (aleatorioGerado < distribuicaoAccMi.get(j)) {
                    double fator = aleatorioGerado * desvPred;
                    double precoSintetico_i = fator + estados.get(j).getInicio();
                    lstSintetica.add(precoSintetico_i);
                    break;
                }
            }
        }
        return lstSintetica;
    }

    /**
     * Calcula o preço sintético atráves da análise do vetor distribuição
     * anterior e o novo vetor distribuição, escolhendo o estado futuro com a
     * posição do vetor que mais variou para cima. Retorna o valor inicial desse
     * estado + (valor aleatório * desvio padrão)
     *
     * @param distribuicao_i
     * @param distribuicao_m0
     * @param estados
     * @param desvPred
     * @return double
     */
    public Preco precoSintetico(double lastPrice, double[] distribuicao_i, double[] distribuicao_m0, ArrayList<Estado> estados, double desvPred) {
        double preco = lastPrice;
        double diferenca = 0.0;

        double aleatorioGerado = this.aleatorio.nextDouble();
        for (int j = 0; j < distribuicao_i.length; j++) {
            if (distribuicao_i[j] >= distribuicao_m0[j]) {
                double prox_diferenca = distribuicao_i[j] - distribuicao_m0[j];

                if (diferenca == 0.0 || prox_diferenca > diferenca) {
                    diferenca = prox_diferenca;
                    double fator = aleatorioGerado * desvPred;
                    preco = fator + estados.get(j).getInicio();
                    // System.out.println("Estado: " + estados.get(j).getNome());
                }

            }
        }
        
        Preco precoFuturo = new Preco(preco);
        return precoFuturo;
    }

    /**
     * Calcula o preço sintético atráves da matriz de probabilidades no tempo n - 1,
     * escolhendo o estado FUTURO que o estado ATUAL mais tem probabilidades de transitar.
     * anterior e o novo vetor distribuição, escolhendo o estado futuro com a
     * posição do vetor que mais variou para cima. Retorna o valor inicial desse
     * estado + (valor aleatório * desvio padrão)
     *
     * @param lastState
     * @param matriz
     * @param estados
     * @param desvPred
     * @param passo
     * @return Preco
     */
    public Preco getPrecoSintetico(Estado lastState, double[][] matriz, ArrayList<Estado> estados, double desvPred, int passo) {
        int indiceEstado = lastState.getNome() - 1;
        double maior = matriz[indiceEstado][0];
        int indiceProxEstado = 0;
        double aleatorioGerado = this.aleatorio.nextDouble();

        for (int j = 1; j < matriz.length; j++) {
            if (matriz[indiceEstado][j] > maior) { // pega o estado com maior chance deste transitar
                maior = matriz[indiceEstado][j];
                indiceProxEstado = j;
            }
        }
        
        
        // 
        
        // o que gera a pertubação para tentar fugir de loops eternos é a divisão pela probabilidade de transição para o estado futuro
            // quanto maior, mais provavel de "fator" ser um número dentro do estado encontrtado - quanto menor, "fator" + inicio irá "fugir" desse estado
        double fator = (aleatorioGerado * desvPred)/maior;
        double valorNoIntervFuturo = fator + estados.get(indiceProxEstado).getInicio();
        
        double preco = valorNoIntervFuturo; 
        Preco precoFuturo = new Preco(preco);
        return precoFuturo;
    }

    /**
     * Calcula o preço sintetico jogando um valor aleatório no vetor de
     * distribuição acumulada do passo em questão, escolhendo o estado futuro
     * como a primeira posição do vetor acc que for menor que o valor aleatório.
     * Retorna o valor inicial desse estado + (valor aleatório * desvio padrão)
     *
     * @param distribuicaoAccMi
     * @param estados
     * @param desvPred
     * @return precoSintetico
     */
    public Preco criarPrecoSintetico(ArrayList<Double> distribuicaoAccMi, ArrayList<Estado> estados, double desvPred) {
        double precoSintetico = 0.0;

        double aleatorioGerado = this.aleatorio.nextDouble();
        for (int j = 0; j < distribuicaoAccMi.size(); j++) {
            if (aleatorioGerado < distribuicaoAccMi.get(j)) {
                double fator = aleatorioGerado * desvPred;
                precoSintetico = fator + estados.get(j).getInicio();
                break;
            }
        }
        Preco precoFuturo = new Preco(precoSintetico);
        return precoFuturo;
    }


    /*public void precoProjetado(double distribuicaoAnterior[], double distribuicao[], ArrayList<Estado> estados, double desvPred, int qtnDados, int passos) {
        ArrayList<Double> probAcumulada = this.setPobAcumulada(distribuicao);

        double aleatorioGerado = this.aleatorio.nextDouble();
        for (int j = 0; j < probAcumulada.size(); j++) {
            if (aleatorioGerado < probAcumulada.get(j)) {
                double precoFuturo = aleatorioGerado * desvPred;
                break;
            }
        }
        // System.out.println("Preco projetado: " + this.precoSintetico);
        /*for (int j = 0; j < distribuicaoAnterior.length; j++) {
            double valorDisAnterior = Math.round(distribuicaoAnterior[j] * qtnDados * 1000.0) / 1000.0;
            double valorDisAtual = Math.round(distribuicao[j] * qtnDados * 1000.0) / 1000.0;
            if (valorDisAnterior < valorDisAtual) {
                System.out.println("Prox preco projetado está no intervalo [" + estados.get(j).getInicio() + ", " + estados.get(j).getFim() + "]");
            }
        }

    }*/

 /*public ArrayList<Double> setPobAcumulada(double distribuicao[]) {
        ArrayList<Double> probAcumulada = new ArrayList<>();
        double soma = 0;
        for (int i = 0; i < distribuicao.length; i++) {
            soma += distribuicao[i];
            probAcumulada.add(soma);
        }

        return probAcumulada;
    }*/
    /**
     * @return the aleatorio
     */
    public double getAleatorio() {
        return aleatorio.nextDouble();
    }

    /**
     * @param aleatorio the aleatorio to set
     */
    public void setAleatorio(Random aleatorio) {
        this.aleatorio = aleatorio;
    }
}
