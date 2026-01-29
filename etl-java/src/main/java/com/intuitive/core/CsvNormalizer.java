package com.intuitive.core;

import com.intuitive.model.DemonstracaoContabil;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import java.io.Reader;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class CsvNormalizer {

    private static final Logger logger = Logger.getLogger(CsvNormalizer.class.getName());

    // Mapeamento de possíveis nomes para cada coluna
    private static final Map<String, String[]> COLUNA_ALIAS = new HashMap<>();

    static {
        COLUNA_ALIAS.put("DATA", new String[]{"DATA", "DT_REGISTRO", "DT_OPERACAO"});
        COLUNA_ALIAS.put("REG_ANS", new String[]{"REG_ANS", "CD_OPERADORA", "REGISTRO_ANS"});
        COLUNA_ALIAS.put("DESCRICAO", new String[]{"DESCRICAO", "DS_CONTA", "NOM_CONTA"});
        COLUNA_ALIAS.put("VALOR", new String[]{"VL_SALDO_FINAL", "VALOR", "VL_SALDO"});
    }

    public List<DemonstracaoContabil> parse(Path csvFile) {
        List<DemonstracaoContabil> resultados = new ArrayList<>();

        try (Reader reader = Files.newBufferedReader(csvFile)) {
            // Configura o leitor para usar ;
            CSVParser parser = new CSVParserBuilder().withSeparator(';').build();
            CSVReader csvReader = new CSVReaderBuilder(reader).withCSVParser(parser).build();

            String[] header = csvReader.readNext();
            if (header == null) return resultados;

            // Descobre dinamicamente os índices das colunas
            Map<String, Integer> indiceColunas = mapearColunas(header);

            if (!validarColunasObrigatorias(indiceColunas)) {
                logger.warning("Arquivo ignorado (colunas insuficientes): " + csvFile.getFileName());
                return resultados;
            }

            String[] line;
            while ((line = csvReader.readNext()) != null) {
                //filtro linha a linha

                String descricao = line[indiceColunas.get("DESCRICAO")];
                
                // Filtro simples por palavra-chave
                if (descricao != null && (descricao.toUpperCase().contains("EVENTOS") || descricao.toUpperCase().contains("SINISTROS"))) {
                    
                    DemonstracaoContabil demo = criarObjeto(line, indiceColunas);
                    if (demo != null) {
                        resultados.add(demo);
                    }
                }
            }

        } catch (Exception e) {
            logger.severe("Erro ao normalizar CSV " + csvFile + ": " + e.getMessage());
        }

        return resultados;
    }

    private Map<String, Integer> mapearColunas(String[] header) {
        Map<String, Integer> mapa = new HashMap<>();

        for (int i = 0; i < header.length; i++) {
            String colunaAtual = header[i].toUpperCase().trim();

            for (Map.Entry<String, String[]> entry : COLUNA_ALIAS.entrySet()) {
                for (String alias : entry.getValue()) {
                    if (colunaAtual.equals(alias)) {
                        mapa.put(entry.getKey(), i);
                    }
                }
            }
        }
        return mapa;
    }

    private boolean validarColunasObrigatorias(Map<String, Integer> mapa) {
        return mapa.containsKey("DATA") && mapa.containsKey("VALOR") && mapa.containsKey("REG_ANS");
    }

    private DemonstracaoContabil criarObjeto(String[] line, Map<String, Integer> mapa) {
        try {
            DemonstracaoContabil demo = new DemonstracaoContabil();

            //Trata a Data
            String dataStr = line[mapa.get("DATA")];
           
            LocalDate data = LocalDate.parse(dataStr, DateTimeFormatter.ISO_DATE); 
            
            demo.setAno(data.getYear());
            // Calcula trimestre
            int trimestre = (data.getMonthValue() - 1) / 3 + 1;
            demo.setTrimestre(trimestre + "T");

            //trata valor
            String valorStr = line[mapa.get("VALOR")];
            valorStr = valorStr.replace(".", "").replace(",", "."); // Remove ponto de milhar, troca vírgula por ponto
            demo.setValorDespesas(new BigDecimal(valorStr));

            //trata Identificadores
            demo.setRazaoSocial(line[mapa.get("DESCRICAO")]); // Usando descrição como placeholder temporário
            
            
            demo.setCnpj(line[mapa.get("REG_ANS")]); 

            return demo;
        } catch (Exception e) {
            return null;
        }
    }
}