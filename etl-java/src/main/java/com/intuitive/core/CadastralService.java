package com.intuitive.core;

import com.intuitive.model.Operadora;
import com.opencsv.bean.CsvToBeanBuilder;

import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class CadastralService {

    private static final Logger logger = Logger.getLogger(CadastralService.class.getName());
    private static final String URL_CADASTRO = "https://dadosabertos.ans.gov.br/FTP/PDA/operadoras_de_plano_de_saude_ativas/Relatorio_Cadop.csv";
    
    // Mapa: Chave = Registro ANS, Valor = Objeto Operadora
    private Map<String, Operadora> cacheOperadoras;

    public void carregarDados(Downloader downloader, Path pastaDestino) {
        Path arquivoDestino = pastaDestino.resolve("operadoras.csv");
        
        // Baixar se não existir
        if (!Files.exists(arquivoDestino)) {
            logger.info("Baixando cadastro de operadoras...");
            downloader.downloadFile(URL_CADASTRO, arquivoDestino);
        }

        //Ler e colocar em Memória (HashMap)
        cacheOperadoras = new HashMap<>();
        try (Reader reader = Files.newBufferedReader(arquivoDestino, StandardCharsets.ISO_8859_1)) {
            
            List<Operadora> lista = new CsvToBeanBuilder<Operadora>(reader)
                    .withType(Operadora.class)
                    .withSeparator(';') // ANS padrão
                    .withIgnoreLeadingWhiteSpace(true)
                    .build()
                    .parse();

            for (Operadora op : lista) {
                
                cacheOperadoras.put(op.getRegistroAns(), op);
            }
            
            logger.info("Cadastro de operadoras carregado. Total: " + cacheOperadoras.size());

        } catch (Exception e) {
            logger.severe("Erro ao carregar cadastro: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Operadora buscarPorRegistro(String registroAns) {
        if (cacheOperadoras == null) return null;
        return cacheOperadoras.get(registroAns);
    }
}