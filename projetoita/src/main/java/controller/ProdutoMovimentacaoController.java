package controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import connection.DatabaseConnection;
import java.util.logging.Logger;
import java.util.logging.Level;
import dao.ProdutoMovimentacaoDAO;
import dao.ProdutoDAO;
import dao.EmpresaDAO;
import dao.PessoaDAO;
import model.ProdutoMovimentacao;
import model.Produto;
import model.Empresa;
import model.Pessoa;
import view.HtmlForm;
import view.HtmlPage;

public class ProdutoMovimentacaoController {
	private static final Logger logger = Logger.getLogger(ProdutoMovimentacaoController.class.getName());
	public void movimentarForm(HttpServletRequest request, HttpServletResponse response) throws IOException {
		HtmlPage page = new HtmlPage("Movimentar Produto", request.getContextPath());
		HtmlForm form = new HtmlForm("post", "?action=movimentarProduto");

		try (Connection conn = DatabaseConnection.getConnection()) {
			ProdutoDAO produtoDAO = new ProdutoDAO(conn);
			List<Produto> produtos = produtoDAO.listar();

			StringBuilder selectProdutos = new StringBuilder("<label>Produto:</label><br><select name='idProduto'>");
			for (Produto p : produtos) {
				selectProdutos.append("<option value='").append(p.getId()).append("'>").append(p.getDescricao())
						.append("</option>");
			}
			selectProdutos.append("</select><br><br>");
			form.addRawHtml(selectProdutos.toString());

		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Erro ao carregar produtos", e);
			form.addRawHtml("<p style='color:red;'>Erro ao carregar produtos</p>");
		}


		try (Connection conn2 = DatabaseConnection.getConnection()) {
			EmpresaDAO empresaDAO = new EmpresaDAO(conn2);
			List<Empresa> empresas = empresaDAO.listar();
			
			if (empresas.isEmpty()) {
				form.addRawHtml("<div class='error-msg'>Nenhuma empresa cadastrada. <a href='?action=cadastrarEmpresaForm'>Cadastre uma empresa primeiro</a></div>");
			} else {
				StringBuilder selectEmpresas = new StringBuilder();
				selectEmpresas.append("<div class='form-group'>");
				selectEmpresas.append("<label>Empresa:</label>");
				selectEmpresas.append("<select name='iddaempresa' required>");
				selectEmpresas.append("<option value=''>Selecione uma empresa</option>");
				for (Empresa empresa : empresas) {
					selectEmpresas.append("<option value='").append(empresa.getId()).append("'>").append(empresa.getNomeRazao())
							.append("</option>");
				}
				selectEmpresas.append("</select>");
				selectEmpresas.append("</div>");
				form.addRawHtml(selectEmpresas.toString());
			}
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Erro ao carregar empresas", e);
			form.addRawHtml("<p style='color: red;'>Erro ao carregar empresas</p>");
		}
		form.addInput("Quantidade Movimentada", "quantidadeMovimentada", "number");
		form.addInput("Valor Unitário", "valorUnitario", "number");

		form.addRawHtml("<label>Tipo de Movimentação:</label><br>" + "<select name='tipoMovimentacao' required>"
				+ "<option value=''>Selecione o tipo</option>"
				+ "<option value='compra'>Compra (Entrada)</option>" + "<option value='venda'>Venda (Saída)</option>"
				+ "</select><br><br>");
		
		form.addRawHtml("<label>Cliente (apenas para vendas):</label><br>");
		
		try (Connection connUsuarios = DatabaseConnection.getConnection()) {
			PessoaDAO pessoaDAO = new PessoaDAO(connUsuarios);
			List<Pessoa> usuarios = pessoaDAO.listar();
			
			StringBuilder selectUsuarios = new StringBuilder();
			selectUsuarios.append("<select name='cliente'>");
			selectUsuarios.append("<option value=''>Nenhum (para compras)</option>");
			for (Pessoa usuario : usuarios) {
				selectUsuarios.append("<option value='").append(usuario.getId()).append("'>");
				selectUsuarios.append(usuario.getEmail()).append("</option>");
			}
			selectUsuarios.append("</select><br><br>");
			form.addRawHtml(selectUsuarios.toString());
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Erro ao carregar usuários", e);
			form.addRawHtml("<select name='cliente'><option>Erro ao carregar usuários</option></select><br><br>");
		}

		form.addButton("Movimentar");

		page.addToBody("<h1>Movimentação de Produto</h1>");
		page.addToBody(form.render());
		

		
		response.getWriter().println(page.render());
	}

