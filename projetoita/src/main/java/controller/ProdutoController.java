package controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import connection.DatabaseConnection;
import dao.EmpresaDAO;
import dao.ProdutoDAO;
import model.Empresa;
import model.Produto;
import view.HtmlPage;

public class ProdutoController {
	private static final Logger logger = Logger.getLogger(ProdutoController.class.getName());

	public void dashboard(HttpServletRequest request, HttpServletResponse response) throws IOException {
		HtmlPage page = new HtmlPage("Painel de Controle - Estoque", request.getContextPath());
		
		StringBuilder dashboard = new StringBuilder();
		dashboard.append("<div class='dashboard-container'>");
		dashboard.append("<div class='dashboard-card'>");
		dashboard.append("<h1>Painel de Controle</h1>");

		// Seção de Ações Rápidas
		dashboard.append("<div class='section'>");
		dashboard.append("<div class='action-grid'>");
		dashboard.append("<a href='?action=cadastrarProdutoForm' class='action-card add'>");
		dashboard.append("<div class='icon'>+</div>");
		dashboard.append("<div class='title'>Novo Produto</div>");
		dashboard.append("</a>");
		dashboard.append("<a href='?action=listarProdutos' class='action-card view'>");
		dashboard.append("<div class='icon'>■</div>");
		dashboard.append("<div class='title'>Ver Estoque</div>");
		dashboard.append("</a>");
		dashboard.append("</div></div>");
		
		dashboard.append("</div></div>");
		
		// CSS específico do dashboard
		dashboard.append("<style>");
		dashboard.append(".dashboard-container { max-width: 1000px; margin: 0 auto; padding: 20px; }");
		dashboard.append(".dashboard-card { background: rgba(255,255,255,0.95); border-radius: 20px; padding: 40px; box-shadow: 0 20px 40px rgba(0,0,0,0.1); }");
		dashboard.append(".section { margin-bottom: 40px; }");
		dashboard.append(".action-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 20px; }");
		dashboard.append(".action-card { background: linear-gradient(135deg, #667eea, #764ba2); padding: 30px; border-radius: 15px; text-decoration: none; color: white; text-align: center; transition: all 0.3s; display: block; }");
		dashboard.append(".action-card:hover { transform: translateY(-5px); box-shadow: 0 15px 30px rgba(102,126,234,0.4); text-decoration: none; color: white; }");
		dashboard.append(".action-card .icon { font-size: 2.5rem; margin-bottom: 15px; }");
		dashboard.append(".action-card .title { font-size: 1.1rem; font-weight: 600; }");
		dashboard.append(".action-card.add { background: linear-gradient(135deg, #48bb78, #38a169); }");
		dashboard.append(".action-card.view { background: linear-gradient(135deg, #4299e1, #3182ce); }");

		dashboard.append("</style>");
		
		page.addToBody(dashboard.toString());
		response.getWriter().println(page.render());
	}

