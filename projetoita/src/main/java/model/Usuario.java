package model;

public class Usuario {
	private String id;
	private String email;
	private String senha;
	private String id_empresa;
	private Boolean active;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
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

	public String getId_empresa() {
		return id_empresa;
	}

	public void setId_empresa(String id_empresa) {
		this.id_empresa = id_empresa;
	}

	public String getIddaempresa() {
		return id_empresa;
	}

	public void setIddaempresa(String iddaempresa) {
		this.id_empresa = iddaempresa;
	}

}