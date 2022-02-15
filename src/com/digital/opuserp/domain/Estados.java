package com.digital.opuserp.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="estados")
public class Estados {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID")
	private Integer id;
	@Column(name="CODIGO_IBGE")
	private Integer codigo_ibge;
	@Column(name="SIGLA")
	private String sigla;
	@Column(name="NOME")
	private String nome;
	
	public Estados(){
		
	}

	public Estados(Integer id, Integer codigo_ibge, String sigla, String nome) {
		super();
		this.id = id;
		this.codigo_ibge = codigo_ibge;
		this.sigla = sigla;
		this.nome = nome;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getCodigo_ibge() {
		return codigo_ibge;
	}

	public void setCodigo_ibge(Integer codigo_ibge) {
		this.codigo_ibge = codigo_ibge;
	}

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
	
	
}
