package controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.logging.Level;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import connection.DatabaseConnection;
import dao.EmpresaDAO;
import dao.PessoaDAO;
import model.Empresa;
import model.Pessoa;
import util.HashUtil;
import view.HtmlForm;
import view.HtmlPage;

public class UsuarioController {
	private static final Logger logger = Logger.getLogger(UsuarioController.class.getName());

	public void cadastrarForm(HttpServletRequest request, HttpServletResponse response) throws IOException {
		HtmlPage page = new HtmlPage("Cadastro de Usuário", request.getContextPath());
		HtmlForm form = new HtmlForm("post", "?action=cadastrarUsuario");
		form.addRawHtml("<h1>Cadastrar Usuário</h1>");

		form.addInput("E-mail", "email", "text");
		form.addInput("Senha", "senha", "password");

		try (Connection conn = DatabaseConnection.getConnection()) {
			EmpresaDAO empresaDAO = new EmpresaDAO(conn);
			List<Empresa> empresas = empresaDAO.listar();
			
			if (empresas.isEmpty()) {
				form.addRawHtml("<div class='error-msg'>Nenhuma empresa cadastrada. <a href='?action=cadastrarEmpresaForm'>Cadastre uma empresa primeiro</a></div>");
				form.addButton("Cadastrar");
				page.addToBody(form.render());
				response.getWriter().println(page.render());
				return;
			}

			StringBuilder selectHtml = new StringBuilder();
			selectHtml.append("<div class='form-group'>");
			selectHtml.append("<label>Empresa:</label>");
			selectHtml.append("<select name='iddaempresa' required>");
			selectHtml.append("<option value=''>Selecione uma empresa</option>");
			for (Empresa empresa : empresas) {
				selectHtml.append("<option value='").append(escapeHtml(empresa.getId())).append("'>").append(escapeHtml(empresa.getNomeRazao()))
						.append("</option>");
			}
			selectHtml.append("</select>");
			selectHtml.append("</div>");

			form.addRawHtml(selectHtml.toString());

		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Erro ao carregar empresas", e);
			form.addRawHtml("<p style='color: red;'>Erro ao carregar empresas</p>");
		}

		form.addButton("Cadastrar");
		page.addToBody(form.render());
		response.getWriter().println(page.render());
	}

	public void cadastrarUsuario(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String email = request.getParameter("email");
		String senha = request.getParameter("senha");
		String idEmpresa = request.getParameter("iddaempresa");
		
		logger.info("Tentativa de cadastro - Email: " + email + ", IdEmpresa: " + idEmpresa);
		
		if (email == null || email.isBlank()) {
			try {
				response.sendRedirect("?action=cadastrarUsuarioForm&error=" + java.net.URLEncoder.encode("Email é obrigatório.", "UTF-8"));
			} catch (Exception e) {
				response.getWriter().println("Erro de validação");
			}
			return;
		}
		
		if (senha == null || senha.isBlank()) {
			try {
				response.sendRedirect("?action=cadastrarUsuarioForm&error=" + java.net.URLEncoder.encode("Senha é obrigatória.", "UTF-8"));
			} catch (Exception e) {
				response.getWriter().println("Erro de validação");
			}
			return;
		}
		
		if (idEmpresa == null || idEmpresa.isBlank()) {
			try {
				response.sendRedirect("?action=cadastrarUsuarioForm&error=" + java.net.URLEncoder.encode("Empresa é obrigatória.", "UTF-8"));
			} catch (Exception e) {
				response.getWriter().println("Erro de validação");
			}
			return;
		}
		
		Pessoa usuario = new Pessoa();
		usuario.setId(UUID.randomUUID().toString());
		usuario.setEmail(email);
		try {
			usuario.setSenha(HashUtil.sha256(senha));
		} catch (RuntimeException e) {
			logger.log(Level.SEVERE, "Erro ao gerar hash SHA-256", e);
			try {
				response.sendRedirect("?action=cadastrarUsuarioForm&error=" + java.net.URLEncoder.encode("Erro ao processar senha. Tente novamente.", "UTF-8"));
			} catch (Exception ex) {
				response.getWriter().println("Erro ao processar senha");
			}
			return;
		}
		usuario.setIdDaEmpresa(idEmpresa);

		try (Connection conn = DatabaseConnection.getConnection()) {
			PessoaDAO dao = new PessoaDAO(conn);
			dao.cadastrar(usuario);
			response.sendRedirect("?action=dashboard&success=" + java.net.URLEncoder.encode("Usuário cadastrado com sucesso!", "UTF-8"));
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Erro ao cadastrar usuário", e);
			String errorMsg = "Erro ao cadastrar usuário";
			if (e.getMessage().contains("Duplicate entry") && e.getMessage().contains("email")) {
				errorMsg = "Este email já está cadastrado. Use outro email.";
			} else if (e.getMessage().contains("Duplicate entry")) {
				errorMsg = "Dados duplicados. Verifique as informações.";
			} else {
				errorMsg = "Erro no banco de dados: " + e.getMessage();
			}
			try {
				response.sendRedirect("?action=cadastrarUsuarioForm&error=" + java.net.URLEncoder.encode(errorMsg, "UTF-8"));
			} catch (Exception ex) {
				response.getWriter().println("Erro ao cadastrar usuário");
			}
		}
	}

