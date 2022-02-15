package com.digital.opuserp.domain;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name="formas_pagamento")
@Cacheable(value=false)
public class FormaPagamento {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID", nullable=true, unique=true, length=10)
	private Integer id;
	@Column(name="EMPRESA_ID", nullable=true, unique=false, length=10)
	private Integer empresa_Id;
	@Column(name="NOME", nullable=false, unique=false, length=50)
	private String nome;
	@Column(name="DESC_MAX", nullable=true, unique=false)
	private Float descMax;
	@Column(name="APROVACAO_CREDITO", nullable=false, unique=false, length=1)
	private Integer aprovacaoCred;
	@Column(name="TIPO_TITULO", nullable=false, unique=false, length=20)
	private String tipoTitulo;
	
	public FormaPagamento(){
		
	}

	public FormaPagamento(Integer id, Integer empresa_id, String nome, Float descMax, Integer aprovacaoCred, String tipoTitulo){
		this.id = id;
		this.empresa_Id = empresa_id;
		this.nome = nome;
		this.descMax = descMax;
		this.aprovacaoCred = aprovacaoCred;
		this.tipoTitulo = tipoTitulo;		
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getEmpresa_Id() {
		return empresa_Id;
	}

	public void setEmpresa_Id(Integer empresa_Id) {
		this.empresa_Id = empresa_Id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Float getDescMax() {
		return descMax;
	}

	public void setDescMax(Float descMax) {
		this.descMax = descMax;
	}

	public Integer getAprovacaoCred() {
		return aprovacaoCred;
	}

	public void setAprovacaoCred(Integer aprovacaoCred) {
		this.aprovacaoCred = aprovacaoCred;
	}

	public String getTipoTitulo() {
		return tipoTitulo;
	}

	public void setTipoTitulo(String tipoTitulo) {
		this.tipoTitulo = tipoTitulo;
	}
		
	
}
