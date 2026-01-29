package com.intuitive.model;

import com.opencsv.bean.CsvBindByName;
import java.math.BigDecimal;

public class DemonstracaoContabil {

    @CsvBindByName(column = "CNPJ")
    private String cnpj;

    @CsvBindByName(column = "RazaoSocial")
    private String razaoSocial;

    @CsvBindByName(column = "Trimestre")
    private String trimestre;

    @CsvBindByName(column = "Ano")
    private Integer ano;

    @CsvBindByName(column = "Valor Despesas")
    private BigDecimal valorDespesas;

    //para o csv
    public DemonstracaoContabil() {
    }

    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }

    public String getRazaoSocial() { return razaoSocial; }
    public void setRazaoSocial(String razaoSocial) { this.razaoSocial = razaoSocial; }

    public String getTrimestre() { return trimestre; }
    public void setTrimestre(String trimestre) { this.trimestre = trimestre; }

    public Integer getAno() { return ano; }
    public void setAno(Integer ano) { this.ano = ano; }

    public BigDecimal getValorDespesas() { return valorDespesas; }
    public void setValorDespesas(BigDecimal valorDespesas) { this.valorDespesas = valorDespesas; }
}