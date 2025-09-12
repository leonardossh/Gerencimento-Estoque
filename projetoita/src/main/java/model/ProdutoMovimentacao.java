package model;

public class ProdutoMovimentacao {
	private String id;
	private String idProduto;
	private String id_Empresa;
	private int quantidadeMovimentada;
	private double valorUnitario;
	private String tipoMovimentacao;
	private String tipo;
	private boolean active;
	private String nomeProduto;
	private String nomeEmpresa;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIdProduto() {
		return idProduto;
	}

	public void setIdProduto(String idProduto) {
		this.idProduto = idProduto;
	}

	public int getQuantidadeMovimentada() {
		return quantidadeMovimentada;
	}

	public void setQuantidadeMovimentada(int quantidadeMovimentada) {
		this.quantidadeMovimentada = quantidadeMovimentada;
	}

	public double getValorUnitario() {
		return valorUnitario;
	}

	public void setValorUnitario(double valorUnitario) {
		this.valorUnitario = valorUnitario;
	}

	public String getTipoMovimentacao() {
		return tipoMovimentacao;
	}

	public void setTipoMovimentacao(String tipoMovimentacao) {
		this.tipoMovimentacao = tipoMovimentacao;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public boolean getActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getId_Empresa() {
		return id_Empresa;
	}

	public void setId_Empresa(String id_Empresa) {
		this.id_Empresa = id_Empresa;
	}

	public String getNomeProduto() {
		return nomeProduto;
	}

	public void setNomeProduto(String nomeProduto) {
		this.nomeProduto = nomeProduto;
	}

	public String getNomeEmpresa() {
		return nomeEmpresa;
	}

	public void setNomeEmpresa(String nomeEmpresa) {
		this.nomeEmpresa = nomeEmpresa;
	}
}