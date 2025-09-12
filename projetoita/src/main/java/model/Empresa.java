package model;

import java.util.UUID;

public class Empresa {
	private String id;
	private String nomeRazao;
	private String apelidoFantasia;
	private String cpfCnpj;
	private String bairro;
	private String cep;
	private String cidade;
	private String pais;
	private String estado;
	private String logradouro;
	private String contato01;
	private String contato02;
	private String email;
	private String emailFinanceiro;
	private Boolean active;

	public boolean getActive() {
		return active != null ? active : false;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Empresa() {
		this.setId(UUID.randomUUID().toString());
	}

	public String getNomeRazao() {
		return nomeRazao;
	}

	public void setNomeRazao(String nomeRazao) {
		this.nomeRazao = nomeRazao;
	}

	public String getApelidoFantasia() {
		return apelidoFantasia;
	}

	public void setApelidoFantasia(String apelidoFantasia) {
		this.apelidoFantasia = apelidoFantasia;
	}

	public String getCpfCnpj() {
		return cpfCnpj;
	}

	public void setCpfCnpj(String cpfCnpj) {
		if (cpfCnpj != null && !isValidCpfCnpj(cpfCnpj)) {
			throw new IllegalArgumentException("CPF/CNPJ inválido");
		}
		this.cpfCnpj = cpfCnpj;
	}
	
	private boolean isValidCpfCnpj(String cpfCnpj) {
		String digits = cpfCnpj.replaceAll("\\D", "");
		return digits.length() == 11 || digits.length() == 14;
	}

	public String getBairro() {
		return bairro;
	}

	public void setBairro(String bairro) {
		this.bairro = bairro;
	}

	public String getCep() {
		return cep;
	}

	public void setCep(String cep) {
		this.cep = cep;
	}

	public String getCidade() {
		return cidade;
	}

	public void setCidade(String cidade) {
		this.cidade = cidade;
	}

	public String getPais() {
		return pais;
	}

	public void setPais(String pais) {
		this.pais = pais;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public String getLogradouro() {
		return logradouro;
	}

	public void setLogradouro(String logradouro) {
		this.logradouro = logradouro;
	}

	public String getContato01() {
		return contato01;
	}

	public void setContato01(String contato01) {
		this.contato01 = contato01;
	}

	public String getContato02() {
		return contato02;
	}

	public void setContato02(String contato02) {
		this.contato02 = contato02;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmailFinanceiro() {
		return emailFinanceiro;
	}

	public void setEmailFinanceiro(String emailFinanceiro) {
		this.emailFinanceiro = emailFinanceiro;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
