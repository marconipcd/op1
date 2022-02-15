package com.digital.opuserp.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="ii")
public class Ii {

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
	
	public Ii(){
		
	}

	public Ii(Integer id, String descricao, String base_calculo, String aliquota) {
		super();
		this.id = id;
		this.descricao = descricao;
		this.base_calculo = base_calculo;
		this.aliquota = aliquota;
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
	
	
}
