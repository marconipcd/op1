package com.digital.opuserp.domain;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Entity;

@Entity
@Table(name="formas_pagamento")
public class FormasPgto {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID")
	private Integer id;
	@Column(name="EMPRESA_ID")
	private Integer empresa_id; 
	@Column(name="NOME")
	private String nome; 
	@Column(name="DESC_MAX")
	private double desc_max; 
	@Column(name="N_PARCELAS", nullable=true)
	private Integer n_parcelas;
	@Column(name="VALOR_MIN_PARC", nullable=true)
	private double valor_min_parc;	
	@Column(name="APROVACAO_CREDITO")
	private boolean aprovacao_credito; 
	@Column(name="TIPO_TITULO")
	private String tipo_titulo;
	@Column(name="VINCULAR_CLIENTE")
	private boolean vincular_cliente;
	@Column(name="NFE")
	private String nfe;
	
	public FormasPgto(){
		
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

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public double getDesc_max() {
		return desc_max;
	}

	public void setDesc_max(double desc_max) {
		this.desc_max = desc_max;
	}

	public boolean isAprovacao_credito() {
		return aprovacao_credito;
	}

	public void setAprovacao_credito(boolean aprovacao_credito) {
		this.aprovacao_credito = aprovacao_credito;
	}

	public String getTipo_titulo() {
		return tipo_titulo;
	}

	public void setTipo_titulo(String tipo_titulo) {
		this.tipo_titulo = tipo_titulo;
	}

	public Integer getN_parcelas() {
		return n_parcelas;
	}

	public void setN_parcelas(Integer n_parcelas) {
		this.n_parcelas = n_parcelas;
	}

	public double getValor_min_parc() {
		return valor_min_parc;
	}

	public void setValor_min_parc(double valor_min_parc) {
		this.valor_min_parc = valor_min_parc;
	}

	public boolean isVincular_cliente() {
		return vincular_cliente;
	}

	public void setVincular_cliente(boolean vincular_cliente) {
		this.vincular_cliente = vincular_cliente;
	}

	public String getNfe() {
		return nfe;
	}

	public void setNfe(String nfe) {
		this.nfe = nfe;
	}
	
	
}
