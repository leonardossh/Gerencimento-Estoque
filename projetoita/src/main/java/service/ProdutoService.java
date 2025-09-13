package service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import dao.ProdutoDAO;
import model.Produto;
import util.Conexao;

public class ProdutoService {
    
    public void cadastrar(Produto produto) throws SQLException {
        produto.setId(UUID.randomUUID().toString());
        produto.setActive(true);
        
        try (Connection conn = Conexao.getConnection()) {
            ProdutoDAO dao = new ProdutoDAO(conn);
            dao.cadastrar(produto);
        }
    }
    
    public List<Produto> listar() throws SQLException {
        try (Connection conn = Conexao.getConnection()) {
            ProdutoDAO dao = new ProdutoDAO(conn);
            return dao.listar();
        }
    }
    
    public Produto buscarPorId(String id) throws SQLException {
        try (Connection conn = Conexao.getConnection()) {
            ProdutoDAO dao = new ProdutoDAO(conn);
            return dao.buscarPorId(id);
        }
    }
    
    public void atualizar(Produto produto) throws SQLException {
        try (Connection conn = Conexao.getConnection()) {
            ProdutoDAO dao = new ProdutoDAO(conn);
            dao.atualizar(produto);
        }
    }
    
    public void inativar(String id) throws SQLException {
        try (Connection conn = Conexao.getConnection()) {
            ProdutoDAO dao = new ProdutoDAO(conn);
            dao.inativar(id);
        }
    }
}