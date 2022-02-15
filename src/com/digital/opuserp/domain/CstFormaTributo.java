package com.digital.opuserp.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="cst_tributo")
public class CstFormaTributo {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID", unique=true, nullable=false)
	private Integer id; 
	
	@Column(name="CST_TRIBUTO_COD", nullable=false)
	private String cst_tributo_cod; 
	
	@Column(name="REFERENCIA", nullable=false)
	private String referencia;

	public CstFormaTributo() {

	}

	public CstFormaTributo(Integer id) {
		super();
		this.id = id;
	}

	public CstFormaTributo(Integer id, String cst_tributo_cod,
			String referencia) {
		super();
		this.id = id;
		this.cst_tributo_cod = cst_tributo_cod;
		this.referencia = referencia;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCst_tributo_cod() {
		return cst_tributo_cod;
	}

	public void setCst_tributo_cod(String cst_tributo_cod) {
		this.cst_tributo_cod = cst_tributo_cod;
	}

	public String getReferencia() {
		return referencia;
	}

	public void setReferencia(String referencia) {
		this.referencia = referencia;
	} 
		
	
}