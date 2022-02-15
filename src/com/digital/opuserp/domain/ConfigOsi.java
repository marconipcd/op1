package com.digital.opuserp.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="config_osi")
public class ConfigOsi {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID")
	private Integer id;	
	@Column(name="EMPRESA_ID")
	private Integer empresa_id;	
	@Column(name="VALOR_LAUDO")
	private String valor_laudo;
	
	public ConfigOsi(){
		
	}

	public ConfigOsi(Integer id, Integer empresa_id, String valor_laudo) {
		super();
		this.id = id;
		this.empresa_id = empresa_id;
		this.valor_laudo = valor_laudo;
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

	public String getValor_laudo() {
		return valor_laudo;
	}

	public void setValor_laudo(String valor_laudo) {
		this.valor_laudo = valor_laudo;
	}

	
	
	
}
