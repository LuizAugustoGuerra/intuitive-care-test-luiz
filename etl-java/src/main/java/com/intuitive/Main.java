package com.intuitive;

import com.intuitive.core.Downloader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class Main {
    
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        logger.info("BAIXANDO ULTIMOS 3 TRIMESTRES");

        Downloader downloader = new Downloader();
        
        // Lista das URLs
        List<String> urls = Arrays.asList(
            "https://dadosabertos.ans.gov.br/FTP/PDA/demonstracoes_contabeis/2025/1T2025.zip",
            "https://dadosabertos.ans.gov.br/FTP/PDA/demonstracoes_contabeis/2025/2T2025.zip",
            "https://dadosabertos.ans.gov.br/FTP/PDA/demonstracoes_contabeis/2025/3T2025.zip"
        );

        int sucessos = 0;

        for (String url : urls) {
            // Extrai o nome do arquivo da URL
            String nomeArquivo = url.substring(url.lastIndexOf("/") + 1);
            Path destino = Paths.get("../data/raw/" + nomeArquivo);

            if (downloader.downloadFile(url, destino)) {
                sucessos++;
            }
        }

        logger.info("Resumo: " + sucessos + "/" + urls.size() + " arquivos baixados com sucesso.");
    }
}