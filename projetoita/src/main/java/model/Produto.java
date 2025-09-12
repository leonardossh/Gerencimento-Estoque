package model;

public class Produto {
	private String id;
	private String descricao;
	private String quantidade;
	private String valorCusto;
	private String valorVenda;
	private String idEmpresa;
	private String quantidadeEstoque;
	private boolean active;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(String quantidade) {
		this.quantidade = quantidade;
	}

	public boolean getActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getValorCusto() {
		return valorCusto;
	}

	public void setValorCusto(String valorCusto) {
		this.valorCusto = valorCusto;
	}

	public String getValorVenda() {
		return valorVenda;
	}

	public void setValorVenda(String valorVenda) {
		this.valorVenda = valorVenda;
	}

	public String getIdEmpresa() {
		return idEmpresa;
	}

	public void setIdEmpresa(String idEmpresa) {
		this.idEmpresa = idEmpresa;
	}

	public String getQuantidadeEstoque() {
		return quantidadeEstoque;
	}

	public void setQuantidadeEstoque(String quantidadeEstoque) {
		this.quantidadeEstoque = quantidadeEstoque;
	}

}