	public void listarUsuarios(HttpServletRequest request, HttpServletResponse response) throws IOException {
		HtmlPage page = new HtmlPage("Lista de Usuarios", request.getContextPath());
		

		HttpSession session = request.getSession(false);
		Pessoa usuario = (session != null) ? (Pessoa) session.getAttribute("usuarioLogado") : null;
		boolean isAdmin = usuario != null && "admin".equals(usuario.getEmail());
		
		try (Connection conn = DatabaseConnection.getConnection()) {
			PessoaDAO dao = new PessoaDAO(conn);
			List<Pessoa> lista = dao.listar();

			StringBuilder html = new StringBuilder();
			html.append("<div class='container'><div class='card'>");
			html.append("<h1>Usuarios Cadastrados</h1>");

			if (lista.isEmpty()) {
				html.append("<p>Nenhum usuario cadastrado.</p>");
			} else {
				html.append("<table class='table'>");
				html.append("<thead><tr><th>Email</th><th>Empresa</th><th>Status</th><th>Acoes</th></tr></thead>");
				html.append("<tbody>");
				
				for (Pessoa u : lista) {
					String rowClass = u.getAtivo() ? "" : "inactive-row";
					html.append("<tr class='").append(rowClass).append("'>");
					html.append("<td>").append(escapeHtml(u.getEmail())).append("</td>");
					html.append("<td>").append(escapeHtml(u.getNomeEmpresa() != null ? u.getNomeEmpresa() : "-")).append("</td>");
					html.append("<td><span class='status-badge ").append(u.getAtivo() ? "active" : "inactive").append("'>");
					html.append(u.getAtivo() ? "Ativo" : "Inativo").append("</span></td>");
					html.append("<td>");
					if (isAdmin) {
						if (u.getAtivo()) {
							html.append("<a href='?action=inativarUsuario&id=").append(u.getId()).append("' class='btn-inativar'>Inativar</a>");
						} else {
							html.append("<a href='?action=ativarUsuario&id=").append(u.getId()).append("' class='btn-ativar'>Ativar</a>");
						}
					} else {
						html.append("-");
					}
					html.append("</td></tr>");
				}
				
				html.append("</tbody></table>");
			}

			html.append("<style>");
			html.append(".table { width: 100%; border-collapse: collapse; margin-top: 20px; }");
			html.append(".table th, .table td { padding: 10px; text-align: left; border-bottom: 1px solid #ddd; }");
			html.append(".table th { background: #f8f9fa; font-weight: bold; }");
			html.append(".inactive-row { background-color: #ffebee; }");
			html.append(".status-badge { padding: 4px 8px; border-radius: 12px; font-size: 0.8rem; font-weight: bold; }");
			html.append(".status-badge.active { background: #d4edda; color: #155724; }");
			html.append(".status-badge.inactive { background: #f8d7da; color: #721c24; }");
			html.append(".btn-ativar { background: #28a745; color: white; padding: 5px 10px; border-radius: 4px; text-decoration: none; font-size: 0.8rem; }");
			html.append(".btn-inativar { background: #dc3545; color: white; padding: 5px 10px; border-radius: 4px; text-decoration: none; font-size: 0.8rem; }");
			html.append("</style>");

			html.append("</div></div>");
			page.addToBody(html.toString());
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Erro ao listar usuarios", e);
			page.addToBody("<div class='container'><div class='card'><p>Erro ao listar usuarios.</p></div></div>");
		}

		response.getWriter().println(page.render());
	}
	
	public void ativarUsuario(HttpServletRequest request, HttpServletResponse response) throws IOException {
		HttpSession session = request.getSession(false);
		Pessoa usuario = (session != null) ? (Pessoa) session.getAttribute("usuarioLogado") : null;
		if (usuario == null || !"admin".equals(usuario.getEmail())) {
			response.sendRedirect("?action=listarUsuarios&error=Acesso negado. Apenas administradores podem executar esta acao.");
			return;
		}
		
		String id = request.getParameter("id");
		try (Connection conn = DatabaseConnection.getConnection()) {
			PessoaDAO dao = new PessoaDAO(conn);
			dao.alterarStatus(id, true);
			response.sendRedirect("?action=listarUsuarios&success=Usuario ativado com sucesso!");
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Erro ao ativar usuario", e);
			response.sendRedirect("?action=listarUsuarios&error=Erro ao ativar usuario");
		}
	}

	public void inativarUsuario(HttpServletRequest request, HttpServletResponse response) throws IOException {
		HttpSession session = request.getSession(false);
		Pessoa usuario = (session != null) ? (Pessoa) session.getAttribute("usuarioLogado") : null;
		if (usuario == null || !"admin".equals(usuario.getEmail())) {
			response.sendRedirect("?action=listarUsuarios&error=Acesso negado. Apenas administradores podem executar esta acao.");
			return;
		}
		
		String id = request.getParameter("id");
		try (Connection conn = DatabaseConnection.getConnection()) {
			PessoaDAO dao = new PessoaDAO(conn);
			dao.alterarStatus(id, false);
			response.sendRedirect("?action=listarUsuarios&success=Usuario inativado com sucesso!");
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Erro ao inativar usuario", e);
			response.sendRedirect("?action=listarUsuarios&error=Erro ao inativar usuario");
		}
	}
	
	private String escapeHtml(String input) {
		if (input == null) return "";
		return input.replace("&", "&amp;")
				.replace("<", "&lt;")
				.replace(">", "&gt;")
				.replace("\"", "&quot;")
				.replace("'", "&#x27;")
				.replace("/", "&#x2F;");
	}
}