	public void movimentarProduto(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String idProduto = request.getParameter("idProduto");
		String idEmpresa = request.getParameter("iddaempresa");
		String quantidadeStr = request.getParameter("quantidadeMovimentada");
		String valorStr = request.getParameter("valorUnitario");
		String cliente = request.getParameter("cliente");
		
		if (idProduto == null || idProduto.isBlank()) {
			try {
				response.sendRedirect("?action=movimentarProdutoForm&error=" + java.net.URLEncoder.encode("Produto é obrigatório.", "UTF-8"));
			} catch (Exception e) {
				response.getWriter().println("Erro de validação");
			}
			return;
		}
		
		if (idEmpresa == null || idEmpresa.isBlank()) {
			try {
				response.sendRedirect("?action=movimentarProdutoForm&error=" + java.net.URLEncoder.encode("Empresa é obrigatória.", "UTF-8"));
			} catch (Exception e) {
				response.getWriter().println("Erro de validação");
			}
			return;
		}
		
		if (quantidadeStr == null || quantidadeStr.isBlank()) {
			try {
				response.sendRedirect("?action=movimentarProdutoForm&error=" + java.net.URLEncoder.encode("Quantidade é obrigatória.", "UTF-8"));
			} catch (Exception e) {
				response.getWriter().println("Erro de validação");
			}
			return;
		}
		
		if (valorStr == null || valorStr.isBlank()) {
			try {
				response.sendRedirect("?action=movimentarProdutoForm&error=" + java.net.URLEncoder.encode("Valor unitário é obrigatório.", "UTF-8"));
			} catch (Exception e) {
				response.getWriter().println("Erro de validação");
			}
			return;
		}
		
		ProdutoMovimentacao mov = new ProdutoMovimentacao();
		mov.setId(UUID.randomUUID().toString());
		mov.setIdProduto(idProduto);
		mov.setId_Empresa(idEmpresa);
		mov.setQuantidadeMovimentada(Integer.parseInt(quantidadeStr));
		mov.setValorUnitario(Double.parseDouble(valorStr));

		String tipoSelecionado = request.getParameter("tipoMovimentacao");
		
		if (tipoSelecionado == null || tipoSelecionado.isBlank()) {
			try {
				response.sendRedirect("?action=movimentarProdutoForm&error=" + java.net.URLEncoder.encode("Tipo de movimentação é obrigatório.", "UTF-8"));
			} catch (Exception e) {
				response.getWriter().println("Erro de validação");
			}
			return;
		}
		
		// Validação do cliente para vendas
		if ("venda".equalsIgnoreCase(tipoSelecionado) && (cliente == null || cliente.isBlank())) {
			try {
				response.sendRedirect("?action=movimentarProdutoForm&error=" + java.net.URLEncoder.encode("Cliente é obrigatório para vendas.", "UTF-8"));
			} catch (Exception e) {
				response.getWriter().println("Erro de validação");
			}
			return;
		}
		
		String tipoEnum;
		if ("compra".equalsIgnoreCase(tipoSelecionado)) {
			tipoEnum = "Entrada";
		} else if ("venda".equalsIgnoreCase(tipoSelecionado)) {
			tipoEnum = "Saida";
		} else {
			try {
				response.sendRedirect("?action=movimentarProdutoForm&error=" + java.net.URLEncoder.encode("Tipo de movimentação inválido.", "UTF-8"));
			} catch (Exception e) {
				response.getWriter().println("Erro de validação");
			}
			return;
		}

		mov.setTipoMovimentacao(tipoSelecionado);
		mov.setTipo(tipoEnum);
		// Define ID do cliente apenas para vendas
		if ("venda".equalsIgnoreCase(tipoSelecionado)) {
			mov.setIdCliente(cliente);
		} else {
			mov.setIdCliente(null); // NULL para compras
		}
		mov.setActive(true);

		try (Connection conn = DatabaseConnection.getConnection()) {
			ProdutoMovimentacaoDAO dao = new ProdutoMovimentacaoDAO(conn);
			dao.registrarMovimentacao(mov);
			response.sendRedirect("?action=listarMovimentacoes&success=" + java.net.URLEncoder.encode("Movimentação registrada com sucesso!", "UTF-8"));
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Erro ao movimentar produto", e);
			String errorMsg = "Erro ao registrar movimentação";
			if (e.getMessage().contains("foreign key")) {
				errorMsg = "Produto ou empresa selecionados não existem.";
			} else {
				errorMsg = "Erro no banco de dados: " + e.getMessage();
			}
			try {
				response.sendRedirect("?action=movimentarProdutoForm&error=" + java.net.URLEncoder.encode(errorMsg, "UTF-8"));
			} catch (Exception ex) {
				response.getWriter().println("Erro ao movimentar produto");
			}
		} catch (NumberFormatException e) {
			logger.log(Level.WARNING, "Erro de formato nos parâmetros", e);
			try {
				response.sendRedirect("?action=movimentarProdutoForm&error=" + java.net.URLEncoder.encode("Valores numéricos inválidos. Verifique quantidade e valor.", "UTF-8"));
			} catch (Exception ex) {
				response.getWriter().println("Erro de formato");
			}
		}
	}

