package com.intuitive.core;

import com.intuitive.model.DemonstracaoContabil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class DatabaseService {

    private static final Logger logger = Logger.getLogger(DatabaseService.class.getName());
    
    // Configurações do Banco
    private static final String URL = "jdbc:mysql://localhost:3306/intuitive_care?useSSL=false&allowPublicKeyRetrieval=true";
    private static final String USER = "user";
    private static final String PASS = "password";

    public void inserirDados(List<DemonstracaoContabil> dados) {
        String sql = "INSERT INTO despesas (ano, cnpj, razao_social, trimestre, valor) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            conn.setAutoCommit(false); 
            logger.info("Conectado ao Banco de Dados. Iniciando inserção de " + dados.size() + " registros...");

            int count = 0;
            for (DemonstracaoContabil item : dados) {
                stmt.setInt(1, item.getAno());
                stmt.setString(2, item.getCnpj());
                stmt.setString(3, item.getRazaoSocial());
                stmt.setString(4, item.getTrimestre());
                
                // CORREÇÃO: setBigDecimal em vez de setDouble
                stmt.setBigDecimal(5, item.getValor());

                stmt.addBatch(); 
                count++;

                if (count % 1000 == 0) {
                    stmt.executeBatch();
                    conn.commit();
                    logger.info("Processados: " + count);
                }
            }

            stmt.executeBatch();
            conn.commit();
            logger.info("=== Carga no Banco Finalizada com Sucesso! Total: " + count + " ===");

        } catch (SQLException e) {
            logger.severe("Erro ao inserir no banco: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void limparTabela() {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement("TRUNCATE TABLE despesas")) {
            stmt.execute();
            logger.info("Tabela 'despesas' limpa com sucesso.");
        } catch (SQLException e) {
            logger.warning("Não foi possível limpar a tabela: " + e.getMessage());
        }
    }
}