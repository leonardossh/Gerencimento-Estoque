package model;

import java.util.UUID;

public class Pessoa {
	private String id;
	private String email;
	private String senha;
	private String idDaEmpresa;
	private Boolean active;
	private String nomeEmpresa;

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public Pessoa() {
		this.id = UUID.randomUUID().toString();

	}

	public String getId() {
		return id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public String getIdDaEmpresa() {
		return idDaEmpresa;
	}

	public void setIdDaEmpresa(String idDaEmpresa) {
		this.idDaEmpresa = idDaEmpresa;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Boolean getAtivo() {
		return active != null ? active : true;
	}

	public void setAtivo(Boolean ativo) {
		this.active = ativo;
	}

	public String getNomeEmpresa() {
		return nomeEmpresa;
	}

	public void setNomeEmpresa(String nomeEmpresa) {
		this.nomeEmpresa = nomeEmpresa;
	}
}
