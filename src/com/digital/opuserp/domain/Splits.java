package com.digital.opuserp.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="splits")
public class Splits {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID", nullable=false)
	private Integer id;
	@Column(name="OLT_ID", nullable=false)
	private Integer olt;
	@Column(name="REDE_ID", nullable=false)
	private Integer rede;
	@Column(name="DESCRICAO", nullable=false)
	private String descricao;
	
	public Splits(){
		
	}

	public Splits(Integer id, Integer olt, Integer rede, String descricao) {
		super();
		this.id = id;
		this.olt = olt;
		this.rede = rede;
		this.descricao = descricao;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getOlt() {
		return olt;
	}

	public void setOlt(Integer olt) {
		this.olt = olt;
	}

	public Integer getRede() {
		return rede;
	}

	public void setRede(Integer rede) {
		this.rede = rede;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	
	
}
