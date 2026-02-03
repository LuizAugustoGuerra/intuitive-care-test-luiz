Teste Técnico - Engenharia de Dados e Desenvolvimento Full Stack
Candidato: Luiz Augusto Guerra

Visão Geral do Projeto
Este projeto consiste em uma solução completa de Engenharia de Dados e Desenvolvimento Web, abrangendo desde a extração e transformação de dados (ETL) de fontes públicas da ANS até a visualização analítica em um dashboard interativo.

A arquitetura foi desenhada para garantir integridade de dados financeiros, performance em consultas analíticas e uma experiência de usuário fluida. O sistema é composto por módulos isolados e containerizados para facilitar a reprodutibilidade.

Arquitetura da Solução
O sistema opera em quatro camadas distintas:

Ingestão e Processamento (Java 17): Responsável pelo Web Scraping, download, descompressão e parsing de arquivos CSV da ANS. Utiliza JDBC para persistência otimizada em lote (Batch Insert).

Armazenamento (MySQL 8): Banco de dados relacional estruturado para cargas de trabalho analíticas (OLAP).

Análise e API (Python 3/Flask):

Script de processamento estatístico (Pandas) para cálculos complexos (Desvio Padrão).

API REST para servir dados ao frontend.

Visualização (Vue.js 3): Interface Single Page Application (SPA) para consulta de operadoras e visualização gráfica.

Decisões Técnicas e Trade-offs
Abaixo estão detalhadas as decisões de arquitetura tomadas durante o desenvolvimento, considerando os requisitos de performance, consistência e prazo.

1. Modelagem de Dados (Banco de Dados)
Estratégia: Tabela Desnormalizada (Abordagem orientada a Leitura).

Decisão: Optou-se por consolidar os dados em uma estrutura única, evitando a normalização excessiva (3NF).

Justificativa: O padrão de acesso é majoritariamente analítico (agrupamentos por UF, somas por Operadora e ordenações). A desnormalização elimina a necessidade de JOINs custosos em tempo de leitura, priorizando a performance das consultas do Dashboard em detrimento de uma leve redundância no armazenamento.

Tipagem de Dados:

Valores Monetários (DECIMAL 15,2): Foi preterido o uso de FLOAT ou DOUBLE para evitar erros de precisão em ponto flutuante, garantindo a exatidão dos centavos necessária para dados contábeis.

Datas (VARCHAR vs DATE): Como a granularidade temporal exigida restringe-se ao trimestre (ex: "1T2025") e não a datas específicas, o uso de VARCHAR ou INT para ano/trimestre simplifica as agregações sem a sobrecarga de funções de data do SGBD.

2. Estratégia de ETL (Extração e Carga)
Ferramenta: Java (JDBC Nativo).

Decisão: A carga foi realizada via aplicação Java em vez de ferramentas de linha de comando (LOAD DATA INFILE).

Justificativa:

Tratamento de Encoding: A fonte de dados original utiliza ISO-8859-1 (padrão antigo), enquanto o banco opera em UTF-8. O Java gerencia essa conversão de forma transparente.

Sanitização Numérica: O formato brasileiro de moeda (vírgula como decimal) foi tratado programaticamente para garantir a conversão correta para BigDecimal, prevenindo inserções de dados corrompidos que poderiam passar despercebidos em importações diretas via script.

3. Análise Estatística
Ferramenta: Python (Pandas).

Decisão: O cálculo de Desvio Padrão e a geração de arquivos agregados (CSV/ZIP) foram delegados ao Python, em vez de SQL puro ou Java.

Justificativa: A biblioteca Pandas oferece operações vetorizadas altamente otimizadas para estatística descritiva. Implementar cálculos de desvio padrão manualmente em Java seria verboso e menos eficiente, enquanto em SQL exigiria queries complexas de difícil manutenção.

4. API e Backend
Framework: Flask.

Decisão: Escolha do Flask em detrimento do FastAPI ou Django.

Justificativa: Dada a natureza direta dos requisitos (servir JSONs de consultas SQL), o Flask oferece a menor sobrecarga de configuração ("boilerplate"). O FastAPI seria uma alternativa válida, mas sua camada de validação (Pydantic) adicionaria complexidade desnecessária para um esquema de dados já validado na etapa de ETL.

Paginação:

Estratégia: Offset-based (LIMIT / OFFSET).

Justificativa: Para interfaces de administração tabular onde o usuário necessita saltar para páginas específicas, a paginação por offset é a implementação mais direta e compatível com componentes de UI padrão.

Estratégia de Cache:

Decisão: Consultas em tempo real (Sem Cache).

Justificativa: O volume de dados (~200.000 registros) é processado pelo MySQL na ordem de milissegundos para as queries indexadas criadas. A introdução de uma camada de cache (Redis) aumentaria a complexidade da infraestrutura sem ganho perceptível de performance para o usuário final neste cenário.

5. Frontend e Interface
Tecnologia: Vue.js (Standalone/CDN).

Decisão: Utilização do Vue.js diretamente no navegador, sem etapa de build (Webpack/Vite).

Justificativa: Reduz drasticamente a complexidade do ambiente de desenvolvimento, eliminando a necessidade de gerenciamento de pacotes (node_modules). Para o escopo da aplicação, esta abordagem entrega reatividade completa com performance nativa.

Busca e Filtro:

Estratégia: Filtragem no Cliente (Client-side).

Justificativa: Aplicada sobre os dados da página atual ou conjuntos limitados, a busca no cliente oferece feedback instantâneo ao usuário (UX), eliminando a latência de rede a cada tecla pressionada (debounce).

Guia de Execução
Pré-requisitos
Docker e Docker Compose

Java JDK 17+ e Maven (para execução local do ETL)

Python 3.9+ (para execução local da API)

1. Inicialização do Banco de Dados
O ambiente utiliza contêineres para garantir isolamento.

Bash
docker-compose up -d
Isso provisionará o MySQL na porta 3306.

2. Execução do ETL (Carga de Dados)
Navegue até o diretório do módulo Java e execute o pipeline.

Bash
cd etl-java
mvn clean compile exec:java -Dexec.mainClass="com.intuitive.Main"
Resultado: Os dados serão baixados, processados e inseridos na tabela despesas.

3. Geração de Estatísticas
Execute o script Python para gerar os arquivos analíticos solicitados.

Bash
cd backend
pip install -r requirements.txt
python main.py
Resultado: Geração do arquivo Teste_LuizAugustoGuerra.zip.

4. Inicialização da API e Dashboard
Inicie o servidor Flask para expor os dados e a interface visual.

Bash
cd backend
python server.py
Acesse o dashboard em: http://localhost:5000 (ou na porta 8000 se servido separadamente).

Estrutura do Repositório
/etl-java: Código fonte Java (Crawler, Parser CSV, Conexão JDBC).

/backend: Código fonte Python (API Flask, Script Estatístico Pandas).

/frontend: Código fonte da interface (HTML/Vue.js).

/data: Diretório reservado para arquivos brutos e processados (ignorado pelo Git).

queries.sql: Contém as queries DDL e DML analíticas solicitadas no teste.

docker-compose.yml: Definição de infraestrutura do banco de dados.