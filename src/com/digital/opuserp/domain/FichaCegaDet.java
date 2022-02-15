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
@Table(name="ficha_cega_det")
public class FichaCegaDet {

	@Id
	@Column(name="ID")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	
	@OneToOne
	@JoinColumn(name="FICHA_CEGA_CAB")
	private FichaCegaCab ficha_cega_cab;
	
	@Column(name="PRODUTO_ID")
	private Integer produto;
	
	@Column(name="SALDO_LOJA")
	private Float saldo_loja;
	
	@Column(name="SALDO_DEPOSITO")
	private Float saldo_deposito;
	
	@Column(name="STATUS")
	private String status;
	
	public FichaCegaDet(){
		
	}

	public FichaCegaDet(Integer id, FichaCegaCab ficha_cega_cab,
			Integer produto, Float saldo_loja, Float saldo_deposito,
			String status) {
		super();
		this.id = id;
		this.ficha_cega_cab = ficha_cega_cab;
		this.produto = produto;
		this.saldo_loja = saldo_loja;
		this.saldo_deposito = saldo_deposito;
		this.status = status;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public FichaCegaCab getFicha_cega_cab() {
		return ficha_cega_cab;
	}

	public void setFicha_cega_cab(FichaCegaCab ficha_cega_cab) {
		this.ficha_cega_cab = ficha_cega_cab;
	}

	public Integer getProduto() {
		return produto;
	}

	public void setProduto(Integer produto) {
		this.produto = produto;
	}

	public Float getSaldo_loja() {
		return saldo_loja;
	}

	public void setSaldo_loja(Float saldo_loja) {
		this.saldo_loja = saldo_loja;
	}

	public Float getSaldo_deposito() {
		return saldo_deposito;
	}

	public void setSaldo_deposito(Float saldo_deposito) {
		this.saldo_deposito = saldo_deposito;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	
	
}
