package com.digital.opuserp.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="parametizacao")
public class Parametizacao {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(nullable=false, unique=true, name="ID")
	private Integer id;
	@Column(nullable=false, name="COD_EMPRESA")
	private Integer cod_empresa;
	@Column(nullable=false,  name="COD_SUBMODULO")
	private Integer cod_submodulo;
	@Column(nullable=false,  name="CHAVE_CAMPO")
	private String chave_campo;
	@Column(nullable=false, unique=true, name="VALOR_PADRAO")
	private String valor_padrao;
	
	public Parametizacao(){
		
	}

	public Parametizacao(Integer id, Integer cod_empresa,
			Integer cod_submodulo, String chave_campo, String valor_padrao) {
		super();
		this.id = id;
		this.cod_empresa = cod_empresa;
		this.cod_submodulo = cod_submodulo;
		this.chave_campo = chave_campo;
		this.valor_padrao = valor_padrao;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getCod_empresa() {
		return cod_empresa;
	}

	public void setCod_empresa(Integer cod_empresa) {
		this.cod_empresa = cod_empresa;
	}

	public Integer getCod_submodulo() {
		return cod_submodulo;
	}

	public void setCod_submodulo(Integer cod_submodulo) {
		this.cod_submodulo = cod_submodulo;
	}

	public String getChave_campo() {
		return chave_campo;
	}

	public void setChave_campo(String chave_campo) {
		this.chave_campo = chave_campo;
	}

	public String getValor_padrao() {
		return valor_padrao;
	}

	public void setValor_padrao(String valor_padrao) {
		this.valor_padrao = valor_padrao;
	}
	
	
}
