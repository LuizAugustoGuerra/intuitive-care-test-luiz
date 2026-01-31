from flask import Flask, jsonify, request, send_file
from flask_cors import CORS
import mysql.connector
import pandas as pd
import io
import matplotlib.pyplot as plt

app = Flask(__name__)
CORS(app)

# --- CORREÇÃO 1: charset='utf8' para tentar arrumar os acentos ---
db_config = {
    'user': 'user',
    'password': 'password',
    'host': 'localhost',
    'database': 'intuitive_care',
    'charset': 'utf8', 
    'use_unicode': True
}

def get_db_connection():
    return mysql.connector.connect(**db_config)

@app.route('/')
def home():
    return "API Online. Use o Frontend na porta 8000."

# --- CORREÇÃO 2: A Rota que Faltava para o Gráfico ---
@app.route('/api/por-uf', methods=['GET'])
def get_por_uf():
    conn = get_db_connection()
    # Pega top 10 estados para o gráfico do Vue
    query = """
    SELECT uf, SUM(valor) as total 
    FROM despesas 
    WHERE uf != 'ND' 
    GROUP BY uf 
    ORDER BY total DESC 
    LIMIT 10
    """
    cursor = conn.cursor(dictionary=True)
    cursor.execute(query)
    resultado = cursor.fetchall()
    conn.close()
    
    # Converter Decimal para Float
    for item in resultado:
        item['total'] = float(item['total'])
        
    return jsonify(resultado)

# --- ROTAS EXISTENTES ---

@app.route('/api/operadoras', methods=['GET'])
def get_operadoras():
    page = int(request.args.get('page', 1))
    limit = int(request.args.get('limit', 10))
    offset = (page - 1) * limit
    
    conn = get_db_connection()
    cursor = conn.cursor(dictionary=True)
    
    query = "SELECT DISTINCT cnpj, razao_social, uf FROM despesas WHERE cnpj IS NOT NULL ORDER BY razao_social LIMIT %s OFFSET %s"
    cursor.execute(query, (limit, offset))
    operadoras = cursor.fetchall()
    
    cursor.execute("SELECT COUNT(DISTINCT cnpj) as total FROM despesas WHERE cnpj IS NOT NULL")
    total = cursor.fetchone()['total']
    conn.close()
    
    return jsonify({
        'data': operadoras,
        'meta': { 'page': page, 'limit': limit, 'total': total, 'total_pages': (total // limit) + 1 }
    })

@app.route('/api/operadoras/<cnpj>', methods=['GET'])
def get_operadora_detalhes(cnpj):
    conn = get_db_connection()
    cursor = conn.cursor(dictionary=True)
    # Filtro > 0 para garantir que o total faça sentido
    query = "SELECT razao_social, cnpj, uf, SUM(valor) as total_gasto FROM despesas WHERE cnpj = %s AND valor > 0 GROUP BY razao_social, cnpj, uf"
    cursor.execute(query, (cnpj,))
    detalhe = cursor.fetchone()
    conn.close()
    
    if detalhe:
        detalhe['total_gasto'] = float(detalhe['total_gasto'])
        return jsonify(detalhe)
    return jsonify({'error': 'Operadora não encontrada'}), 404

# --- CORREÇÃO 3: Limpeza do Histórico (Tirar zeros e negativos) ---
@app.route('/api/operadoras/<cnpj>/despesas', methods=['GET'])
def get_operadora_historico(cnpj):
    conn = get_db_connection()
    cursor = conn.cursor(dictionary=True)
    
    # Adicionei "AND valor > 0" para sumir com os estornos e zeros
    query = """
    SELECT ano, trimestre, valor, uf 
    FROM despesas 
    WHERE cnpj = %s AND valor > 1000 
    ORDER BY ano, trimestre
    """
    cursor.execute(query, (cnpj,))
    historico = cursor.fetchall()
    conn.close()
    
    for h in historico: h['valor'] = float(h['valor'])
    return jsonify(historico)

@app.route('/api/estatisticas', methods=['GET'])
def get_estatisticas():
    conn = get_db_connection()
    cursor = conn.cursor(dictionary=True)
    
    cursor.execute("SELECT SUM(valor) as total FROM despesas")
    total_geral = float(cursor.fetchone()['total'] or 0)
    
    cursor.execute("SELECT AVG(total_op) as media FROM (SELECT SUM(valor) as total_op FROM despesas GROUP BY cnpj) as sub")
    media_por_operadora = float(cursor.fetchone()['media'] or 0)
    
    cursor.execute("SELECT razao_social, SUM(valor) as total FROM despesas GROUP BY razao_social ORDER BY total DESC LIMIT 5")
    top_5 = cursor.fetchall()
    for t in top_5: t['total'] = float(t['total'])
    
    conn.close()
    return jsonify({ 'total_geral': total_geral, 'media_por_operadora': media_por_operadora, 'top_5': top_5 })

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True)