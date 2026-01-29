package com.intuitive.core;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipExtractor {

    private static final Logger logger = Logger.getLogger(ZipExtractor.class.getName());

    /**
     * Procura dentro do ZIP um arquivo que contenha o termo chave
     * e o extrai para a pasta de destino.
     */
    public boolean extractRelevantCsv(Path zipFile, Path outputDir, String searchTerm) {
        try (InputStream fis = Files.newInputStream(zipFile);
             ZipInputStream zis = new ZipInputStream(fis)) {

            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                
                // Normaliza
                String entryName = entry.getName().toLowerCase();
                
                // Verifica se é um CSV e se tem o termo
                if (entryName.endsWith(".csv") && entryName.contains(searchTerm.toLowerCase())) {
                    
                    logger.info("Arquivo alvo encontrado no ZIP: " + entry.getName());
                    
                    // Define onde salvar
                    Path targetPath = outputDir.resolve(Path.of(entry.getName()).getFileName());
                    
                    // Garante que a pasta existe
                    if (targetPath.getParent() != null) Files.createDirectories(targetPath.getParent());

                    // Copia do stream do ZIP direto para o disco
                    Files.copy(zis, targetPath, StandardCopyOption.REPLACE_EXISTING);
                    
                    logger.info("Extraído para: " + targetPath);
                    return true; 
                }
                
                zis.closeEntry();
            }
            
            logger.warning("Nenhum CSV com o termo '" + searchTerm + "' encontrado em " + zipFile.getFileName());
            return false;

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erro ao processar ZIP: " + zipFile, e);
            return false;
        }
    }
}