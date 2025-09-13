package servlet;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import controller.DashboardController;
import controller.EmpresaController;
import controller.LoginController;
import controller.ProdutoController;
import controller.ProdutoMovimentacaoController;
import controller.UsuarioController;

public class App extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(App.class.getName());
	
	private String sanitizeForLog(String input) {
		if (input == null) return "null";
		return input.replaceAll("[\r\n]", "_").replaceAll("[^a-zA-Z0-9_-]", "_");
	}

	public App() {
		super();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html");

		String acao = request.getParameter("action");

		EmpresaController empresaController = new EmpresaController();
		LoginController loginController = new LoginController();
		DashboardController dashboardController = new DashboardController();
		UsuarioController usuarioController = new UsuarioController();
		ProdutoController produtoController = new ProdutoController();
		ProdutoMovimentacaoController movimentacaoController = new ProdutoMovimentacaoController();

		switch (acao != null ? acao : "") {
		case "cadastrarEmpresaForm":
			empresaController.cadastrarEmpresaForm(request, response);
			break;
		case "empresaCadastrada":
			empresaController.empresaCadastrada(request, response);
			break;
		case "listarEmpresas":
			empresaController.listarEmpresas(request, response);
			break;
		case "login":
			loginController.loginForm(request, response);
			break;
		case "dashboard":
			dashboardController.dashboard(request, response);
			break;
		case "cadastrarUsuarioForm":
			usuarioController.cadastrarForm(request, response);
			break;
		case "listarUsuarios":
			usuarioController.listarUsuarios(request, response);
			break;
		case "cadastrarProdutoForm":
			produtoController.cadastrarForm(request, response);
			break;
		case "listarProdutos":
			produtoController.listarProdutos(request, response);
			break;
		case "movimentarProdutoForm":
			movimentacaoController.movimentarForm(request, response);
			break;
		case "listarMovimentacoes":
			movimentacaoController.listarMovimentacoes(request, response);
			break;
		case "dashboardProduto":
			produtoController.dashboard(request, response);
			break;
		case "logout":
			request.getSession().invalidate();
			response.sendRedirect("?action=login");
			break;
		default:
			response.sendRedirect("?action=login");
			break;
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String acao = request.getParameter("action");
		logger.info("POST recebido com action: " + (acao != null ? sanitizeForLog(acao) : "null"));
		
		EmpresaController empresaController = new EmpresaController();
		LoginController loginController = new LoginController();
		UsuarioController usuarioController = new UsuarioController();
		ProdutoController produtoController = new ProdutoController();
		ProdutoMovimentacaoController movimentacaoController = new ProdutoMovimentacaoController();

		switch (acao != null ? acao : "") {
		case "cadastrarEmpresa":
			empresaController.cadastrarEmpresa(request, response);
			break;
		case "loginUsuario":
			loginController.loginUsuario(request, response);
			break;
		case "cadastrarUsuario":
			usuarioController.cadastrarUsuario(request, response);
			break;
		case "cadastrarProduto":
			produtoController.cadastrarProduto(request, response);
			break;
		case "movimentarProduto":
			movimentacaoController.movimentarProduto(request, response);
			break;
		default:
			logger.log(Level.WARNING, "Ação POST não reconhecida: " + sanitizeForLog(acao));
			response.getWriter().println("Ação POST não reconhecida");
		}
	}
}