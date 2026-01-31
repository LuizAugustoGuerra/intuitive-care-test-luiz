CREATE DATABASE IF NOT EXISTS intuitive_care;
USE intuitive_care;

-- 3.2. DDL - Criação da Tabela
CREATE TABLE IF NOT EXISTS despesas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    ano INT NOT NULL,
    trimestre VARCHAR(10) NOT NULL,
    cnpj VARCHAR(20),
    razao_social VARCHAR(255),
    uf VARCHAR(2),
    valor DECIMAL(15,2),
    INDEX idx_uf (uf),
    INDEX idx_razao (razao_social),
    INDEX idx_trimestre (trimestre)
);

-- 3.3. Importação de Dados
-- A importação é realizada via Aplicação Java (Batch Insert) para garantir integridade e tratamento de encoding.
-- Comando de exemplo caso fosse via SQL puro:
/*
LOAD DATA INFILE '/var/lib/mysql-files/consolidado_despesas.csv'
INTO TABLE despesas
FIELDS TERMINATED BY ';'
LINES TERMINATED BY '\n'
IGNORE 1 ROWS
(ano, trimestre, cnpj, razao_social, uf, @valor_br)
SET valor = REPLACE(@valor_br, ',', '.');
*/

-- 3.4. Queries Analíticas

-- Query 1: Top 5 operadoras com maior crescimento percentual (1T vs 3T)
SELECT 
    t1.razao_social,
    t1.valor AS valor_t1,
    t3.valor AS valor_t3,
    ROUND(((t3.valor - t1.valor) / t1.valor) * 100, 2) AS crescimento_pct
FROM 
    (SELECT razao_social, SUM(valor) as valor FROM despesas WHERE trimestre = '1T2025' GROUP BY razao_social) t1
JOIN 
    (SELECT razao_social, SUM(valor) as valor FROM despesas WHERE trimestre = '3T2025' GROUP BY razao_social) t3
ON t1.razao_social = t3.razao_social
ORDER BY crescimento_pct DESC
LIMIT 5;

-- Query 2: Distribuição por UF (Top 5 totais) + Média por Operadora
SELECT 
    uf,
    SUM(valor) AS total_despesas_estado,
    AVG(valor) AS media_despesas_por_registro
FROM despesas
WHERE uf IS NOT NULL AND uf != 'ND'
GROUP BY uf
ORDER BY total_despesas_estado DESC
LIMIT 5;

-- Query 3: Operadoras com despesas acima da média em pelo menos 2 trimestres
WITH MediasPorTrimestre AS (
    SELECT trimestre, AVG(valor) as media_global
    FROM despesas
    GROUP BY trimestre
),
DespesasOperadora AS (
    SELECT razao_social, trimestre, SUM(valor) as total_op
    FROM despesas
    GROUP BY razao_social, trimestre
)
SELECT 
    d.razao_social
FROM DespesasOperadora d
JOIN MediasPorTrimestre m ON d.trimestre = m.trimestre
WHERE d.total_op > m.media_global
GROUP BY d.razao_social
HAVING COUNT(*) >= 2;