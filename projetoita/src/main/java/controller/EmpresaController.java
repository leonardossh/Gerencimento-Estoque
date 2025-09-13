package controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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

public class EmpresaController {
	private static final Logger logger = Logger.getLogger(EmpresaController.class.getName());

	public void cadastrarEmpresaForm(HttpServletRequest request, HttpServletResponse response) throws IOException {
		HtmlPage page = new HtmlPage("Cadastro de Empresa", request.getContextPath());
		HtmlForm form = new HtmlForm("post", "?action=cadastrarEmpresa");

		form.addRawHtml("<h1>Cadastro de Empresa</h1>");
		

		form.addInput("Nome/Razão Social", "nomeRazao", "text");
		form.addInput("Apelido Fantasia", "apelidoFantasia", "text", false);
		form.addInput("CPF/CNPJ", "cpfCnpj", "text");
		

		form.addInput("Logradouro", "logradouro", "text", false);
		form.addInput("Bairro", "bairro", "text", false);
		form.addInput("CEP", "cep", "text", false);
		form.addInput("Cidade", "cidade", "text");
		form.addInput("Estado", "estado", "text");
		form.addInput("País", "pais", "text", false);
		

		form.addInput("Telefone 1", "contato01", "tel", false);
		form.addInput("Telefone 2", "contato02", "tel", false);
		form.addInput("E-mail da Empresa", "email", "email", false);
		form.addInput("E-mail Financeiro", "emailFinanceiro", "email", false);
		
		form.addRawHtml("<hr><h3>Dados do Administrador</h3>");
		form.addInput("E-mail do Administrador", "emailAdmin", "email");
		form.addInput("Senha do Administrador", "senha", "password");

		form.addButton("Cadastrar Empresa");

		page.addToBody(form.render());

		response.setContentType("text/html; charset=UTF-8");
		response.getWriter().println(page.render());
	}

	public void cadastrarEmpresa(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String senhaAdmin = request.getParameter("senha");
		logger.info("Processando cadastro de empresa");

		String nomeRazao = request.getParameter("nomeRazao");
		String apelidoFantasia = request.getParameter("apelidoFantasia");
		String cpfCnpj = request.getParameter("cpfCnpj");
		String logradouro = request.getParameter("logradouro");
		String bairro = request.getParameter("bairro");
		String cep = request.getParameter("cep");
		String cidade = request.getParameter("cidade");
		String estado = request.getParameter("estado");
		String pais = request.getParameter("pais");
		String contato01 = request.getParameter("contato01");
		String contato02 = request.getParameter("contato02");
		String emailEmpresa = request.getParameter("email");
		String emailFinanceiro = request.getParameter("emailFinanceiro");

		String emailAdmin = request.getParameter("emailAdmin");
		String senhaAdmin1 = request.getParameter("senha");

		if (senhaAdmin1 == null || senhaAdmin1.isBlank()) {
			logger.warning("Senha do administrador está vazia");
			try {
				response.sendRedirect("?action=cadastrarEmpresaForm&error=" + java.net.URLEncoder.encode("A senha do administrador é obrigatória.", "UTF-8"));
			} catch (Exception e) {
				response.getWriter().println("Erro de validação");
			}
			return;
		}
		
		if (nomeRazao == null || nomeRazao.isBlank()) {
			logger.warning("Nome/Razão Social é obrigatório");
			try {
				response.sendRedirect("?action=cadastrarEmpresaForm&error=" + java.net.URLEncoder.encode("Nome/Razão Social é obrigatório.", "UTF-8"));
			} catch (Exception e) {
				response.getWriter().println("Erro de validação");
			}
			return;
		}
		
		if (emailAdmin == null || emailAdmin.isBlank()) {
			logger.warning("Email do administrador é obrigatório");
			try {
				response.sendRedirect("?action=cadastrarEmpresaForm&error=" + java.net.URLEncoder.encode("Email do administrador é obrigatório.", "UTF-8"));
			} catch (Exception e) {
				response.getWriter().println("Erro de validação");
			}
			return;
		}

		Empresa empresa = new Empresa();
		empresa.setNomeRazao(nomeRazao);
		empresa.setApelidoFantasia(apelidoFantasia);
		empresa.setCpfCnpj(cpfCnpj);
		empresa.setLogradouro(logradouro);
		empresa.setBairro(bairro);
		empresa.setCep(cep);
		empresa.setCidade(cidade);
		empresa.setEstado(estado);
		empresa.setPais(pais);
		empresa.setContato01(contato01);
		empresa.setContato02(contato02);
		empresa.setEmail(emailEmpresa);
		empresa.setEmailFinanceiro(emailFinanceiro);

		Pessoa admin = new Pessoa();
		admin.setId(java.util.UUID.randomUUID().toString());
		admin.setEmail(emailAdmin);

		try {
			admin.setSenha(HashUtil.sha256(senhaAdmin1));
		} catch (RuntimeException e) {
			logger.log(Level.SEVERE, "Erro ao gerar hash SHA-256", e);
			try {
				response.sendRedirect("?action=cadastrarEmpresaForm&error=" + java.net.URLEncoder.encode("Erro ao processar senha. Tente novamente.", "UTF-8"));
			} catch (Exception ex) {
				response.getWriter().println("Erro ao processar senha");
			}
			return;
		}

		try (Connection conn = DatabaseConnection.getConnection()) {
			logger.info("Conexão estabelecida, iniciando cadastro da empresa");
			
			EmpresaDAO empresaDAO = new EmpresaDAO(conn);
			empresaDAO.cadastrar(empresa);
			logger.info("Empresa cadastrada com ID: " + empresa.getId());

			admin.setIdDaEmpresa(empresa.getId());
			logger.info("Iniciando cadastro do usuário admin");

			PessoaDAO usuarioDAO = new PessoaDAO(conn);
			usuarioDAO.cadastrar(admin);
			logger.info("Usuário admin cadastrado com sucesso");

			response.sendRedirect("?action=login&success=" + java.net.URLEncoder.encode("Empresa cadastrada com sucesso! Faça login com suas credenciais.", "UTF-8"));
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Erro SQL: " + e.getMessage() + " - SQLState: " + e.getSQLState() + " - ErrorCode: " + e.getErrorCode(), e);
			String errorMsg = "Erro ao cadastrar";
			if (e.getMessage().contains("Duplicate entry") && e.getMessage().contains("email")) {
				errorMsg = "Este email já está cadastrado. Use outro email.";
			} else if (e.getMessage().contains("Duplicate entry")) {
				errorMsg = "Dados duplicados. Verifique as informações.";
			} else {
				errorMsg = "Erro no banco de dados: " + e.getMessage();
			}
			response.sendRedirect("?action=cadastrarEmpresaForm&error=" + java.net.URLEncoder.encode(errorMsg, "UTF-8"));
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Erro geral: " + e.getMessage(), e);
			try {
				response.sendRedirect("?action=cadastrarEmpresaForm&error=" + java.net.URLEncoder.encode("Erro inesperado: " + e.getMessage(), "UTF-8"));
			} catch (Exception ex) {
				response.getWriter().println("Erro crítico");
			}
		}
	}

