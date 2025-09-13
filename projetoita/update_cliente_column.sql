-- Script para modificar a coluna cliente para armazenar ID do usuário
ALTER TABLE produtomovimentacao ADD COLUMN id_cliente VARCHAR(36);
ALTER TABLE produtomovimentacao ADD FOREIGN KEY (id_cliente) REFERENCES pessoa(id);