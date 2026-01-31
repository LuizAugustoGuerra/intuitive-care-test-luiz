import mysql.connector
import pandas as pd
import zipfile
import os

db_config = {
    'user': 'user',
    'password': 'password',
    'host': 'localhost',
    'database': 'intuitive_care',
    'raise_on_warnings': True
}

def gerar_relatorio_estatistico():
    print("--- Iniciando Processamento Estatístico (Python) ---")
    
    try:
        conn = mysql.connector.connect(**db_config)
        
        query = "SELECT razao_social, uf, trimestre, valor FROM despesas"
        df = pd.read_sql(query, conn)
        
        print(f"Dados carregados: {len(df)} registros.")

        df['valor'] = df['valor'].astype(float)

       
        grupo = df.groupby(['razao_social', 'uf'])

        df_total = grupo['valor'].sum().reset_index(name='total_despesas')

       
        df_media = grupo['valor'].mean().reset_index(name='media_trimestral')

        df_std = grupo['valor'].std().fillna(0).reset_index(name='desvio_padrao')

        resultado = pd.merge(df_total, df_media, on=['razao_social', 'uf'])
        resultado = pd.merge(resultado, df_std, on=['razao_social', 'uf'])

        resultado = resultado.sort_values(by='total_despesas', ascending=False)

        resultado['total_despesas'] = resultado['total_despesas'].round(2)
        resultado['media_trimestral'] = resultado['media_trimestral'].round(2)
        resultado['desvio_padrao'] = resultado['desvio_padrao'].round(2)

        print("Cálculos finalizados. Exemplo do Top 3:")
        print(resultado.head(3))

        csv_filename = "despesas_agregadas.csv"
        resultado.to_csv(csv_filename, index=False, sep=';', decimal=',')
        print(f"Arquivo gerado: {csv_filename}")

        zip_filename = "Teste_LuizAugustoGuerra.zip"
        with zipfile.ZipFile(zip_filename, 'w', zipfile.ZIP_DEFLATED) as zf:
            zf.write(csv_filename)
        
        print(f"SUCESSO! Arquivo pronto para entrega: {zip_filename}")
        
        if os.path.exists(csv_filename):
            os.remove(csv_filename)

    except Exception as e:
        print(f"ERRO FATAL: {e}")
    finally:
        if 'conn' in locals() and conn.is_connected():
            conn.close()

if __name__ == "__main__":
    gerar_relatorio_estatistico()