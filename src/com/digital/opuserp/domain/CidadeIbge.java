package com.digital.opuserp.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="cidade_ibge")
public class CidadeIbge {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID", nullable=false, unique=true)
	Integer id;
	@Column(name="CIDADE")
	String cidade;
	@Column(name="CODIGO_IBGE")
	String codigo_ibge;
	
	public CidadeIbge(){
		
	}

	public CidadeIbge(Integer id, String cidade, String codigo_ibge) {
		super();
		this.id = id;
		this.cidade = cidade;
		this.codigo_ibge = codigo_ibge;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCidade() {
		return cidade;
	}

	public void setCidade(String cidade) {
		this.cidade = cidade;
	}

	public String getCodigo_ibge() {
		return codigo_ibge;
	}

	public void setCodigo_ibge(String codigo_ibge) {
		this.codigo_ibge = codigo_ibge;
	}
	
	
}
