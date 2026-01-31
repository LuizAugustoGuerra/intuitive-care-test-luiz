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
    
    private static final String URL = "jdbc:mysql://localhost:3306/intuitive_care?useSSL=false&allowPublicKeyRetrieval=true";
    private static final String USER = "user";
    private static final String PASS = "password";

    public void inserirDados(List<DemonstracaoContabil> dados) {
        // CORREÇÃO: Adicionado o campo UF no SQL
        String sql = "INSERT INTO despesas (ano, cnpj, razao_social, uf, trimestre, valor) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            conn.setAutoCommit(false); 
            logger.info("Conectado ao Banco. Iniciando inserção de " + dados.size() + " registros...");

            int count = 0;
            for (DemonstracaoContabil item : dados) {
                stmt.setInt(1, item.getAno());
                stmt.setString(2, item.getCnpj());
                stmt.setString(3, item.getRazaoSocial());
                
                // NOVO: Setando a UF (se for nulo, salvamos como "ND" - Não Definido)
                String ufParaSalvar = (item.getUf() != null) ? item.getUf() : "ND";
                stmt.setString(4, ufParaSalvar);
                
                stmt.setString(5, item.getTrimestre());
                stmt.setBigDecimal(6, item.getValor());

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
            logger.info("=== Carga Finalizada! Total: " + count + " ===");

        } catch (SQLException e) {
            logger.severe("Erro ao inserir: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void limparTabela() {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement("TRUNCATE TABLE despesas")) {
            stmt.execute();
            logger.info("Tabela 'despesas' limpa com sucesso.");
        } catch (SQLException e) {
            logger.warning("Erro ao limpar tabela: " + e.getMessage());
        }
    }
}