	public void empresaCadastrada(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String contextPath = request.getContextPath();
		HtmlPage page = new HtmlPage("Cadastro Realizado", contextPath);

		page.addToBody("<div class='container'><div class='card'>");
		page.addToBody("<h1>Cadastro realizado com sucesso!</h1>");
		page.addToBody("<p style='text-align: center; color: #4a5568; margin-bottom: 20px;'>Você já pode fazer login com seu usuário administrador.</p>");
		page.addToBody("<div style='text-align: center;'><a href='?action=login' class='btn'>Ir para Login</a></div>");
		page.addToBody("</div></div>");

		response.getWriter().println(page.render());
	}

	public void listarEmpresas(HttpServletRequest request, HttpServletResponse response) throws IOException {
		HtmlPage page = new HtmlPage("Empresas Cadastradas", request.getContextPath());
		

		HttpSession session = request.getSession(false);
		Pessoa usuario = (session != null) ? (Pessoa) session.getAttribute("usuarioLogado") : null;
		boolean isAdmin = usuario != null && "admin".equals(usuario.getEmail());

		try (Connection conn = DatabaseConnection.getConnection()) {
			EmpresaDAO empresaDAO = new EmpresaDAO(conn);
			List<Empresa> empresas = empresaDAO.listar();

			StringBuilder html = new StringBuilder();
			html.append("<div class='container'><div class='card'>");
			html.append("<h1>Empresas Cadastradas</h1>");

			if (empresas.isEmpty()) {
				html.append("<p>Nenhuma empresa cadastrada.</p>");
			} else {
				html.append("<table class='table'>");
				html.append("<thead><tr><th>Nome/Razao</th><th>Nome Fantasia</th><th>CNPJ</th><th>Cidade/Estado</th><th>Telefone</th><th>Email</th><th>Status</th><th>Acoes</th></tr></thead>");
				html.append("<tbody>");
				
				for (Empresa e : empresas) {
					String rowClass = e.getActive() ? "" : "inactive-row";
					html.append("<tr class='").append(rowClass).append("'>");
					html.append("<td>").append(escapeHtml(e.getNomeRazao())).append("</td>");
					html.append("<td>").append(escapeHtml(e.getApelidoFantasia() != null ? e.getApelidoFantasia() : "-")).append("</td>");
					html.append("<td>").append(escapeHtml(e.getCpfCnpj())).append("</td>");
					html.append("<td>").append(escapeHtml(e.getCidade())).append("/").append(escapeHtml(e.getEstado())).append("</td>");
					html.append("<td>").append(escapeHtml(e.getContato01() != null ? e.getContato01() : "-")).append("</td>");
					html.append("<td>").append(escapeHtml(e.getEmail() != null ? e.getEmail() : "-")).append("</td>");
					html.append("<td><span class='status-badge ").append(e.getActive() ? "active" : "inactive").append("'>");
					html.append(e.getActive() ? "Ativa" : "Inativa").append("</span></td>");
					html.append("<td>");
					if (isAdmin) {
						if (e.getActive()) {
							html.append("<a href='?action=inativarEmpresa&id=").append(e.getId()).append("' class='btn-inativar'>Inativar</a>");
						} else {
							html.append("<a href='?action=ativarEmpresa&id=").append(e.getId()).append("' class='btn-ativar'>Ativar</a>");
						}
					} else {
						html.append("-");
					}
					html.append("</td></tr>");
				}
				
				html.append("</tbody></table>");
			}

			html.append("<style>");
			html.append(".container { max-width: 100%; padding: 10px; overflow-x: auto; }");
			html.append(".table { width: 100%; border-collapse: collapse; margin-top: 20px; min-width: 800px; }");
			html.append(".table th, .table td { padding: 10px; text-align: left; border-bottom: 1px solid #ddd; white-space: nowrap; }");
			html.append(".table th { background: #f8f9fa; font-weight: bold; }");
			html.append(".inactive-row { background-color: #ffebee; }");
			html.append(".status-badge { padding: 4px 8px; border-radius: 12px; font-size: 0.8rem; font-weight: bold; }");
			html.append(".status-badge.active { background: #d4edda; color: #155724; }");
			html.append(".status-badge.inactive { background: #f8d7da; color: #721c24; }");
			html.append(".btn-ativar { background: #28a745; color: white; padding: 5px 10px; border-radius: 4px; text-decoration: none; font-size: 0.8rem; }");
			html.append(".btn-inativar { background: #dc3545; color: white; padding: 5px 10px; border-radius: 4px; text-decoration: none; font-size: 0.8rem; }");
			html.append("@media (max-width: 768px) { .container { padding: 5px; } .table { font-size: 0.8rem; min-width: 600px; } .table th, .table td { padding: 5px; } }");
			html.append("</style>");

			html.append("</div></div>");
			page.addToBody(html.toString());
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Erro ao listar empresas", e);
			page.addToBody("<div class='container'><div class='card'><p>Erro ao listar empresas.</p></div></div>");
		}

		response.getWriter().println(page.render());
	}
	
