package com.digital.opuserp.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="grupo_produto")
public class GrupoProduto {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID")
	private Integer id;
	@Column(name="EMPRESA_ID")
	private Integer empresa_id;
	@Column(name="NOME_GRUPO")
	private String nome_grupo;
	@Column(name="STATUS")
	private String status;
	
	
	public GrupoProduto(){
		
	}

	
	public GrupoProduto(Integer id) {
		super();
		this.id = id;
	
	}
	
	public GrupoProduto(Integer id, Integer empresa_id, String nome_grupo) {
		super();
		this.id = id;
		this.empresa_id = empresa_id;
		this.nome_grupo = nome_grupo;
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

	public String getNome_grupo() {
		return nome_grupo;
	}

	public void setNome_grupo(String nome_grupo) {
		this.nome_grupo = nome_grupo;
	}


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}
	
	
}
