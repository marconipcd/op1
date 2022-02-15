package com.digital.opuserp.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="issqn")
public class Issqn {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID", nullable=false, unique=true)
	private Integer id;
	@Column(name="DESCRICAO")
	private String descricao;
	@Column(name="BASE_CALCULO")
	private String base_calculo;
	@Column(name="ALIQUOTA")
	private String aliquota;
	@Column(name="ITEM_LISTA_SERVICO")
	private String item_lista_servico;
	@Column(name="SITUACAO_TRIBUTARIA")
	private String situacao_tributaria;
	
	public Issqn(){
		
	}

	public Issqn(Integer id, String descricao, String base_calculo,
			String aliquota, String item_lista_servico,
			String situacao_tributaria) {
		super();
		this.id = id;
		this.descricao = descricao;
		this.base_calculo = base_calculo;
		this.aliquota = aliquota;
		this.item_lista_servico = item_lista_servico;
		this.situacao_tributaria = situacao_tributaria;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getBase_calculo() {
		return base_calculo;
	}

	public void setBase_calculo(String base_calculo) {
		this.base_calculo = base_calculo;
	}

	public String getAliquota() {
		return aliquota;
	}

	public void setAliquota(String aliquota) {
		this.aliquota = aliquota;
	}

	public String getItem_lista_servico() {
		return item_lista_servico;
	}

	public void setItem_lista_servico(String item_lista_servico) {
		this.item_lista_servico = item_lista_servico;
	}

	public String getSituacao_tributaria() {
		return situacao_tributaria;
	}

	public void setSituacao_tributaria(String situacao_tributaria) {
		this.situacao_tributaria = situacao_tributaria;
	}
	
	
}
