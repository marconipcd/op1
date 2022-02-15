package com.digital.opuserp.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="cst_origem")
public class CstOrigem {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID", unique=true, nullable=false)
	private Integer id; 
	
	@Column(name="CST_ORIGEM_COD", nullable=false)
	private Integer cst_origem_cod; 
	
	@Column(name="REFERENCIA", nullable=false)
	private String referencia; 

	public CstOrigem(){
		
	}

	public CstOrigem(Integer id) {
		super();
		this.id = id;
	}

	public CstOrigem(Integer id, Integer cst_origem_cod, String referencia) {
		super();
		this.id = id;
		this.cst_origem_cod = cst_origem_cod;
		this.referencia = referencia;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getCst_origem_cod() {
		return cst_origem_cod;
	}

	public void setCst_origem_cod(Integer cst_origem_cod) {
		this.cst_origem_cod = cst_origem_cod;
	}

	public String getReferencia() {
		return referencia;
	}

	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}

	
	
}
