package com.digital.opuserp.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="config_acesso")
public class ConfigAcesso {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID")
	private Integer id;	
	@Column(name="EMPRESA_ID")
	private Integer empresa_id;	
	@Column(name="SECAO")
	private String secao;
	@Column(name="VALOR")
	private String valor;
	
	public ConfigAcesso(){
		
	}

	public ConfigAcesso(Integer id, Integer empresa_id, String secao,
			String valor) {
		super();
		this.id = id;
		this.empresa_id = empresa_id;
		this.secao = secao;
		this.valor = valor;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getEmpresa_id() {
		return empresa_id;
	}

	public void setEmpresa_id(Integer empresa_id) {
		this.empresa_id = empresa_id;
	}

	public String getSecao() {
		return secao;
	}

	public void setSecao(String secao) {
		this.secao = secao;
	}

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}
	
	
}
