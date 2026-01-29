package com.intuitive.core;

import com.intuitive.model.Operadora;
import com.opencsv.bean.CsvToBeanBuilder;

import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class CadastralService {

    private static final Logger logger = Logger.getLogger(CadastralService.class.getName());
    
    // Mapa em memória: Chave = Registro ANS, Valor = Objeto Operadora
    private Map<String, Operadora> cacheOperadoras;

    public void carregarDados(Downloader downloader, Path pastaDestino) {
        
        Path arquivoDestino = pastaDestino.resolve("Relatorio_cadop.csv");
        
        // Arquivo teve que ser baixado manualmente, caso nao tenha aparece o erro
        if (!Files.exists(arquivoDestino)) {
            logger.warning("ATENÇÃO: Arquivo 'Relatorio_cadop.csv' não encontrado em " + pastaDestino);
            logger.warning("Por favor, coloque o arquivo na pasta data/raw manualmente.");
            return;
        }

        cacheOperadoras = new HashMap<>();
      
        try (Reader reader = Files.newBufferedReader(arquivoDestino, StandardCharsets.ISO_8859_1)) {
            
            List<Operadora> lista = new CsvToBeanBuilder<Operadora>(reader)
                    .withType(Operadora.class)
                    .withSeparator(';') 
                    .withIgnoreLeadingWhiteSpace(true)
                    .build()
                    .parse();

            for (Operadora op : lista) {
                
                if (op.getRegistroAns() != null) {
                    String registroLimpo = op.getRegistroAns().replace("\"", "").trim();
                    cacheOperadoras.put(registroLimpo, op);
                }
            }
            
            logger.info("Cadastro carregado com sucesso! Total de operadoras: " + cacheOperadoras.size());

        } catch (Exception e) {
            logger.severe("Erro ao ler cadastro: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Operadora buscarPorRegistro(String registroAns) {
        if (cacheOperadoras == null) return null;
        // Tenta buscar direto ou removendo zeros à esquerda
        Operadora op = cacheOperadoras.get(registroAns);
        if (op == null && registroAns.startsWith("0")) {
             op = cacheOperadoras.get(registroAns.replaceFirst("^0+(?!$)", ""));
        }
        return op;
    }
}