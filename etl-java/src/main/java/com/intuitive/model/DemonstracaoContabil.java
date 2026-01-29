package com.intuitive.model;

import com.opencsv.bean.CsvBindByName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
}