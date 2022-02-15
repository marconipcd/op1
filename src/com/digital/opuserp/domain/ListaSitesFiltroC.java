package com.digital.opuserp.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="lista_sites_filtro_c")
public class ListaSitesFiltroC {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="id", nullable=false, unique=true)
	private Integer id;
	@Column(name="nome", nullable=false)
	private String nome;
	@Column(name="data_atualizacao", nullable=false)
	private Date data_atualizacao;
	@Column(name="status", nullable=false)
	private String status;
	
	public ListaSitesFiltroC(){
		
	}

	public ListaSitesFiltroC(Integer id, String nome, Date data_atualizacao, String status) {
		super();
		this.id = id;
		this.nome = nome;
		this.data_atualizacao = data_atualizacao;
		this.status = status;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Date getData_atualizacao() {
		return data_atualizacao;
	}

	public void setData_atualizacao(Date data_atualizacao) {
		this.data_atualizacao = data_atualizacao;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	
}
