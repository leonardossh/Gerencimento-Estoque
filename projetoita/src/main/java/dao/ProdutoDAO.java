package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

import model.Produto;

public class ProdutoDAO {
	private static final Logger logger = Logger.getLogger(ProdutoDAO.class.getName());
	private Connection conn;

	public ProdutoDAO(Connection conn) {
		this.conn = conn;
	}

	public void cadastrar(Produto produto) throws SQLException {
		String sql = "INSERT INTO produto (id, descricao, quantidade, valor_custo, valor_venda, iddaempresa, quantidade_estoque, active) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, produto.getId());
			ps.setString(2, produto.getDescricao());
			ps.setInt(3, Integer.parseInt(produto.getQuantidade()));
			ps.setBigDecimal(4, new java.math.BigDecimal(produto.getValorCusto()));
			ps.setBigDecimal(5, new java.math.BigDecimal(produto.getValorVenda()));
			ps.setString(6, produto.getIdEmpresa());
			ps.setInt(7, Integer.parseInt(produto.getQuantidadeEstoque()));
			ps.setBoolean(8, produto.getActive());
			ps.executeUpdate();
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Erro ao cadastrar produto", e);
			throw e;
		}
	}

	public List<Produto> listar() throws SQLException {
		List<Produto> lista = new ArrayList<>();
		String sql = "SELECT * FROM produto";
		try (PreparedStatement ps = conn.prepareStatement(sql);
			 ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				Produto p = new Produto();
				p.setId(rs.getString("id"));
				p.setDescricao(rs.getString("descricao"));
				p.setQuantidade(String.valueOf(rs.getInt("quantidade")));
				p.setValorCusto(rs.getBigDecimal("valor_custo").toString());
				p.setValorVenda(rs.getBigDecimal("valor_venda").toString());
				p.setIdEmpresa(rs.getString("iddaempresa"));
				p.setQuantidadeEstoque(String.valueOf(rs.getInt("quantidade_estoque")));
				p.setActive(rs.getBoolean("active"));
				lista.add(p);
			}
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Erro ao listar produtos", e);
			throw e;
		}
		return lista;
	}
}