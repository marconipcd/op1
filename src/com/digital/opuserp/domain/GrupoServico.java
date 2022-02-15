package com.digital.opuserp.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;

@Entity
@Table(name="grupo_servico")
public class GrupoServico {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID",nullable=false, unique=true)
	private Integer id;
	@OneToOne
	@JoinColumn(name="EMPRESA_ID")
	private Empresa empresa;
	@Column(name="NOME")
	private String nome;
	@Column(name="DATA_CADASTRO")
	private Date data_cadastro;
	
	@PrePersist
	private void onInsert(){
		data_cadastro = new Date();
	}
	
	public GrupoServico(){
		
	}

	public GrupoServico(Integer id, Empresa empresa, String nome,
			Date data_cadastro) {
		super();
		this.id = id;
		this.empresa = empresa;
		this.nome = nome;
		this.data_cadastro = data_cadastro;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Empresa getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Date getData_cadastro() {
		return data_cadastro;
	}

	public void setData_cadastro(Date data_cadastro) {
		this.data_cadastro = data_cadastro;
	}
	
}
