package com.digital.opuserp.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="preferencias_dashboard")
public class PreferenciasDashboard {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID", nullable=false, unique=true)
	private Integer id;
	
	@OneToOne
	@JoinColumn(name="EMPRESA_ID")
	private Empresa empresa;
	
	@OneToOne
	@JoinColumn(name="USUARIO_ID")
	private Usuario usuario;
	
	@Column(name="APP")
	private String app;
	
	public PreferenciasDashboard(){
		
	}
	
	

	public PreferenciasDashboard(Integer id, Empresa empresa, Usuario usuario,
			String app) {
		super();
		this.id = id;
		this.empresa = empresa;
		this.usuario = usuario;
		this.app = app;
	}



	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Empresa getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public String getApp() {
		return app;
	}

	public void setApp(String app) {
		this.app = app;
	}
	
	
}
