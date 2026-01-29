package com.intuitive.core;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.logging.Logger;
import java.util.logging.Level;

public class Downloader {

    // Criamos o logger manualmente
    private static final Logger logger = Logger.getLogger(Downloader.class.getName());
    private final HttpClient client;

    public Downloader() {
        this.client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(20))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }

    public boolean downloadFile(String url, Path destination) {
        try {
            logger.info("Iniciando download de: " + url);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

            if (response.statusCode() != 200) {
                logger.warning("Falha ao baixar (HTTP " + response.statusCode() + "): " + url);
                return false;
            }

            if (destination.getParent() != null) {
                Files.createDirectories(destination.getParent());
            }

            Files.copy(response.body(), destination, StandardCopyOption.REPLACE_EXISTING);
            
            logger.info("Download concluído: " + destination.getFileName());
            return true;

        } catch (IOException | InterruptedException e) {
            logger.log(Level.SEVERE, "Erro ao baixar arquivo", e);
            Thread.currentThread().interrupt();
            return false;
        }
    }
}