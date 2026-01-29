package com.intuitive.model;

import com.opencsv.bean.CsvBindByName;

public class Operadora {

    
    @CsvBindByName(column = "Registro ANS")
    private String registroAns;

    @CsvBindByName(column = "CNPJ")
    private String cnpj;

    @CsvBindByName(column = "Razão Social")
    private String razaoSocial;

    @CsvBindByName(column = "UF")
    private String uf;
    
    @CsvBindByName(column = "Modalidade")
    private String modalidade;

   
    public String getRegistroAns() { return registroAns; }
    public void setRegistroAns(String registroAns) { this.registroAns = registroAns; }

    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }

    public String getRazaoSocial() { return razaoSocial; }
    public void setRazaoSocial(String razaoSocial) { this.razaoSocial = razaoSocial; }
    
    public String getUf() { return uf; }
    public void setUf(String uf) { this.uf = uf; }
    
    public String getModalidade() { return modalidade; }
    public void setModalidade(String modalidade) { this.modalidade = modalidade; }
}