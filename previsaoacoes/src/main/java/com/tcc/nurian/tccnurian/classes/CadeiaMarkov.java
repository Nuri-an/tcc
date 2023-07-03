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
public class CadeiaMarkov {

    private ArrayList<Estado> estados;
    private MatrizTransicao matrizOriginal;
    private Distribuicao distribuicao;
    private Dados conjuntoDados;

    public CadeiaMarkov() {
        this.conjuntoDados = new Dados();
        this.estados = new ArrayList();

        this.conjuntoDados.lerDadosBaseCSV();
        this.conjuntoDados.calculaValMin();
        this.conjuntoDados.calculaValMax();
        this.conjuntoDados.calculaDesvPadBase();

        this.criarEstados();
        this.inicializarTransicao();
        this.relacionarPrecoEstado();
        this.apagarEstadosVazios();
        this.criarTransicoes();

        this.matrizOriginal = new MatrizTransicao(this.estados.size());
        this.matrizOriginal.criaMatrizInicial(this.estados);
        this.matrizOriginal.imprime(this.matrizOriginal.getMatriz());

        this.distribuicao = new Distribuicao(this.estados.size());
        this.distribuicao.criarDistribuicaoM0(this.estados, this.conjuntoDados.getPrecosFechamento().size());
        this.distribuicao.imprime(this.distribuicao.getDistribuicaoM0());
    }

    /**
     * Cria os estados I[inicio; fim]
     *
     */
    public void criarEstados() {
        boolean parada = false;
        int i = 0;
        double maximo = this.conjuntoDados.getValMax();

        while (!parada) {
            this.estados.add(new Estado());
            this.estados.get(i).setNome(i + 1);
            if (i == 0) {
                this.estados.get(i).setInicio(this.conjuntoDados.getValMin());
            } else {
                this.estados.get(i).setInicio(this.estados.get(i - 1).getFim());
            }

            double fim = this.estados.get(i).getInicio() + this.conjuntoDados.getMetadeDesvPad();

            if (fim > maximo) {
                this.estados.get(i).setFim(maximo);
                parada = true;
                System.out.println("acabou");
            } else {
                this.estados.get(i).setFim(fim);
            }

            i++;
        }
    }

    /**
     * Cada estado In contém um vetor de tamanho igual a quantidade total de
     * estados, representando as transições In para todos os estados (incluindo
     * ele memso). Inicializa as transições de cada estado
     */
    public void inicializarTransicao() {
        for (int i = 0; i < this.estados.size(); i++) {
            this.estados.get(i).inicializarTransicao(this.estados.size());
        }
    }

    /**
     * Para cada valor no conjunto de daos, procura em que estado ele se encaixa
     */
    public void relacionarPrecoEstado() {
        for (int i = 0; i < this.conjuntoDados.getPrecosFechamento().size(); i++) {
            double preco = this.conjuntoDados.getPrecosFechamento().get(i).getPreco();

            for (int j = 0; j < this.estados.size(); j++) {
                int qtnEstadosAnterior = this.estados.get(j).getQtnItens();

                if (j == this.estados.size() - 1) { // último da estdo
                    if (preco >= this.estados.get(j).getInicio() && preco <= this.estados.get(j).getFim()) {
                        this.conjuntoDados.getPrecosFechamento().get(i).setEstado(this.estados.get(j));
                        this.estados.get(j).setQtnItens(qtnEstadosAnterior + 1);
                    }
                    break;
                }
                if (preco >= this.estados.get(j).getInicio() && preco < this.estados.get(j).getFim()) {
                    this.conjuntoDados.getPrecosFechamento().get(i).setEstado(this.estados.get(j));
                    this.estados.get(j).setQtnItens(qtnEstadosAnterior + 1);
                    break;
                }
            }
        }
    }

    /**
     * Apaga estados que não possuem valores do conjunto
     */
    public void apagarEstadosVazios() {
        for (int i = 0; i < this.estados.size(); i++) {
            if (this.estados.get(i).getQtnItens() == 0) {
                for (int j = 0; j < this.estados.size(); j++) {
                    if (j >= i)
                        this.estados.get(j).setNome(j);
                    this.estados.get(j).getTransicao().remove(i);
                }
                this.estados.remove(i);
            }
        }
    }

