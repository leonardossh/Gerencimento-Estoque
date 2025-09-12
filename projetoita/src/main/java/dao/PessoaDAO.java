package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

import model.Pessoa;

public class PessoaDAO {
	private static final Logger logger = Logger.getLogger(PessoaDAO.class.getName());
	private Connection conn;

	public PessoaDAO(Connection conn) {
		this.conn = conn;
	}

	public void cadastrar(Pessoa pessoa) throws SQLException {
		String sql = "INSERT INTO pessoa (id, email, senha, iddaempresa) VALUES (?, ?, ?, ?)";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, pessoa.getId());
			ps.setString(2, pessoa.getEmail());
			ps.setString(3, pessoa.getSenha());
			ps.setString(4, pessoa.getIdDaEmpresa());
			ps.executeUpdate();
			logger.info("Pessoa cadastrada com sucesso!");
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Erro ao cadastrar pessoa", e);
			throw e;
		}
	}

	public void atualizar(Pessoa usuario) {
		String sql = "UPDATE pessoa SET email = ?, senha = ?, iddaempresa = ? WHERE id = ?";

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, usuario.getEmail());
			ps.setString(2, usuario.getSenha());
			ps.setString(3, usuario.getIdDaEmpresa());
			ps.setString(4, usuario.getId());
			ps.executeUpdate();

			logger.info("Usuário atualizado com sucesso!");
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Erro ao atualizar usuário", e);
		}
	}

	public List<Pessoa> listar() throws SQLException {
		List<Pessoa> usuarios = new ArrayList<>();
		String sql = "SELECT p.*, e.nomerazao as empresa_nome FROM pessoa p " +
					 "LEFT JOIN empresa e ON p.iddaempresa = e.id ORDER BY p.email";

		try (PreparedStatement ps = conn.prepareStatement(sql);
			 ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				Pessoa u = new Pessoa();
				u.setId(rs.getString("id"));
				u.setEmail(rs.getString("email"));
				u.setSenha(rs.getString("senha"));
				u.setIdDaEmpresa(rs.getString("iddaempresa"));
				u.setNomeEmpresa(rs.getString("empresa_nome"));
				u.setAtivo(rs.getBoolean("ativo"));
				usuarios.add(u);
			}
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Erro ao listar pessoas", e);
			throw e;
		}
		return usuarios;
	}

	public Pessoa listarPorId(String id) {
		String sql = "SELECT * FROM pessoa WHERE id = ?";

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, id);
			try (ResultSet rs = ps.executeQuery()) {

			if (rs.next()) {
				Pessoa u = new Pessoa();
				u.setId(rs.getString("id"));
				u.setEmail(rs.getString("email"));
				u.setSenha(rs.getString("senha"));
				u.setIdDaEmpresa(rs.getString("iddaempresa"));

					return u;
				}
			}
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Erro ao buscar usuário por ID", e);
		}
		return null;
	}

	public void alterarStatus(String id, boolean ativo) throws SQLException {
		String sql = "UPDATE pessoa SET ativo = ? WHERE id = ?";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setBoolean(1, ativo);
			ps.setString(2, id);
			ps.executeUpdate();
			logger.info("Status do usuario alterado com sucesso!");
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Erro ao alterar status do usuario", e);
			throw e;
		}
	}

	public void inativar(String id) {
		String sql = "UPDATE pessoa SET ativo = false WHERE id = ?";

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, id);
			ps.executeUpdate();

			logger.info("Usuário inativado com sucesso!");
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Erro ao inativar usuário", e);
		}
	}

	public Pessoa buscarPorEmailESenha(String email, String senha) throws SQLException {
		String sql = "SELECT * FROM pessoa WHERE email = ? AND senha = ? AND ativo = TRUE";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, email);
			ps.setString(2, senha);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					Pessoa u = new Pessoa();
					u.setId(rs.getString("id"));
					u.setEmail(rs.getString("email"));
					u.setSenha(rs.getString("senha"));
					u.setIdDaEmpresa(rs.getString("iddaempresa"));
					return u;
				}
			}
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Erro ao buscar pessoa por email e senha", e);
			throw e;
		}
		return null;
	}


}
