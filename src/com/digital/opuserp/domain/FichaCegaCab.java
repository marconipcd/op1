package com.digital.opuserp.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="ficha_cega_cab")
public class FichaCegaCab {

	@Id
	@Column(name="ID")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	
	@Column(name="EMPRESA_ID")
	private Integer empresa;
	
	@Column(name="USUARIO")
	private String usuario;
	
	@Column(name="DATA")
	private Date data;
	
	public FichaCegaCab(){
		
	}

	public FichaCegaCab(Integer id, Integer empresa, String usuario, Date data) {
		super();
		this.id = id;
		this.empresa = empresa;
		this.usuario = usuario;
		this.data = data;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Integer empresa) {
		this.empresa = empresa;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}
	
	
	
}
