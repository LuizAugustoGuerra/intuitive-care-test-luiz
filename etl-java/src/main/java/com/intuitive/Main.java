package com.intuitive;

import com.intuitive.core.CsvNormalizer;
import com.intuitive.core.Downloader;
import com.intuitive.core.ZipExtractor;
import com.intuitive.model.DemonstracaoContabil;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;

import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class Main {
    
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        logger.info("=== ETL INICIADO ===");

        Downloader downloader = new Downloader();
        ZipExtractor extractor = new ZipExtractor();
        CsvNormalizer normalizer = new CsvNormalizer();
        
        List<String> urls = Arrays.asList(
            "https://dadosabertos.ans.gov.br/FTP/PDA/demonstracoes_contabeis/2025/1T2025.zip",
            "https://dadosabertos.ans.gov.br/FTP/PDA/demonstracoes_contabeis/2025/2T2025.zip",
            "https://dadosabertos.ans.gov.br/FTP/PDA/demonstracoes_contabeis/2025/3T2025.zip"
        );

        Path pastaRaw = Paths.get("../data/raw");
        Path pastaProcessed = Paths.get("../data/processed");
        List<DemonstracaoContabil> todosDados = new ArrayList<>();

        try {
            if (!Files.exists(pastaProcessed)) Files.createDirectories(pastaProcessed);

            //Download e Extração
            for (String url : urls) {
                String nomeArquivo = url.substring(url.lastIndexOf("/") + 1);
                Path arquivoZip = pastaRaw.resolve(nomeArquivo);

                if (downloader.downloadFile(url, arquivoZip)) {
                    extractor.extractRelevantCsv(arquivoZip, pastaProcessed, "csv");
                }
            }

            //Normalização e Consolidação
            try (Stream<Path> paths = Files.walk(pastaProcessed)) {
                paths.filter(Files::isRegularFile)
                     .filter(p -> p.toString().endsWith(".csv"))
                     .forEach(arquivo -> {
                         logger.info("Processando: " + arquivo.getFileName());
                         List<DemonstracaoContabil> dados = normalizer.parse(arquivo);
                         todosDados.addAll(dados);
                         logger.info(" -> Registros extraídos: " + dados.size());
                     });
            }

            //Gerar CSV Final Consolidado
            Path saidaFinal = Paths.get("../data/consolidado_despesas.csv");
            try (Writer writer = Files.newBufferedWriter(saidaFinal)) {
                StatefulBeanToCsv<DemonstracaoContabil> beanToCsv = new StatefulBeanToCsvBuilder<DemonstracaoContabil>(writer)
                        .withSeparator(';') // CSV final com ;
                        .build();
                
                beanToCsv.write(todosDados);
                logger.info("=== CONSOLIDAÇÃO CONCLUÍDA ===");
                logger.info("Arquivo gerado: " + saidaFinal.toAbsolutePath());
                logger.info("Total de linhas: " + todosDados.size());
            }

        } catch (Exception e) {
            logger.severe("Erro fatal no processo: " + e.getMessage());
            e.printStackTrace();
        }
    }
}