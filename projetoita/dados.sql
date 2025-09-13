CREATE DATABASE IF NOT EXISTS projetoitamar;
USE projetoitamar;

DROP TABLE IF EXISTS produtomovimentacao;
DROP TABLE IF EXISTS produto;
DROP TABLE IF EXISTS pessoa;
DROP TABLE IF EXISTS empresa;
CREATE TABLE empresa (
    id CHAR(36) PRIMARY KEY,
    nomerazao VARCHAR(250),
    apelidofantasia VARCHAR(250),
    cpfcnpj VARCHAR(250),
    bairro VARCHAR(250),
    cep VARCHAR(250),
    cidade VARCHAR(250),
    pais VARCHAR(250),
    estado VARCHAR(250),
    logradouro VARCHAR(1000),
    contato01 VARCHAR(250),
    contato02 VARCHAR(250),
    email VARCHAR(250),
    emailfinanceiro VARCHAR(250),
    ativo BOOLEAN DEFAULT TRUE
);


CREATE TABLE pessoa (
    id VARCHAR(36) PRIMARY KEY,
    email VARCHAR(100) NOT NULL,
    senha VARCHAR(100) NOT NULL,
    iddaempresa VARCHAR(36),
    ativo BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (iddaempresa) REFERENCES empresa(id)
);


CREATE TABLE produto (
    id VARCHAR(36) PRIMARY KEY,
    descricao VARCHAR(1000),
    quantidade INT,
    valor_custo DECIMAL(10,2),
    valor_venda DECIMAL(10,2),
    iddaempresa VARCHAR(36),
    quantidade_estoque INT,
    active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (iddaempresa) REFERENCES empresa(id)
);


CREATE TABLE produtomovimentacao (
    id VARCHAR(36) PRIMARY KEY,
    id_produto VARCHAR(36),
    iddaempresa VARCHAR(36),
    quantidade_movimentada INT,
    valor_unitario DECIMAL(10,2),
    tipo_movimentacao ENUM('compra', 'venda', 'Compra', 'Venda'),
    tipo ENUM('Entrada', 'Saida'),
    active BOOLEAN DEFAULT TRUE,
    data_movimentacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_produto) REFERENCES produto(id),
    FOREIGN KEY (iddaempresa) REFERENCES empresa(id)
);


INSERT INTO empresa (id, nomerazao, cpfcnpj, cidade, estado, ativo) 
VALUES ('admin-empresa-id', 'Empresa Administradora', '00.000.000/0001-00', 'Cidade Admin', 'Estado Admin', TRUE);

INSERT INTO pessoa (id, email, senha, iddaempresa, ativo) 
VALUES ('admin-user-id', 'admin', '03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4', 'admin-empresa-id', TRUE);

