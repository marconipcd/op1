package com.digital.opuserp.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="cest")
public class Cest {

	@Id
	@Column(name="ID", nullable=false)
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	@Column(name="CEST")
	private String cest;
	@Column(name="NCM")
	private String ncm;
	@Column(name="DESCRICAO")
	private String descricao;
	
	public Cest(){
		
	}
	
	public Cest(Integer id, String cest, String ncm, String descricao){
		this.id = id;
		this.cest = cest;
		this.ncm = ncm;
		this.descricao = descricao;
	}
	
	public void setId(Integer id){
		this.id = id;
	}
	public Integer getId(){
		return id;
	}
	
	public void setCest(String cest){
		this.cest = cest;
	}
	public String getCest(){
		return cest;
	}
	
	public void setNcm(String ncm){
		this.ncm = ncm;
	}
	public String getNcm(){
		return ncm;
	}
	
	public void setDescricao(String descricao){
		this.descricao = descricao;				
	}
	public String getDescricao(){
		return descricao;
	}
	
}
