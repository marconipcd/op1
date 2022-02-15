package com.digital.opuserp.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="ramo_atividade")
public class RamoAtividade {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;	
	private String nome;
	@Temporal(TemporalType.TIMESTAMP)
	private Date data_cadastro;
	@Temporal(TemporalType.TIMESTAMP)
	private Date data_altercao;
	
	public RamoAtividade(){
		
	}

	public RamoAtividade(Integer id, String nome, Date data_cadastro,
			Date data_altercao) {
		super();
		this.id = id;
		this.nome = nome;
		this.data_cadastro = data_cadastro;
		this.data_altercao = data_altercao;
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

	public Date getData_cadastro() {
		return data_cadastro;
	}

	public void setData_cadastro(Date data_cadastro) {
		this.data_cadastro = data_cadastro;
	}

	public Date getData_altercao() {
		return data_altercao;
	}

	public void setData_altercao(Date data_altercao) {
		this.data_altercao = data_altercao;
	}
	
	
	
}
