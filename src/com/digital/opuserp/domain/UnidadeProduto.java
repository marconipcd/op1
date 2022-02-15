package com.digital.opuserp.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="unidade_produto")
public class UnidadeProduto {

	 @Id
	 @GeneratedValue(strategy=GenerationType.AUTO)
	 @Column(name="ID", nullable=false, unique=true)
	 private Integer id; 
	 @Column(name="EMPRESA_ID")
	 private Integer empresa_id; 
	 @Column(name="NOME")
	 private String nome;
	 @Column(name="PODE_FRACIONAR")
	 private boolean pode_fracionar; 
	 @Column(name="DESCRICAO")
	 private String descricao;
	 
	 public UnidadeProduto(){
		 
	 }
	
	 public UnidadeProduto(Integer id, Integer empresa_id, String nome,
			boolean pode_fracionar, String descricao) {
		super();
		this.id = id;
		this.empresa_id = empresa_id;
		this.nome = nome;
		this.pode_fracionar = pode_fracionar;
		this.descricao = descricao;
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

	public boolean isPode_fracionar() {
		return pode_fracionar;
	}

	public void setPode_fracionar(boolean pode_fracionar) {
		this.pode_fracionar = pode_fracionar;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	 
	 
	 
	 
}
