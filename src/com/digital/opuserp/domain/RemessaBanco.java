package com.digital.opuserp.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="remessa_banco")
public class RemessaBanco {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID")
	private Integer id;
	
	@Column(name="NOME_ARQUIVO")
	private String nome_arquivo;
	
	@Column(name="DATA")
	private Date data;
	
	public RemessaBanco(){
		
	}
	
	public RemessaBanco(Integer id, String nome_arquivo, Date data){
		this.id = id;
		this.nome_arquivo = nome_arquivo;
		this.data = data;
	}
	
	public Integer getId(){
		return id;
	}
	public void setId(Integer id){
		this.id = id;
	}
	
	public String getNome_arquivo(){
		return nome_arquivo;
	}
	public void setNome_arquivo(String nome_arquivo){
		this.nome_arquivo = nome_arquivo;
	}
	
	public Date getData(){
		return data;
	}
	public void setData(Date data){
		this.data = data;
	}
}
