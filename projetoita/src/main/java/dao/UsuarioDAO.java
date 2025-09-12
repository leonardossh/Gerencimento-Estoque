package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

import model.Usuario;
import java.util.UUID;

public class UsuarioDAO {
	private static final Logger logger = Logger.getLogger(UsuarioDAO.class.getName());
	private Connection conn;

	public UsuarioDAO(Connection conn) {
		this.conn = conn;
	}

	public void cadastrar(Usuario usuario) throws SQLException {
		// Gerar ID se não existir
		if (usuario.getId() == null || usuario.getId().isEmpty()) {
			usuario.setId(UUID.randomUUID().toString());
		}
		
		String sql = "INSERT INTO pessoa (id, email, senha, iddaempresa, ativo) VALUES (?, ?, ?, ?, ?)";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, usuario.getId());
			ps.setString(2, usuario.getEmail());
			ps.setString(3, usuario.getSenha());
			ps.setString(4, usuario.getIddaempresa());
			ps.setBoolean(5, usuario.getActive() != null ? usuario.getActive() : true);
			ps.executeUpdate();
			logger.info("Usuário cadastrado com sucesso: " + usuario.getEmail());
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Erro ao cadastrar usuário: " + e.getMessage(), e);
			throw e;
		}
	}

	public List<Usuario> listar() throws SQLException {
		List<Usuario> lista = new ArrayList<>();
		String sql = "SELECT * FROM pessoa";
		try (PreparedStatement ps = conn.prepareStatement(sql);
			 ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				Usuario u = new Usuario();
				u.setId(rs.getString("id"));
				u.setEmail(rs.getString("email"));
				u.setSenha(rs.getString("senha"));
				u.setIddaempresa(rs.getString("iddaempresa"));
				u.setActive(rs.getBoolean("ativo"));
				lista.add(u);
			}
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Erro ao listar usuários", e);
			throw e;
		}
		return lista;
	}
}