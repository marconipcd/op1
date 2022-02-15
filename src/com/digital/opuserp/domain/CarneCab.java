package com.digital.opuserp.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="carne_cab")
public class CarneCab {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID")
	private Integer id;
	
	@Column(name="COD_CARNE_GERENCIA")
	private Integer codCarneGerencia;
	
	@Column(name="STATUS")
	private String status;
	
	@Column(name="COVER")
	private String cover;
	
	@Column(name="LINK_CARNE")
	private String linkCarne;
	
	@Column(name="LINK_CARNE_PDF")
	private String linkCarnePdf;
	
	@Column(name="DATA_CADASTRO")
	private Date dataCadastro;
	
	public CarneCab(){
		
	}

	public CarneCab(Integer id, Integer codCarneGerencia, String status,
			String cover, String linkCarne, String linkCarnePdf,
			Date dataCadastro) {
		super();
		this.id = id;
		this.codCarneGerencia = codCarneGerencia;
		this.status = status;
		this.cover = cover;
		this.linkCarne = linkCarne;
		this.linkCarnePdf = linkCarnePdf;
		this.dataCadastro = dataCadastro;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getCodCarneGerencia() {
		return codCarneGerencia;
	}

	public void setCodCarneGerencia(Integer codCarneGerencia) {
		this.codCarneGerencia = codCarneGerencia;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCover() {
		return cover;
	}

	public void setCover(String cover) {
		this.cover = cover;
	}

	public String getLinkCarne() {
		return linkCarne;
	}

	public void setLinkCarne(String linkCarne) {
		this.linkCarne = linkCarne;
	}

	public String getLinkCarnePdf() {
		return linkCarnePdf;
	}

	public void setLinkCarnePdf(String linkCarnePdf) {
		this.linkCarnePdf = linkCarnePdf;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	
}