	public void listarMovimentacoes(HttpServletRequest request, HttpServletResponse response) throws IOException {
		HtmlPage page = new HtmlPage("Movimentações de Produtos", request.getContextPath());
		
		StringBuilder listHtml = new StringBuilder();
		listHtml.append("<div class='container'><div class='card'>");
		listHtml.append("<div class='header-section'>");
		listHtml.append("<h1>Historico de Movimentacoes</h1>");
		listHtml.append("<a href='?action=movimentarProdutoForm' class='btn-add'>Nova Movimentacao</a>");
		listHtml.append("</div>");

		try (Connection conn = DatabaseConnection.getConnection()) {
			ProdutoMovimentacaoDAO dao = new ProdutoMovimentacaoDAO(conn);
			List<ProdutoMovimentacao> lista = dao.listar();

			if (lista.isEmpty()) {
				listHtml.append("<div class='empty-state'>");
				listHtml.append("<div class='empty-icon'>Vazio</div>");
				listHtml.append("<h3>Nenhuma movimentação registrada</h3>");
				listHtml.append("<p>Comece registrando a primeira movimentação de produto</p>");
				listHtml.append("<a href='?action=movimentarProdutoForm' class='btn'>Registrar Primeira Movimentacao</a>");
				listHtml.append("</div>");
			} else {
				listHtml.append("<div class='movements-grid'>");
				
				for (ProdutoMovimentacao mov : lista) {
					listHtml.append("<div class='movement-card'>");
					listHtml.append("<div class='movement-header'>");
					listHtml.append("<div class='movement-type'>");
					listHtml.append("<span class='badge badge-").append(mov.getTipoMovimentacao().toLowerCase()).append("'>");
					listHtml.append(mov.getTipoMovimentacao().equals("compra") ? "Compra" : "Venda").append("</span>");
					listHtml.append("</div>");
					listHtml.append("<span class='status ").append(mov.getActive() ? "active" : "inactive").append("'>");
					listHtml.append(mov.getActive() ? "Ativo" : "Inativo").append("</span>");
					listHtml.append("</div>");
					
					listHtml.append("<div class='movement-info'>");
					listHtml.append("<div class='info-item'>");
					listHtml.append("<span class='label'>Produto:</span>");
					listHtml.append("<span class='value'>").append(escapeHtml(mov.getNomeProduto() != null ? mov.getNomeProduto() : "N/A")).append("</span>");
					listHtml.append("</div>");
					
					listHtml.append("<div class='info-item'>");
					listHtml.append("<span class='label'>Empresa:</span>");
					listHtml.append("<span class='value'>").append(escapeHtml(mov.getNomeEmpresa() != null ? mov.getNomeEmpresa() : "N/A")).append("</span>");
					listHtml.append("</div>");
					
					listHtml.append("<div class='info-item'>");
					listHtml.append("<span class='label'>Quantidade:</span>");
					listHtml.append("<span class='value quantity'>").append(mov.getQuantidadeMovimentada()).append("</span>");
					listHtml.append("</div>");
					
					listHtml.append("<div class='info-item'>");
					listHtml.append("<span class='label'>Valor Unitário:</span>");
					listHtml.append("<span class='value price'>R$ ").append(String.format("%.2f", mov.getValorUnitario())).append("</span>");
					listHtml.append("</div>");
					
					listHtml.append("<div class='info-item'>");
					listHtml.append("<span class='label'>Tipo:</span>");
					listHtml.append("<span class='value'>").append(escapeHtml(mov.getTipo())).append("</span>");
					listHtml.append("</div>");
					
					// Mostra cliente apenas para vendas
					if ("venda".equalsIgnoreCase(mov.getTipoMovimentacao()) && mov.getNomeCliente() != null && !mov.getNomeCliente().isBlank()) {
						listHtml.append("<div class='info-item'>");
						listHtml.append("<span class='label'>Cliente:</span>");
						listHtml.append("<span class='value cliente'>").append(escapeHtml(mov.getNomeCliente())).append("</span>");
						listHtml.append("</div>");
					}
					listHtml.append("</div>");
					
					double valorTotal = mov.getQuantidadeMovimentada() * mov.getValorUnitario();
					listHtml.append("<div class='movement-footer'>");
					listHtml.append("<div class='total-value'>Total: R$ ").append(String.format("%.2f", valorTotal)).append("</div>");
					listHtml.append("</div>");
					listHtml.append("</div>");
				}
				
				listHtml.append("</div>");
			}
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Erro ao listar movimentações", e);
			listHtml.append("<div class='error-msg'>Erro ao carregar movimentações do banco de dados</div>");
		}
		
		listHtml.append("</div></div>");
		

		listHtml.append("<style>");
		listHtml.append(".header-section { display: flex; justify-content: space-between; align-items: center; margin-bottom: 30px; }");
		listHtml.append(".btn-add { background: linear-gradient(135deg, #48bb78, #38a169); color: white; padding: 12px 20px; border-radius: 8px; text-decoration: none; font-weight: 600; }");
		listHtml.append(".empty-state { text-align: center; padding: 60px 20px; }");
		listHtml.append(".empty-icon { font-size: 4rem; margin-bottom: 20px; }");
		listHtml.append(".movements-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(320px, 1fr)); gap: 20px; }");
		listHtml.append(".movement-card { background: white; border-radius: 12px; padding: 20px; box-shadow: 0 4px 15px rgba(0,0,0,0.1); transition: all 0.3s; }");
		listHtml.append(".movement-card:hover { transform: translateY(-5px); box-shadow: 0 8px 25px rgba(0,0,0,0.15); }");
		listHtml.append(".movement-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 15px; }");
		listHtml.append(".movement-info { margin-bottom: 15px; }");
		listHtml.append(".info-item { display: flex; justify-content: space-between; margin-bottom: 8px; }");
		listHtml.append(".label { color: #718096; font-size: 0.9rem; }");
		listHtml.append(".value { font-weight: 600; color: #2d3748; }");
		listHtml.append(".value.quantity { color: #4299e1; font-size: 1.1rem; }");
		listHtml.append(".value.price { color: #38a169; font-weight: 700; }");
		listHtml.append(".value.cliente { color: #805ad5; font-weight: 600; }");
		listHtml.append(".movement-footer { border-top: 1px solid #e2e8f0; padding-top: 15px; text-align: center; }");
		listHtml.append(".total-value { font-size: 1.2rem; font-weight: 700; color: #2d3748; }");
		listHtml.append(".badge { padding: 6px 12px; border-radius: 12px; font-size: 0.8rem; font-weight: 600; }");
		listHtml.append(".badge-compra { background: #c6f6d5; color: #22543d; }");
		listHtml.append(".badge-venda { background: #fed7d7; color: #742a2a; }");
		listHtml.append(".status { padding: 4px 8px; border-radius: 20px; font-size: 0.8rem; font-weight: 600; }");
		listHtml.append(".status.active { background: #c6f6d5; color: #22543d; }");
		listHtml.append(".status.inactive { background: #fed7d7; color: #742a2a; }");
		listHtml.append("@media (max-width: 768px) { .header-section { flex-direction: column; gap: 15px; } .movements-grid { grid-template-columns: 1fr; } }");
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