	public void cadastrarForm(HttpServletRequest request, HttpServletResponse response) throws IOException {
		HtmlPage page = new HtmlPage("Cadastrar Produto", request.getContextPath());
		
		StringBuilder formHtml = new StringBuilder();
		formHtml.append("<div class='container'><div class='card'>");
		formHtml.append("<h1>Novo Produto</h1>");
		formHtml.append("<form method='post' action='?action=cadastrarProduto' class='product-form'>");
		
		// Informações básicas
		formHtml.append("<div class='form-section'>");
		formHtml.append("<h3>Informações Básicas</h3>");
		formHtml.append("<div class='form-group'>");
		formHtml.append("<label>Descrição do Produto</label>");
		formHtml.append("<input type='text' name='descricao' placeholder='Ex: Notebook Dell Inspiron' required>");
		formHtml.append("</div>");
		formHtml.append("<div class='form-group'>");
		formHtml.append("<label>Quantidade</label>");
		formHtml.append("<input type='number' name='quantidade' placeholder='1' min='1' required>");
		formHtml.append("</div></div>");
		
		// Preços
		formHtml.append("<div class='form-section'>");
		formHtml.append("<h3>Preços</h3>");
		formHtml.append("<div class='form-row'>");
		formHtml.append("<div class='form-group'>");
		formHtml.append("<label>Valor de Custo (R$)</label>");
		formHtml.append("<input type='number' name='valorCusto' placeholder='0.00' step='0.01' min='0' required>");
		formHtml.append("</div>");
		formHtml.append("<div class='form-group'>");
		formHtml.append("<label>Valor de Venda (R$)</label>");
		formHtml.append("<input type='number' name='valorVenda' placeholder='0.00' step='0.01' min='0' required>");
		formHtml.append("</div></div></div>");
		
		// Estoque
		formHtml.append("<div class='form-section'>");
		formHtml.append("<h3>Estoque</h3>");
		formHtml.append("<div class='form-group'>");
		formHtml.append("<label>Quantidade em Estoque</label>");
		formHtml.append("<input type='number' name='quantidade_estoque' placeholder='0' min='0' required>");
		formHtml.append("</div></div>");

		try (Connection conn = DatabaseConnection.getConnection()) {
			EmpresaDAO empresaDAO = new EmpresaDAO(conn);
			List<Empresa> empresas = empresaDAO.listar();
			
			if (empresas.isEmpty()) {
				formHtml.append("<div class='error-msg'>Nenhuma empresa cadastrada. <a href='?action=cadastrarEmpresaForm'>Cadastre uma empresa primeiro</a></div>");
				formHtml.append("</form></div></div>");
				page.addToBody(formHtml.toString());
				response.getWriter().println(page.render());
				return;
			}

			// Empresa
			formHtml.append("<div class='form-section'>");
			formHtml.append("<h3>Empresa</h3>");
			formHtml.append("<div class='form-group'>");
			formHtml.append("<label>Selecione a Empresa</label>");
			formHtml.append("<select name='iddaempresa' required>");
			formHtml.append("<option value=''>Escolha uma empresa...</option>");
			for (Empresa empresa : empresas) {
				formHtml.append("<option value='").append(escapeHtml(empresa.getId())).append("'>").append(escapeHtml(empresa.getNomeRazao()))
						.append("</option>");
			}
			formHtml.append("</select>");
			formHtml.append("</div></div>");

		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Erro ao carregar empresas", e);
			formHtml.append("<div class='form-section'><div class='error-msg'>Erro ao carregar empresas</div></div>");
		}

		formHtml.append("<button type='submit' class='btn-submit'>Cadastrar Produto</button>");
		formHtml.append("</form></div></div>");
		
		// CSS específico
		formHtml.append("<style>");
		formHtml.append(".form-section { margin-bottom: 30px; padding: 20px; background: #f8f9fa; border-radius: 12px; }");
		formHtml.append(".form-section h3 { margin-bottom: 20px; color: #4a5568; font-size: 1.2rem; }");
		formHtml.append(".form-row { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; }");
		formHtml.append(".btn-submit { display: block; width: 100%; padding: 16px; background: linear-gradient(135deg, #48bb78, #38a169); color: white; border: none; border-radius: 12px; font-size: 1.1rem; font-weight: 600; cursor: pointer; transition: all 0.3s; margin-top: 20px; }");
		formHtml.append(".btn-submit:hover { transform: translateY(-2px); box-shadow: 0 10px 25px rgba(72,187,120,0.3); }");
		formHtml.append(".btn-submit:active { transform: translateY(0); }");

		formHtml.append("@media (max-width: 768px) { .form-row { grid-template-columns: 1fr; } }");
		formHtml.append("</style>");
		
		page.addToBody(formHtml.toString());
		response.getWriter().println(page.render());
	}

