package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;
import java.util.logging.Level;

import model.Empresa;

public class EmpresaDAO {
	private static final Logger logger = Logger.getLogger(EmpresaDAO.class.getName());
	private Connection conn;

	public EmpresaDAO(Connection conn) {
		this.conn = conn;
	}

	public void cadastrar(Empresa empresa) throws SQLException {
		String sql = "INSERT INTO empresa (id, nomerazao, apelidofantasia, cpfcnpj, bairro, cep, cidade, pais, estado, logradouro, contato01, contato02, email, emailfinanceiro, ativo) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, empresa.getId());
			ps.setString(2, empresa.getNomeRazao());
			ps.setString(3, empresa.getApelidoFantasia());
			ps.setString(4, empresa.getCpfCnpj());
			ps.setString(5, empresa.getBairro());
			ps.setString(6, empresa.getCep());
			ps.setString(7, empresa.getCidade());
			ps.setString(8, empresa.getPais());
			ps.setString(9, empresa.getEstado());
			ps.setString(10, empresa.getLogradouro());
			ps.setString(11, empresa.getContato01());
			ps.setString(12, empresa.getContato02());
			ps.setString(13, empresa.getEmail());
			ps.setString(14, empresa.getEmailFinanceiro());
			ps.setBoolean(15, true);
			ps.executeUpdate();
			logger.info("Empresa cadastrada com sucesso!");
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Erro ao cadastrar empresa", e);
			throw e;
		}
	}

	public void atualizar(Empresa empresa) {
		String sql = "UPDATE empresa SET nomerazao = ?, apelidofantasia = ?, cpfcnpj = ?, "
				+ "bairro = ?, cep = ?, cidade = ?, pais = ?, estado = ?, logradouro = ?, "
				+ "contato01 = ?, contato02 = ?, email = ?, emailfinanceiro = ? WHERE id = ?";

		try (PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setString(1, empresa.getNomeRazao());
			ps.setString(2, empresa.getApelidoFantasia());
			ps.setString(3, empresa.getCpfCnpj());
			ps.setString(4, empresa.getBairro());
			ps.setString(5, empresa.getCep());
			ps.setString(6, empresa.getCidade());
			ps.setString(7, empresa.getPais());
			ps.setString(8, empresa.getEstado());
			ps.setString(9, empresa.getLogradouro());
			ps.setString(10, empresa.getContato01());
			ps.setString(11, empresa.getContato02());
			ps.setString(12, empresa.getEmail());
			ps.setString(13, empresa.getEmailFinanceiro());
			ps.setString(14, empresa.getId());

			ps.executeUpdate();
			logger.info("Empresa atualizada com sucesso!");
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Erro ao atualizar empresa", e);
		}
	}

	public List<Empresa> listar() throws SQLException {
		List<Empresa> empresas = new ArrayList<>();
		String sql = "SELECT * FROM empresa ORDER BY nomerazao";

		try (PreparedStatement ps = conn.prepareStatement(sql);
			 ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				Empresa empresa = new Empresa();
				empresa.setId(rs.getString("id"));
				empresa.setNomeRazao(rs.getString("nomerazao"));
				empresa.setApelidoFantasia(rs.getString("apelidofantasia"));
				empresa.setCpfCnpj(rs.getString("cpfcnpj"));
				empresa.setBairro(rs.getString("bairro"));
				empresa.setCep(rs.getString("cep"));
				empresa.setCidade(rs.getString("cidade"));
				empresa.setPais(rs.getString("pais"));
				empresa.setEstado(rs.getString("estado"));
				empresa.setLogradouro(rs.getString("logradouro"));
				empresa.setContato01(rs.getString("contato01"));
				empresa.setContato02(rs.getString("contato02"));
				empresa.setEmail(rs.getString("email"));
				empresa.setEmailFinanceiro(rs.getString("emailfinanceiro"));
				empresa.setActive(rs.getBoolean("ativo"));
				empresas.add(empresa);
			}
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Erro ao listar empresas", e);
			throw e;
		}
		return empresas;
	}

	public Empresa listarPorId(String id) {
		String sql = "SELECT * FROM empresa WHERE id = ?";

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
				Empresa empresa = new Empresa();
				empresa.setId(rs.getString("id"));
				empresa.setNomeRazao(rs.getString("nomerazao"));
				empresa.setApelidoFantasia(rs.getString("apelidofantasia"));
				empresa.setCpfCnpj(rs.getString("cpfcnpj"));
				empresa.setBairro(rs.getString("bairro"));
				empresa.setCep(rs.getString("cep"));
				empresa.setCidade(rs.getString("cidade"));
				empresa.setPais(rs.getString("pais"));
				empresa.setEstado(rs.getString("estado"));
				empresa.setContato01(rs.getString("contato01"));
				empresa.setContato02(rs.getString("contato02"));
				empresa.setEmail(rs.getString("email"));
				empresa.setEmailFinanceiro(rs.getString("emailfinanceiro"));

					return empresa;
				}
			}
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Erro ao buscar empresa por ID", e);
		}
		return null;

	}

	public void alterarStatus(String id, boolean ativo) throws SQLException {
		String sql = "UPDATE empresa SET ativo = ? WHERE id = ?";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setBoolean(1, ativo);
			ps.setString(2, id);
			ps.executeUpdate();
			logger.info("Status da empresa alterado com sucesso!");
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Erro ao alterar status da empresa", e);
			throw e;
		}
	}

	public void inativar(String id) {
		String sql = "UPDATE empresa SET ativo = false WHERE id = ?";

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, id);
			ps.executeUpdate();

			logger.info("Empresa inativada com sucesso!");
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Erro ao inativar empresa", e);
		}
	}

}