package controller;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import connection.DatabaseConnection;
import dao.PessoaDAO;
import model.Pessoa;
import util.HashUtil;
import view.HtmlForm;
import view.HtmlPage;

public class LoginController {
	private static final Logger logger = Logger.getLogger(LoginController.class.getName());

	public void loginForm(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String contextPath = request.getContextPath();
		HtmlPage page = new HtmlPage("Login", contextPath);

		HtmlForm form = new HtmlForm("post", "?action=loginUsuario");
		form.addRawHtml("<h1>Login</h1>");
		form.addInput("Email", "email", "email");
		form.addInput("Senha", "senha", "password");
		form.addButton("Entrar");

		page.addToBody(form.render());


		page.addToBody(
				"<div style='text-align: center; margin-top: 20px;'><p style='color: #4a5568; margin-bottom: 15px;'>Não tem cadastro?</p><a href='?action=cadastrarEmpresaForm' class='menu-item' style='display: inline-block; padding: 15px 30px; margin: 0;'>Cadastrar Nova Empresa</a></div>");

		response.getWriter().println(page.render());
	}

	public void loginUsuario(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String email = request.getParameter("email");
		String senhaParam = request.getParameter("senha");
		
		if (email == null || senhaParam == null || email.trim().isEmpty() || senhaParam.trim().isEmpty()) {
			try {
				response.sendRedirect("?action=login&error=" + java.net.URLEncoder.encode("Email e senha são obrigatórios.", "UTF-8"));
			} catch (Exception e) {
				response.getWriter().println("Erro de validação");
			}
			return;
		}
		
		String senha = HashUtil.sha256(senhaParam);

		try (Connection conn = DatabaseConnection.getConnection()) {
			PessoaDAO usuarioDAO = new PessoaDAO(conn);
			List<Pessoa> usuarios = usuarioDAO.listar();

			for (Pessoa u : usuarios) {
				if (u.getEmail().equals(email) && u.getSenha().equals(senha)) {
					HttpSession session = request.getSession();
					session.setAttribute("usuarioLogado", u);


					response.sendRedirect("?action=dashboard");
					return;
				}
			}


			try {
				response.sendRedirect("?action=login&error=" + java.net.URLEncoder.encode("Email ou senha incorretos.", "UTF-8"));
			} catch (Exception e) {
				response.getWriter().println("Erro de validação");
			}

		} catch (Exception e) {
			logger.log(Level.SEVERE, "Erro ao realizar login", e);
			try {
				response.sendRedirect("?action=login&error=" + java.net.URLEncoder.encode("Erro ao realizar login.", "UTF-8"));
			} catch (Exception ex) {
				response.getWriter().println("Erro crítico");
			}
		}
	}
}
