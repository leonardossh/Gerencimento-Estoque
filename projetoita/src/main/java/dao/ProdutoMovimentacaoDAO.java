package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import model.ProdutoMovimentacao;

public class ProdutoMovimentacaoDAO {
	private static final Logger logger = Logger.getLogger(ProdutoMovimentacaoDAO.class.getName());
	private Connection conn;

	public ProdutoMovimentacaoDAO(Connection conn) {
		this.conn = conn;
	}

	public void registrarMovimentacao(ProdutoMovimentacao mov) throws SQLException {
		String sql = "INSERT INTO produtomovimentacao "
				+ "(id, id_produto, iddaempresa, quantidade_movimentada, valor_unitario, tipo_movimentacao, tipo, active, id_cliente) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, mov.getId());
			ps.setString(2, mov.getIdProduto());
			ps.setString(3, mov.getId_Empresa());
			ps.setInt(4, mov.getQuantidadeMovimentada());
			ps.setDouble(5, mov.getValorUnitario());
			ps.setString(6, mov.getTipoMovimentacao());
			ps.setString(7, mov.getTipo());
			ps.setBoolean(8, mov.getActive());
			ps.setString(9, mov.getIdCliente());
			ps.executeUpdate();
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Erro ao registrar movimentação", e);
			throw e;
		}

		String updateSql = mov.getTipoMovimentacao().equals("compra")
				? "UPDATE produto SET quantidade_estoque = quantidade_estoque + ? WHERE id = ?"
				: "UPDATE produto SET quantidade_estoque = quantidade_estoque - ? WHERE id = ?";
		try (PreparedStatement ps2 = conn.prepareStatement(updateSql)) {
			ps2.setInt(1, mov.getQuantidadeMovimentada());
			ps2.setString(2, mov.getIdProduto());
			ps2.executeUpdate();
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Erro ao atualizar estoque", e);
			throw e;
		}
	}

	public List<ProdutoMovimentacao> listar() throws SQLException {
		List<ProdutoMovimentacao> lista = new ArrayList<>();
		String sql = "SELECT pm.*, p.descricao as produto_nome, e.nomerazao as empresa_nome, pe.email as cliente_nome " +
					 "FROM produtomovimentacao pm " +
					 "LEFT JOIN produto p ON pm.id_produto = p.id " +
					 "LEFT JOIN empresa e ON pm.iddaempresa = e.id " +
					 "LEFT JOIN pessoa pe ON pm.id_cliente = pe.id " +
					 "ORDER BY pm.data_movimentacao DESC";
		try (PreparedStatement ps = conn.prepareStatement(sql);
			 ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				ProdutoMovimentacao mov = new ProdutoMovimentacao();
				mov.setId(rs.getString("id"));
				mov.setIdProduto(rs.getString("id_produto"));
				mov.setId_Empresa(rs.getString("iddaempresa"));
				mov.setNomeProduto(rs.getString("produto_nome"));
				mov.setNomeEmpresa(rs.getString("empresa_nome"));
				mov.setQuantidadeMovimentada(rs.getInt("quantidade_movimentada"));
				mov.setValorUnitario(rs.getDouble("valor_unitario"));
				mov.setTipoMovimentacao(rs.getString("tipo_movimentacao"));
				mov.setTipo(rs.getString("tipo"));
				mov.setActive(rs.getBoolean("active"));
				mov.setIdCliente(rs.getString("id_cliente"));
				mov.setNomeCliente(rs.getString("cliente_nome"));
				lista.add(mov);
			}
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Erro ao listar movimentações", e);
			throw e;
		}
		return lista;
	}


}