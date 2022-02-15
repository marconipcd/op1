package com.digital.opuserp.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="simples_nacional")
public class SimplesNacional {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID", unique=true, nullable=false)
	private Integer id; 
	
	@Column(name="SIMPLES_NACIONAL_COD", nullable=false)
	private Integer simples_nacional_cod; 
	
	@Column(name="REFERENCIA", nullable=false)
	private String referencia;

	public SimplesNacional() {
		super();
	}

	public SimplesNacional(Integer id) {
		super();
		this.id = id;
	}

	public SimplesNacional(Integer id, Integer simples_nacional_cod,
			String referencia) {
		super();
		this.id = id;
		this.simples_nacional_cod = simples_nacional_cod;
		this.referencia = referencia;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getSimples_nacional_cod() {
		return simples_nacional_cod;
	}

	public void setSimples_nacional_cod(Integer simples_nacional_cod) {
		this.simples_nacional_cod = simples_nacional_cod;
	}

	public String getReferencia() {
		return referencia;
	}

	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}

	
}