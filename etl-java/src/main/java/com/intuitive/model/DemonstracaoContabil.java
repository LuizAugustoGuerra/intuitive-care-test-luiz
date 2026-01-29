package com.intuitive.model;

import com.opencsv.bean.CsvBindByName;
import java.math.BigDecimal; // Importante!

public class DemonstracaoContabil {

    @CsvBindByName(column = "ANO")
    private int ano;

    @CsvBindByName(column = "CNPJ")
    private String cnpj;

    @CsvBindByName(column = "RAZAOSOCIAL")
    private String razaoSocial;

    @CsvBindByName(column = "TRIMESTRE")
    private String trimestre;

    
    @CsvBindByName(column = "VALOR DESPESAS")
    private BigDecimal valor;

   

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public void setRazaoSocial(String razaoSocial) {
        this.razaoSocial = razaoSocial;
    }

    public String getTrimestre() {
        return trimestre;
    }

    public void setTrimestre(String trimestre) {
        this.trimestre = trimestre;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }
}