	public void cadastrarProduto(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String descricao = request.getParameter("descricao");
		String quantidade = request.getParameter("quantidade");
		String valorCusto = request.getParameter("valorCusto");
		String valorVenda = request.getParameter("valorVenda");
		String idEmpresa = request.getParameter("iddaempresa");
		String quantidadeEstoque = request.getParameter("quantidade_estoque");
		
		if (descricao == null || descricao.isBlank()) {
			try {
				response.sendRedirect("?action=cadastrarProdutoForm&error=" + java.net.URLEncoder.encode("Descrição é obrigatória.", "UTF-8"));
			} catch (Exception e) {
				response.getWriter().println("Erro de validação");
			}
			return;
		}
		
		if (quantidade == null || quantidade.isBlank()) {
			try {
				response.sendRedirect("?action=cadastrarProdutoForm&error=" + java.net.URLEncoder.encode("Quantidade é obrigatória.", "UTF-8"));
			} catch (Exception e) {
				response.getWriter().println("Erro de validação");
			}
			return;
		}
		
		if (valorCusto == null || valorCusto.isBlank()) {
			try {
				response.sendRedirect("?action=cadastrarProdutoForm&error=" + java.net.URLEncoder.encode("Valor de custo é obrigatório.", "UTF-8"));
			} catch (Exception e) {
				response.getWriter().println("Erro de validação");
			}
			return;
		}
		
		if (valorVenda == null || valorVenda.isBlank()) {
			try {
				response.sendRedirect("?action=cadastrarProdutoForm&error=" + java.net.URLEncoder.encode("Valor de venda é obrigatório.", "UTF-8"));
			} catch (Exception e) {
				response.getWriter().println("Erro de validação");
			}
			return;
		}
		
		if (idEmpresa == null || idEmpresa.isBlank()) {
			try {
				response.sendRedirect("?action=cadastrarProdutoForm&error=" + java.net.URLEncoder.encode("Empresa é obrigatória.", "UTF-8"));
			} catch (Exception e) {
				response.getWriter().println("Erro de validação");
			}
			return;
		}
		
		if (quantidadeEstoque == null || quantidadeEstoque.isBlank()) {
			try {
				response.sendRedirect("?action=cadastrarProdutoForm&error=" + java.net.URLEncoder.encode("Quantidade em estoque é obrigatória.", "UTF-8"));
			} catch (Exception e) {
				response.getWriter().println("Erro de validação");
			}
			return;
		}
		
		Produto produto = new Produto();
		produto.setId(UUID.randomUUID().toString());
		produto.setDescricao(descricao);
		produto.setQuantidade(quantidade);
		produto.setValorCusto(valorCusto);
		produto.setValorVenda(valorVenda);
		produto.setIdEmpresa(idEmpresa);
		produto.setQuantidadeEstoque(quantidadeEstoque);
		produto.setActive(true);

		try (Connection conn = DatabaseConnection.getConnection()) {
			ProdutoDAO dao = new ProdutoDAO(conn);
			dao.cadastrar(produto);
			response.sendRedirect("?action=listarProdutos&success=" + java.net.URLEncoder.encode("Produto cadastrado com sucesso!", "UTF-8"));
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Erro ao cadastrar produto", e);
			String errorMsg = "Erro ao cadastrar produto";
			if (e.getMessage().contains("Duplicate entry")) {
				errorMsg = "Produto já existe. Verifique os dados.";
			} else if (e.getMessage().contains("foreign key")) {
				errorMsg = "Empresa selecionada não existe.";
			} else {
				errorMsg = "Erro no banco de dados: " + e.getMessage();
			}
			try {
				response.sendRedirect("?action=cadastrarProdutoForm&error=" + java.net.URLEncoder.encode(errorMsg, "UTF-8"));
			} catch (Exception ex) {
				response.getWriter().println("Erro ao cadastrar produto");
			}
		} catch (NumberFormatException e) {
			logger.log(Level.WARNING, "Erro de formato nos parâmetros", e);
			try {
				response.sendRedirect("?action=cadastrarProdutoForm&error=" + java.net.URLEncoder.encode("Valores numéricos inválidos. Verifique quantidade e preços.", "UTF-8"));
			} catch (Exception ex) {
				response.getWriter().println("Erro de formato");
			}
		}
	}

