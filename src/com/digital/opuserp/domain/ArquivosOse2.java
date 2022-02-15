package com.digital.opuserp.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="arquivos_ose2")
public class ArquivosOse2 {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID")
	private Integer id;	
	
	@Column(name="OSE_ID")
	private Integer ose;
	
	@Column(name="NOME",nullable=true)
	private String nome;
	
		
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATA")
	private Date data;
	
	@Column(name="USUARIO")
	private String usuario;
	
	@Column(name="LINK")
	private String link;
	
	public ArquivosOse2(){
		
	}

	public ArquivosOse2(Integer id, Integer contrato,String nome, Date data, String link) {
		super();
		this.id = id;
		this.ose = contrato;
		this.nome = nome;		
		this.data = data;
		this.link = link;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getContrato() {
		return ose;
	}

	public void setContrato(Integer contrato) {
		this.ose = contrato;
	}
	
	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}
	
	
}
