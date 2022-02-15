package com.digital.opuserp.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;



@Entity
@Table(name="ncm")
public class Ncm {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="CODIGO", unique=true, nullable=false)
	private Integer codigo;
	@Column(name="DESCRICAO")
	private String descricao;
	
	public Ncm(){
		
	}

	public Ncm(Integer codigo, String descricao) {
		super();
		this.codigo = codigo;
		this.descricao = descricao;
	}

	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
}
