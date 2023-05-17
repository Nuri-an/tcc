package com.tcc.nurian.tccnurian;

import com.tcc.nurian.tccnurian.classes.CadeiaMarkov;
import com.tcc.nurian.tccnurian.classes.Estado;
import com.tcc.nurian.tccnurian.classes.MatrizTransicao;
import com.tcc.nurian.tccnurian.classes.Preco;
import com.tcc.nurian.tccnurian.classes.Projecao;
import com.tcc.nurian.tccnurian.classes.Transicao;
import java.util.ArrayList;

/**
 *
 * @author jose
 */
public class TccNurian {

    public static void main(String[] args) {
        CadeiaMarkov mkc = new CadeiaMarkov();
        Projecao projecao = new Projecao();
        double[] distribuicao_anterior = mkc.getDistribuicao().getDistribuicaoM0();

        ArrayList<Double> lst = new ArrayList();
        MatrizTransicao matrizM0 = mkc.getMatrix();
        ArrayList<MatrizTransicao> lstMatrizes = new ArrayList();
        lstMatrizes.add(matrizM0);

        int passos = 90;
        int listSize = mkc.getConjuntoDados().getPrecosFechamento().size();
        Estado lastState = mkc.getConjuntoDados().getPrecosFechamento().get(listSize - 1).getEstado();
        double lastPrice = mkc.getConjuntoDados().getPrecosFechamento().get(listSize - 1).getPreco();
        
        for (int i = 1; i < passos + 1; i++) {
            double matrizResultado[][] = matrizM0.getMatriz();

            // multiplica as diferentes matrizes da cadeia até o tempo n
            for (int k = 1; k < lstMatrizes.size(); k++) {
                double produtoMatrizes[][] = matrizM0.produto(matrizResultado, lstMatrizes.get(k).getMatriz());
                matrizResultado = produtoMatrizes;
            }

            // multiplica o vetor M0 pelo resultado da multiplicação das matrizes
            double[] vetorDistribuicaoMi = mkc.getDistribuicao().getDistribuicaoMi(matrizResultado);

            // projeção M(1) - Nurian
            Preco precoFuturo = projecao.precoSintetico(lastPrice, vetorDistribuicaoMi, distribuicao_anterior, mkc.getStates(), mkc.getConjuntoDados().getDesvPad());
            lastPrice = precoFuturo.getPreco();
            
            // projeção M(2) - Zé
            //ArrayList<Double> vetorDistribuicaoAccMi = mkc.getDistribuicaoAcc(vetorDistribuicaoMi);
            //Preco precoFuturo = projecao.criarPrecoSintetico(vetorDistribuicaoAccMi, mkc.getStates(), mkc.getConjuntoDados().getDesvPad());
            
            // projeção do preço futuro com M(3) - Nurian novo
            //Preco precoFuturo = projecao.getPrecoSintetico(lastState, lstMatrizes.get(lstMatrizes.size() - 1).getMatriz(), mkc.getStates(), mkc.getConjuntoDados().getDesvPad(), i);
            
            precoFuturo.setEstado(mkc.buscaEstado(precoFuturo)); // refatora o estado futuro, caso o valor, acressido da inflação, passe o limite
            
            int estadoAtualIndice = lastState.getNome() - 1;
            Estado estadoDestino = mkc.getStates().get(precoFuturo.getEstado().getNome() - 1);
            int estadoDestinoIndice = estadoDestino.getNome() - 1;
            Transicao transicao = lastState.getTransicao().get(estadoDestinoIndice);

            // atualiza quantidades considerando a transição para o novo estado
            estadoDestino.setQtnItens(estadoDestino.getQtnItens() + 1);
            transicao.setQtn(transicao.getQtn() + 1);
            double qtnItensEstados = lastState.getQtnItens();

            //  cria uma nova matriz com a probabilidade do estado atual para os demais diferente
            MatrizTransicao newMatriz = lstMatrizes.get(lstMatrizes.size() - 1);            
            for (int j = 0; j < newMatriz.getMatriz().length; j++) {
                double qtnTransicoes = lastState.getTransicao().get(j).getQtn();
                double probTransicao = qtnTransicoes / qtnItensEstados;
                
                newMatriz.getMatriz()[estadoAtualIndice][j] = probTransicao;
            }

            
            lstMatrizes.add(newMatriz);
            lst.add(precoFuturo.getPreco());
            distribuicao_anterior = vetorDistribuicaoMi;
            lastState = precoFuturo.getEstado();            
        }

        imprime(lst);
        mkc.getConjuntoDados().lerDadosFuturosReaisCSV();
        ArrayList<Double> dadosReais = new ArrayList<Double>(mkc.getConjuntoDados().getPrecosFuturosReais().subList(0, passos));

        mkc.getConjuntoDados().escreverDadosEmCSV("vivt/vivt-dados_reais_" + passos + "dias_formatado", dadosReais);
        mkc.getConjuntoDados().escreverDadosEmCSV("vivt/vivt-dados_sinteticos_" + passos + "dias_formatado", lst);

        System.out.println("\t\t Real \t \t Sintético");

        double mediaReal = mkc.getConjuntoDados().calculaMedia(dadosReais);
        double mediaSintetica = mkc.getConjuntoDados().calculaMedia(lst);
        System.out.println("Média: \t"
                + mediaReal
                + " \t \t"
                + mediaSintetica);

        double medianaReal = mkc.getConjuntoDados().calculaMediana(dadosReais);
        double medianaSintetico = mkc.getConjuntoDados().calculaMediana(lst);
        System.out.println("Mediana: \t"
                + medianaReal
                + " \t \t"
                + medianaSintetico);

        double desvPrevReal = mkc.getConjuntoDados().calculaDesvPad(dadosReais);
        double desvPrevSintetico = mkc.getConjuntoDados().calculaDesvPad(lst);
        System.out.println("Desvio Padrão: \t"
                + desvPrevReal
                + " \t \t"
                + desvPrevSintetico);

        // Grau de assimetria, quanto a média está afastada da mediana.
        System.out.print("Assimetria: \t" );
        double skewnessReal = mkc.getConjuntoDados().calculaSkewness(dadosReais);
        System.out.print("\t\t" );
        double skewnessSintetico = mkc.getConjuntoDados().calculaSkewness(lst);
        System.out.println("\nSkewness: \t"
                + skewnessReal
                + " \t \t"
                + skewnessSintetico);

        // verificar melhor
        double kurtosisReal = mkc.getConjuntoDados().calculaKurtosis(dadosReais);
        double kurtosisSintetico = mkc.getConjuntoDados().calculaKurtosis(lst);
        System.out.println("Kurtosis: \t"
                + kurtosisReal
                + " \t \t"
                + kurtosisSintetico);

        // calcula erro absoluto medio
        double mae = mkc.getConjuntoDados().calculaMAE(dadosReais, lst);
        System.out.println("Erro absoluto medio: \t" + mae);

        // calcula erro quadrado medio
        double mse = mkc.getConjuntoDados().calculaMSE(dadosReais, lst);
        System.out.println("Erro quadrado medio: \t" + mse);

        // calcula raíz do erro quadrado medio
        double rmse = Math.sqrt(mse);
        System.out.println("Raíz do erro quadrado medio: \t" + rmse);

    }

    public static void imprime(ArrayList<Double> lst) {
        System.out.print("\n\nLista Sintetica: [");
        for (Double d : lst) {
            System.out.print(String.format("%.2f", d) + "; ");
        }
        System.out.println("]");

        System.out.println("\n________________________________________________________________\n");
    }
}