	public void listarProdutos(HttpServletRequest request, HttpServletResponse response) throws IOException {
		HtmlPage page = new HtmlPage("Estoque de Produtos", request.getContextPath());
		
		StringBuilder listHtml = new StringBuilder();
		listHtml.append("<div class='container'><div class='card'>");
		listHtml.append("<div class='header-section'>");
		listHtml.append("<h1>Estoque de Produtos</h1>");
		listHtml.append("<a href='?action=cadastrarProdutoForm' class='btn-add'>Adicionar Produto</a>");
		listHtml.append("</div>");

		try (Connection conn = DatabaseConnection.getConnection()) {
			ProdutoDAO dao = new ProdutoDAO(conn);
			List<Produto> lista = dao.listar();

			if (lista.isEmpty()) {
				listHtml.append("<div class='empty-state'>");
				listHtml.append("<div class='empty-icon'>Vazio</div>");
				listHtml.append("<h3>Nenhum produto cadastrado</h3>");
				listHtml.append("<p>Comece adicionando seu primeiro produto ao estoque</p>");
				listHtml.append("<a href='?action=cadastrarProdutoForm' class='btn'>Adicionar Primeiro Produto</a>");
				listHtml.append("</div>");
			} else {
				listHtml.append("<div class='products-grid'>");
				
				for (Produto p : lista) {
					listHtml.append("<div class='product-card'>");
					listHtml.append("<div class='product-header'>");
					listHtml.append("<h3>").append(escapeHtml(p.getDescricao())).append("</h3>");
					listHtml.append("<span class='status ").append(p.getActive() ? "active" : "inactive").append("'>");
					listHtml.append(p.getActive() ? "Ativo" : "Inativo").append("</span>");
					listHtml.append("</div>");
					
					listHtml.append("<div class='product-info'>");
					listHtml.append("<div class='info-item'>");
					listHtml.append("<span class='label'>Quantidade:</span>");
					listHtml.append("<span class='value'>").append(escapeHtml(p.getQuantidade())).append("</span>");
					listHtml.append("</div>");
					
					listHtml.append("<div class='info-item'>");
					listHtml.append("<span class='label'>Estoque:</span>");
					int estoque = Integer.parseInt(p.getQuantidadeEstoque() != null ? p.getQuantidadeEstoque() : "0");
					String estoqueClass = estoque <= 5 ? "low" : estoque <= 20 ? "medium" : "high";
					listHtml.append("<span class='value stock ").append(estoqueClass).append("'>").append(estoque).append("</span>");
					listHtml.append("</div>");
					listHtml.append("</div>");
					
					listHtml.append("<div class='product-prices'>");
					try {
						String valorCustoStr = p.getValorCusto();
						String valorVendaStr = p.getValorVenda();
						if (valorCustoStr != null && valorVendaStr != null) {
							double valorCusto = Double.parseDouble(valorCustoStr.replace(",", "."));
							double valorVenda = Double.parseDouble(valorVendaStr.replace(",", "."));
							listHtml.append("<div class='price-item'>");
							listHtml.append("<span class='price-label'>Custo:</span>");
							listHtml.append("<span class='price-value cost'>R$ ").append(String.format(Locale.ROOT, "%.2f", valorCusto)).append("</span>");
							listHtml.append("</div>");
							listHtml.append("<div class='price-item'>");
							listHtml.append("<span class='price-label'>Venda:</span>");
							listHtml.append("<span class='price-value sale'>R$ ").append(String.format(Locale.ROOT, "%.2f", valorVenda)).append("</span>");
							listHtml.append("</div>");
							
		
							double margem = ((valorVenda - valorCusto) / valorCusto) * 100;
							listHtml.append("<div class='margin'>Margem: ").append(String.format(Locale.ROOT, "%.1f%%", margem)).append("</div>");
						} else {
							listHtml.append("<div class='price-error'>Preços não definidos</div>");
						}
					} catch (NumberFormatException | NullPointerException e) {
						logger.log(Level.WARNING, "Erro ao formatar valores do produto", e);
						listHtml.append("<div class='price-error'>Valores inválidos</div>");
					}
					
					listHtml.append("</div>");
					listHtml.append("<div class='product-footer'>");
					listHtml.append("<small>Empresa: ").append(escapeHtml(p.getIdEmpresa())).append("</small>");
					listHtml.append("</div>");
					listHtml.append("</div>");
				}
				
				listHtml.append("</div>");
			}
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Erro ao listar produtos", e);
			listHtml.append("<div class='error-msg'>Erro ao carregar produtos do banco de dados</div>");
		}
		
		listHtml.append("</div></div>");
		

		listHtml.append("<style>");
		listHtml.append(".header-section { display: flex; justify-content: space-between; align-items: center; margin-bottom: 30px; }");
		listHtml.append(".btn-add { background: linear-gradient(135deg, #48bb78, #38a169); color: white; padding: 12px 20px; border-radius: 8px; text-decoration: none; font-weight: 600; }");
		listHtml.append(".empty-state { text-align: center; padding: 60px 20px; }");
		listHtml.append(".empty-icon { font-size: 4rem; margin-bottom: 20px; }");
		listHtml.append(".products-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(280px, 1fr)); gap: 15px; }");
		listHtml.append(".product-card { background: white; border-radius: 12px; padding: 20px; box-shadow: 0 4px 15px rgba(0,0,0,0.1); transition: all 0.3s; }");
		listHtml.append(".product-card:hover { transform: translateY(-5px); box-shadow: 0 8px 25px rgba(0,0,0,0.15); }");
		listHtml.append(".product-header { display: flex; justify-content: space-between; align-items: start; margin-bottom: 15px; }");
		listHtml.append(".product-header h3 { margin: 0; color: #2d3748; font-size: 1.1rem; }");
		listHtml.append(".status { padding: 4px 8px; border-radius: 20px; font-size: 0.8rem; font-weight: 600; }");
		listHtml.append(".status.active { background: #c6f6d5; color: #22543d; }");
		listHtml.append(".status.inactive { background: #fed7d7; color: #742a2a; }");
		listHtml.append(".product-info { margin-bottom: 15px; }");
		listHtml.append(".info-item { display: flex; justify-content: space-between; margin-bottom: 8px; }");
		listHtml.append(".label { color: #718096; font-size: 0.9rem; }");
		listHtml.append(".value { font-weight: 600; color: #2d3748; }");
		listHtml.append(".stock.low { color: #e53e3e; }");
		listHtml.append(".stock.medium { color: #d69e2e; }");
		listHtml.append(".stock.high { color: #38a169; }");
		listHtml.append(".product-prices { background: #f7fafc; padding: 15px; border-radius: 8px; margin-bottom: 15px; }");
		listHtml.append(".price-item { display: flex; justify-content: space-between; margin-bottom: 8px; }");
		listHtml.append(".price-label { color: #718096; font-size: 0.9rem; }");
		listHtml.append(".price-value.cost { color: #e53e3e; font-weight: 600; }");
		listHtml.append(".price-value.sale { color: #38a169; font-weight: 600; }");
		listHtml.append(".margin { text-align: center; font-size: 0.9rem; color: #4a5568; font-weight: 600; margin-top: 10px; }");
		listHtml.append(".product-footer { border-top: 1px solid #e2e8f0; padding-top: 10px; }");
		listHtml.append(".product-footer small { color: #718096; }");
		listHtml.append("@media (max-width: 768px) { .header-section { flex-direction: column; gap: 15px; } .products-grid { grid-template-columns: 1fr; gap: 10px; } }");
		listHtml.append("@media (max-width: 480px) { .container { padding: 5px; } .product-card { padding: 10px; } .product-header h3 { font-size: 0.9rem; } }");

		listHtml.append("</style>");
		
		page.addToBody(listHtml.toString());
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
