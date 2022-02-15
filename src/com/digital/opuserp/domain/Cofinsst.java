package com.digital.opuserp.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="cofins_st")
public class Cofinsst {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID")
	private Integer id;
	
	@Column(name="DESCRICAO")
	private String descricao;
	@Column(name="TIPO")
	private String tipo;
	@Column(name="BASE_CALCULO")
	private String base_calculo;
	@Column(name="ALIQUOTA")
	private String aliquota;
	@Column(name="VALOR")
	private String valor;
	
	public Cofinsst(){
		
	}

	public Cofinsst(Integer id, String descricao, String tipo,
			String base_calculo, String aliquota, String valor) {
		super();
		this.id = id;
		this.descricao = descricao;
		this.tipo = tipo;
		this.base_calculo = base_calculo;
		this.aliquota = aliquota;
		this.valor = valor;
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

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
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

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}
	
}