    /**
     * Criar as transições e conta quantas ocorrencias tem em cada intervalo
     */
    public void criarTransicoes() {
        for (int i = 0; i < this.conjuntoDados.getPrecosFechamento().size(); i++) {
            if (i == this.conjuntoDados.getPrecosFechamento().size() - 1) { // último d lista
                Estado estadoAtual = this.conjuntoDados.getPrecosFechamento().get(i).getEstado();

                int maiorQtnTransicao = 0;
                int maiorQtnTransicaoIndex = 0;
                for (int j = 0; j < estadoAtual.getTransicao().size(); j++) { // busca em seu estado a transição com maior qunatidade de elementos
                    if (j == 0) {
                        maiorQtnTransicao = estadoAtual.getTransicao().get(j).getQtn();
                    } else if (estadoAtual.getTransicao().get(j).getQtn() > maiorQtnTransicao) {
                        maiorQtnTransicao = estadoAtual.getTransicao().get(j).getQtn();
                        maiorQtnTransicaoIndex = j;
                    }
                }

                estadoAtual.getTransicao().get(maiorQtnTransicaoIndex).setQtn(maiorQtnTransicao + 1); // adicionar esse último valor da lista, sem transição, nessa transição com maior número de itens

                break;
            }

            Estado estadoAtual = this.conjuntoDados.getPrecosFechamento().get(i).getEstado();
            Estado estadoSeguinte = this.conjuntoDados.getPrecosFechamento().get(i + 1).getEstado();
            int index = estadoSeguinte.getNome() - 1; // nome de cada estado inicia no 1
            if (estadoAtual.getTransicao().get(index).getNome().isEmpty()) { // transição sem nome
                estadoAtual.getTransicao().get(index).setNome("I" + estadoAtual.getNome() + " -> " + "I" + estadoSeguinte.getNome());
            }
            int qtnAnterior = estadoAtual.getTransicao().get(index).getQtn();
            estadoAtual.getTransicao().get(index).setQtn(qtnAnterior + 1);
        }

        System.out.println("- Criando transições:");
        System.out.println(this.estados.size() + " estados");

        System.out.println("- Transições criadas com sucesso");
    }

    /**
     * Calculará a Matrix_Mi com potenciação -  CM homogenea
     *
     * @param passos
     * @return
    public double[][] getMatrizMi(int passos) {
        double matrizMi[][] = this.matrizOriginal.pontencia(passos);
        System.out.println("Matriz M(" + passos + ")");
        this.matrizOriginal.imprime(matrizMi);
        return matrizMi;
    }
     */

    /**
     * @param preco
     * @return estado
     */
    public Estado buscaEstado(Preco preco) {
        Estado estado = this.estados.get(this.estados.size() - 1);
            for (int j = 0; j < this.estados.size(); j++) {
                if(preco.getPreco() >= this.estados.get(j).getInicio()
                        && preco.getPreco() <= this.estados.get(j).getFim()) {
                    estado = this.estados.get(j);
                }
            }
            
        return estado;
    }

    /**
     * Calculará a Matrix_Mi, depois o vetor_Mi e na sequencia o vetor_Mi_acc
     *
     * @param vetorDistribuicaoMi
     * @param passos
     * @return
     */
    public ArrayList<Double> getDistribuicaoAcc(double [] vetorDistribuicaoMi) {
        // double matrizMi[][] = getMatrizMi(passos);
        
        //double vetorDistribuicaoMi[] = this.distribuicao.getDistribuicaoMi(matrizMi);
        
        // System.out.print("\nVetor M" + passos + ": ");    
        for (int i = 0; i < vetorDistribuicaoMi.length; i++) {
            System.out.print(String.format("%.2f", vetorDistribuicaoMi[i]) + ", ");            
        }
        
        // projeção M(1)
        //return vetorDistribuicaoMi;
        
        // projeção M(2)
        return this.distribuicao.getDistribuicaoAcc(vetorDistribuicaoMi);
    }

    /**
     * @return the states
     */
    public ArrayList<Estado> getEstados() {
        return estados;
    }

    /**
     * @param estados the states to set
     */
    public void setEstados(ArrayList<Estado> estados) {
        this.estados = estados;
    }

    /**
     * @return the matrix
     */
    public MatrizTransicao getMatrix() {
        return matrizOriginal;
    }

    /**
     * @param matrix the matrix to set
     */
    public void setMatrix(MatrizTransicao matrix) {
        this.matrizOriginal = matrix;
    }

    /**
     * @return the distribuicao
     */
    public Distribuicao getDistribuicao() {
        return distribuicao;
    }

    /**
     * @param distribuicao the distribuicao to set
     */
    public void setDistribuicao(Distribuicao distribuicao) {
        this.distribuicao = distribuicao;
    }

    /**
     * @return the conjuntoDados
     */
    public Dados getConjuntoDados() {
        return conjuntoDados;
    }

    /**
     * @param conjuntoDados the conjuntoDados to set
     */
    public void setConjuntoDados(Dados conjuntoDados) {
        this.conjuntoDados = conjuntoDados;
    }

}