	public void ativarEmpresa(HttpServletRequest request, HttpServletResponse response) throws IOException {
		HttpSession session = request.getSession(false);
		Pessoa usuario = (session != null) ? (Pessoa) session.getAttribute("usuarioLogado") : null;
		if (usuario == null || !"admin".equals(usuario.getEmail())) {
			response.sendRedirect("?action=listarEmpresas&error=Acesso negado. Apenas administradores podem executar esta acao.");
			return;
		}
		
		String id = request.getParameter("id");
		try (Connection conn = DatabaseConnection.getConnection()) {
			EmpresaDAO dao = new EmpresaDAO(conn);
			dao.alterarStatus(id, true);
			response.sendRedirect("?action=listarEmpresas&success=Empresa ativada com sucesso!");
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Erro ao ativar empresa", e);
			response.sendRedirect("?action=listarEmpresas&error=Erro ao ativar empresa");
		}
	}

	public void inativarEmpresa(HttpServletRequest request, HttpServletResponse response) throws IOException {
		HttpSession session = request.getSession(false);
		Pessoa usuario = (session != null) ? (Pessoa) session.getAttribute("usuarioLogado") : null;
		if (usuario == null || !"admin".equals(usuario.getEmail())) {
			response.sendRedirect("?action=listarEmpresas&error=Acesso negado. Apenas administradores podem executar esta acao.");
			return;
		}
		
		String id = request.getParameter("id");
		try (Connection conn = DatabaseConnection.getConnection()) {
			EmpresaDAO dao = new EmpresaDAO(conn);
			dao.alterarStatus(id, false);
			response.sendRedirect("?action=listarEmpresas&success=Empresa inativada com sucesso!");
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Erro ao inativar empresa", e);
			response.sendRedirect("?action=listarEmpresas&error=Erro ao inativar empresa");
		}
	}

	private String escapeHtml(String input) {
		if (input == null) return "";
		return input.replace("&", "&amp;")
				.replace("<", "&lt;")
				.replace(">", "&gt;")
				.replace("\"", "&quot;")
				.replace("'", "&#x27;");
	}
}