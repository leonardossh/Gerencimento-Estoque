package controller;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.Pessoa;
import view.HtmlPage;

public class DashboardController {
	private static final Logger logger = Logger.getLogger(DashboardController.class.getName());

	public void dashboard(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String contextPath = request.getContextPath();

		HttpSession session = request.getSession(false);
		Pessoa usuario = (session != null) ? (Pessoa) session.getAttribute("usuarioLogado") : null;

		if (usuario == null) {
			response.sendRedirect("?action=login");
			return;
		}

		HtmlPage page = new HtmlPage("Painel de Controle", contextPath);
		
		// CSS limpo sem dependências externas
		StringBuilder css = new StringBuilder();
		css.append("<style>");
		css.append("body { font-family: Arial, sans-serif; background: #f5f5f5; margin: 0; padding: 20px; }");
		css.append(".dashboard-container { max-width: 800px; margin: 0 auto; background: white; border-radius: 10px; padding: 30px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }");
		css.append(".dashboard-title { text-align: center; color: #333; margin-bottom: 10px; font-size: 28px; }");
		css.append(".welcome-text { text-align: center; color: #666; margin-bottom: 30px; }");
		css.append(".menu-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 15px; margin-bottom: 30px; }");
		css.append(".menu-btn { display: block; padding: 20px; background: #007bff; color: white; text-decoration: none; border-radius: 8px; text-align: center; font-weight: bold; transition: background 0.3s; }");
		css.append(".menu-btn:hover { background: #0056b3; color: white; text-decoration: none; }");
		css.append(".logout-container { text-align: center; }");
		css.append(".logout-btn { display: inline-block; padding: 12px 25px; background: #dc3545; color: white; text-decoration: none; border-radius: 6px; font-weight: bold; }");
		css.append(".logout-btn:hover { background: #c82333; color: white; text-decoration: none; }");
		css.append("@media (max-width: 768px) { .menu-grid { grid-template-columns: 1fr; } }");
		css.append("</style>");
		
		page.addToBody(css.toString());
		page.addToBody("<div class='dashboard-container'>");
		page.addToBody("<h1 class='dashboard-title'>Painel de Controle</h1>");
		page.addToBody("<p class='welcome-text'>Bem-vindo, " + escapeHtml(usuario.getEmail()) + "!</p>");

		page.addToBody("<div class='menu-grid'>");
		page.addToBody("<a href='?action=cadastrarProdutoForm' class='menu-btn'>Cadastrar Produto</a>");
		page.addToBody("<a href='?action=listarProdutos' class='menu-btn'>Listar Produtos</a>");
		page.addToBody("<a href='?action=movimentarProdutoForm' class='menu-btn'>Movimentar Produto</a>");
		page.addToBody("<a href='?action=cadastrarEmpresaForm' class='menu-btn'>Cadastrar Empresa</a>");
		page.addToBody("<a href='?action=listarEmpresas' class='menu-btn'>Listar Empresas</a>");
		page.addToBody("<a href='?action=cadastrarUsuarioForm' class='menu-btn'>Cadastrar Usuario</a>");
		page.addToBody("<a href='?action=listarUsuarios' class='menu-btn'>Listar Usuarios</a>");
		page.addToBody("<a href='?action=listarMovimentacoes' class='menu-btn'>Listar Movimentacoes</a>");
		page.addToBody("</div>");

		page.addToBody("<div class='logout-container'>");
		page.addToBody("<a href='?action=logout' class='logout-btn'>Sair</a>");
		page.addToBody("</div>");
		page.addToBody("</div>");

		response.getWriter().println(page.render());
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
