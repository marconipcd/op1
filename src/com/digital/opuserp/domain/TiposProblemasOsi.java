package com.digital.opuserp.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="tipos_problemas_osi")
public class TiposProblemasOsi {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID", nullable=false, unique=true)
	private Integer id;
	
	@Column(name="EMPRESA_ID", nullable=false)
	private Integer empresa_id;
	
	@Column(name="NOME", nullable=false)
	private String nome;
	
	@Column(name="VALOR", nullable=false)
	private String valor;
	
	public TiposProblemasOsi()
	{
		
	}

	public TiposProblemasOsi(Integer id, Integer empresa_id, String nome,
			String valor) {
		super();
		this.id = id;
		this.empresa_id = empresa_id;
		this.nome = nome;
		this.valor = valor;
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

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}
	
	
	
	
	
}
