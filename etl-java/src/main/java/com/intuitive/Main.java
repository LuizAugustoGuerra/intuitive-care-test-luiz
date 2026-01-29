package com.intuitive;

import com.intuitive.core.Downloader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

public class Main {
    
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        logger.info("INICIANDO SISTEMA ETL");

        Downloader downloader = new Downloader();
        
        String urlReal = "https://dadosabertos.ans.gov.br/FTP/PDA/demonstracoes_contabeis/2025/3T2025.zip";
        Path destino = Paths.get("../data/raw/3T2025.zip");

        logger.info("Tentando baixar: " + urlReal);

        boolean sucesso = downloader.downloadFile(urlReal, destino);

        if (sucesso) {
            logger.info("SUCESSO");
            logger.info("Verifique a pasta 'data/raw'.");
        } else {
            logger.severe("FALHA NO DOWNLOAD");
        }
    }
}