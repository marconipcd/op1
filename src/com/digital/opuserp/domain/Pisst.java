package com.digital.opuserp.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="pis_st")
public class Pisst {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID", nullable=false, unique=true)
	private Integer id;
	@Column(name="DESCRICAO")
	private String descricao;
	@Column(name="TIPO_PIS")
	private String tipo_pis;
	@Column(name="BASE_CALCULO")
	private String base_calculo;
	@Column(name="ALIQUOTA")
	private String aliquota;
	@Column(name="VALOR_PIS")
	private String valor_pis;
	
	public Pisst(){
		
	}

	public Pisst(Integer id, String descricao, String tipo_pis,
			String base_calculo, String aliquota, String valor_pis) {
		super();
		this.id = id;
		this.descricao = descricao;
		this.tipo_pis = tipo_pis;
		this.base_calculo = base_calculo;
		this.aliquota = aliquota;
		this.valor_pis = valor_pis;
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

	public String getTipo_pis() {
		return tipo_pis;
	}

	public void setTipo_pis(String tipo_pis) {
		this.tipo_pis = tipo_pis;
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

	public String getValor_pis() {
		return valor_pis;
	}

	public void setValor_pis(String valor_pis) {
		this.valor_pis = valor_pis;
	}
	
	
	
	
	

}
