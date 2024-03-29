/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tcc.nurian.tccnurian.classes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 *
 * @author nuria
 */
public class Dados {

    private ArrayList<Preco> precosFechamento;
    private ArrayList<Double> precosFuturosReais;
    private double metadeDesvPad;
    private double valMin;
    private double valMax;
    private int qtnTotal;
    public String PATH;

    public Dados() {
        //this.PATH = "C:\\Users\\nuria\\Documents\\NetBeansProjects\\StocksForecast\\src\\assets\\";
        this.PATH = "";
        try {
            String dadosBaseCSV = this.PATH + "dados.csv";
            BufferedReader dadosBaseBr = new BufferedReader(new FileReader(dadosBaseCSV));

            // inicialização dados base
            this.precosFechamento = new ArrayList();
            long linhasDadosBase = dadosBaseBr.lines().count();
            for (int j = 0; j < (int) linhasDadosBase; j++) {
                this.precosFechamento.add(new Preco());
            }
            
            // inicialização dados futuros
            this.precosFuturosReais = new ArrayList();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        this.metadeDesvPad = 0;
        this.valMin = 0;
        this.valMax = 0;
        this.qtnTotal = this.precosFechamento.size();
    }

    /**
     * @return the precosFechamento
     */
    public ArrayList<Preco> getPrecosFechamento() {
        return precosFechamento;
    }

    /**
     * @param precosFechamento the precosFechamento to set
     */
    public void setPrecosFechamento(ArrayList<Preco> precosFechamento) {
        this.precosFechamento = precosFechamento;
    }

    /**
     * @return the precosFuturosReais
     */
    public ArrayList<Double> getPrecosFuturosReais() {
        return precosFuturosReais;
    }

    /**
     * @param precosFuturosReais the precosFuturosReais to set
     */
    public void setPrecosPrecosFuturosReais(ArrayList<Double> precosFuturosReais) {
        this.precosFuturosReais = precosFuturosReais;

    }

    /**
     * @return the desvPad
     */
    public double getMetadeDesvPad() {
        return metadeDesvPad;
    }

    /**
     * @param desvPad the desvPad to set
     */
    public void setMetadeDesvPad(double desvPad) {
        this.metadeDesvPad = desvPad;
    }

    /**
     * @return the valMin
     */
    public double getValMin() {
        return valMin;
    }

    /**
     * @param valMin the valMin to set
     */
    public void setValMin(double valMin) {
        this.valMin = valMin;
    }

    /**
     * @return the valMax
     */
    public double getValMax() {
        return valMax;
    }

    /**
     * @param valMax the valMax to set
     */
    public void setValMax(double valMax) {
        this.valMax = valMax;
    }

    /**
     * @return the qtnTotal
     */
    public int getQtnTotal() {
        return qtnTotal;
    }

    /**
     * @param qtnTotal the qtnTotal to set
     */
    public void setQtnTotal(int qtnTotal) {
        this.qtnTotal = qtnTotal;
    }

    /**
     * Lê o arquivo de dados de dados base. Formatação (csv): um valor em cada
     * linha; núemros decimais separados por vírgula
     */
    public void lerDadosBaseCSV() {
        BufferedReader br = null;
        String linha;
        try {
            int i = 0;
            br = new BufferedReader(new FileReader("dados.csv"));
            linha = br.readLine();
            while (linha != null) {
                // TODO: nova base de dados B3 - formatação: dois últimos dígitos são centavos -> 2754 é 27.54
                String centavos = linha.substring(linha.length() - 2);
                double pregao = Double.parseDouble(linha.substring(0, linha.lastIndexOf(centavos)) + "." + centavos);
                this.precosFechamento.get(i).setPreco(pregao);
                if(pregao == 0.37)
                    System.err.println(i);
                i++;
                linha = br.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("- Arquivo lido com sucesso!");
    }

    /**
     * Lê o arquivo de dados de dados futuros reais. Formatação (csv): um valor
     * em cada linha; núemros decimais separados por vírgula
     */
    public void lerDadosFuturosReaisCSV() {
        BufferedReader br = null;
        String linha;
        try {
            br = new BufferedReader(new FileReader("real.csv"));
            linha = br.readLine();
            while (linha != null) {
                String centavos = linha.substring(linha.length() - 2);
                double pregao = Double.parseDouble(linha.substring(0, linha.lastIndexOf(centavos)) + "." + centavos);
                this.precosFuturosReais.add(pregao);

                linha = br.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("- Arquivo lido com sucesso!");
    }
    
    /**
     * Escreve um aquivo cvs com os dados formatados, para a criação dos gráficos
     * @param nome
     * @param dados
     */
    public void escreverDadosEmCSV(String nome, ArrayList<Double> dados) {
        try (PrintWriter writer = new PrintWriter(new File(nome + ".csv"))) {
            
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < dados.size(); j++) {
                String precoFormatado = dados.get(j).toString().replace(".", ",");
                sb.append(precoFormatado);
                sb.append('\n');
            }
            
            writer.write(sb.toString());
            writer.close();
            System.out.println("done!");

        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Calcula o media de uma lista de dados
     *
     * @param listaDados
     * @return double
     */
    public double calculaMedia(ArrayList<Double> listaDados) {
        float somaPrecos = 0;

        for (int i = 0; i < listaDados.size(); i++) {
            somaPrecos += listaDados.get(i);
        }

        double media = somaPrecos / listaDados.size();

        return media;
    }

    /**
     * Calcula o desvio padrão de uma lista de dados
     *
     * @param listaDados
     * @return double
     */
    public double calculaDesvPad(ArrayList<Double> listaDados) {
        double media = calculaMedia(listaDados);

        double desvPad = 0;
        for (int i = 0; i < listaDados.size(); i++) {
            desvPad += Math.pow(listaDados.get(i) - media, 2);
        }

        desvPad = desvPad / listaDados.size();
        if (desvPad > 0) {
            desvPad = Math.sqrt(desvPad);
        }

        return desvPad;
    }

    /**
     * Calcula o desvio padrão dos dados base - métrica para calcular o intevalo de
     * valores para os estados
     */
    public void calculaDesvPadBase() {
        ArrayList<Double> precoDadosBase = new ArrayList();
        
        for (int i = 0; i < this.precosFechamento.size(); i++) {
            precoDadosBase.add(this.precosFechamento.get(i).getPreco());
        }
        
        this.metadeDesvPad = calculaDesvPad(precoDadosBase)*0.5;
        System.err.println("Desvio padrao base: " + this.metadeDesvPad);
    }
    
    /**
     * Calcula a mediana de uma lista de dados
     *
     * @param listaDados
     * @return double
     */
    public double calculaMediana(ArrayList<Double> listaDados) {
        ArrayList<Double> listaOrganizada = new ArrayList<Double>(listaDados.stream().sorted().collect(Collectors.toList()));
        int qtn = listaOrganizada.size();
        double mediana = 0.0;
        if (qtn % 2 == 0) { // par
            double central1 = listaOrganizada.get((qtn/2) - 1); // n/1 (considerando o 0)
            double central2 = listaOrganizada.get(qtn/2); // (n/2)+1 (considerando o 0)
            mediana = (central1 + central2) / 2;
        } else {
            mediana = listaOrganizada.get((qtn - 1)/2);
        }
            
        return mediana;
    }
    
    /**
     * Calcula o COEFICIENTE DE ASSIMETRIA DE PEARSON (skewness) de uma lista de dados
     *
     * @param listaDados
     * @return double
     */
    public double calculaSkewness(ArrayList<Double> listaDados) {
        double media = this.calculaMedia(listaDados);
        double mediana = this.calculaMediana(listaDados);
        double desvPad = this.calculaDesvPad(listaDados);
        
        double skewness = (3*(media-mediana))/desvPad;
        BigDecimal skewnessBig = new BigDecimal(skewness);      
        
        if((skewnessBig.compareTo(new BigDecimal(0.15)) == 1 // 0.15 < val
                && skewness <= 1) // val < 1
            || (skewnessBig.compareTo(new BigDecimal(-0.15)) == -1 // val < -0.15
                && skewness >= -1)) // -1 < val
            System.out.print("Assimetria moderada");        
        
        else if(Math.abs(skewness) > 1)
            System.out.print("Assimetria forte");
        
        else if((new BigDecimal(Math.abs(skewness))).compareTo(new BigDecimal(0.15)) == -1) // 0 < val < 0.15
            System.out.print("Simétrica");            
        
        if(skewness < 0) System.out.print(" à esquerda");
        else System.out.print(" à direita");
        
        return skewness;
    }
    
    /**
     * Calcula o sample kurtosis de uma lista de dados
     *
     * @param listaDados
     * @return double
     */
    public double calculaKurtosis(ArrayList<Double> listaDados) {
        double media = this.calculaMedia(listaDados);
        double potenciaDesvPad = Math.pow(this.calculaDesvPad(listaDados), 4);
        double sumMedia = 0.0;
        
        for (int i = 0; i < listaDados.size(); i++) {
            sumMedia += Math.pow(listaDados.get(i) - media, 4);
        }
        
        double kurtosis = sumMedia/(listaDados.size() * potenciaDesvPad);
            
        return kurtosis;
    }
    
    
    /**
     * Calcula o erro absoluto médio de uma lista de dados
     *
     * @param listaDadosReal
     * @param listaDadosSintentico
     * @return double
     */
    public double calculaMAE(ArrayList<Double> listaDadosReal, ArrayList<Double> listaDadosSintentico) {
        double sumDif = 0.0;
        
        for (int i = 0; i < listaDadosReal.size(); i++) {
            sumDif += Math.abs(listaDadosSintentico.get(i) - listaDadosReal.get(i));
        }
           
        double mae = sumDif / listaDadosReal.size();
        
        return mae;
    }
    
    
    /**
     * Calcula o erro quadrado médio de uma lista de dados
     *
     * @param listaDadosReal
     * @param listaDadosSintentico
     * @return double
     */
    public double calculaMSE(ArrayList<Double> listaDadosReal, ArrayList<Double> listaDadosSintentico) {
        double sumDifPow = 0.0;
        
        for (int i = 0; i < listaDadosReal.size(); i++) {
            sumDifPow += Math.pow(listaDadosReal.get(i) - listaDadosSintentico.get(i), 2);
        }
            
        double mse = sumDifPow / listaDadosReal.size();
        
        return mse;
    }

    /**
     * Encontra o valor mínimo do conjunto de dados base
     */
    public void calculaValMin() {
        for (int i = 0; i < this.precosFechamento.size(); i++) {
            if (i == 0) {
                this.valMin = this.precosFechamento.get(i).getPreco();
            }
            if (this.precosFechamento.get(i).getPreco() < this.valMin) {
                this.valMin = this.precosFechamento.get(i).getPreco();
            }
        }
        System.out.println("- Valor minimio concluido: " + this.valMin);
    }

    /**
     * Encontra o valor máximo do conjunto de dados base
     */
    public void calculaValMax() {
        for (int i = 0; i < this.precosFechamento.size(); i++) {
            if (i == 0) {
                this.valMax = this.precosFechamento.get(i).getPreco();
            }
            if (this.precosFechamento.get(i).getPreco() > this.valMax) {
                this.valMax = this.precosFechamento.get(i).getPreco();
            }
        }
        System.out.println("- Valor máximo concluido: " + this.valMax);
    }
}
