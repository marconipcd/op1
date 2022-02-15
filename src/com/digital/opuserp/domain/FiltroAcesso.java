package com.digital.opuserp.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;

@Entity
@Table(name="filtro_adulto_acesso")
public class FiltroAcesso {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID", nullable=false, unique=true)
	private Integer id;
	@Column(name="ACESSO_CLIENTE_ID", nullable=false)
	private Integer cod_contrato;
	@Column(name="nome", nullable=false)
	private String nome;
	@Column(name="palavra", nullable=false)
	private String palavra;
	@Column(name="DATA_CADASTRO", nullable=false)
	private Date data_cadastro;
	
	@PrePersist
	private void onCreate(){
		data_cadastro = new Date();
	}
	
	public FiltroAcesso(){
		
	}

	
	public FiltroAcesso(Integer id, Integer cod_contrato,String nome, String palavra,
			Date data_cadastro) {
		super();
		this.id = id;
		this.cod_contrato = cod_contrato;
		this.nome = nome;
		this.palavra = palavra;
		this.data_cadastro = data_cadastro;
	}

	public Integer getCod_contrato() {
		return cod_contrato;
	}

	public void setCod_contrato(Integer cod_contrato) {
		this.cod_contrato = cod_contrato;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getPalavra() {
		return palavra;
	}

	public void setPalavra(String palavra) {
		this.palavra = palavra;
	}

	public Date getData_cadastro() {
		return data_cadastro;
	}

	public void setData_cadastro(Date data_cadastro) {
		this.data_cadastro = data_cadastro;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
	
	
}
