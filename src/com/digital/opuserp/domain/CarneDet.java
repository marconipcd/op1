package com.digital.opuserp.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="carne_det")
public class CarneDet {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID")
	private Integer id;
	
	@OneToOne
	@JoinColumn(name="CARNE_CAB")
	private CarneCab carneCab;
	
	@Column(name="COD_PARCELA")
	private Integer codParcela;
	
	@Column(name="STATUS_PARCELA")
	private String statusParcela;
	
	@Column(name="VALOR_PARCELA")	
	private String valorParcela;	
	
	@Column(name="DATA_EXPIRACAO")
	private String dataExpiracao;
	
	@Column(name="LINK_PARCELA")
	private String linkParcela;
	
	@Column(name="LINK_PARCELA_PDF")
	private String linkParcelaPdf;
	
	@Column(name="CODIGO_BARRAS")
	private String codigoBarras;
	
	
	
	public CarneDet(){
		
	}



	public CarneDet(Integer id, CarneCab carneCab, Integer codParcela,
			String statusParcela, String valorParcela, String dataExpiracao,
			String linkParcela, String linkParcelaPdf, String codigoBarras) {
		super();
		this.id = id;
		this.carneCab = carneCab;
		this.codParcela = codParcela;
		this.statusParcela = statusParcela;
		this.valorParcela = valorParcela;
		this.dataExpiracao = dataExpiracao;
		this.linkParcela = linkParcela;
		this.linkParcelaPdf = linkParcelaPdf;
		this.codigoBarras = codigoBarras;
	}



	public Integer getId() {
		return id;
	}



	public void setId(Integer id) {
		this.id = id;
	}



	public CarneCab getCarneCab() {
		return carneCab;
	}



	public void setCarneCab(CarneCab carneCab) {
		this.carneCab = carneCab;
	}



	public Integer getCodParcela() {
		return codParcela;
	}



	public void setCodParcela(Integer codParcela) {
		this.codParcela = codParcela;
	}



	public String getStatusParcela() {
		return statusParcela;
	}



	public void setStatusParcela(String statusParcela) {
		this.statusParcela = statusParcela;
	}



	public String getValorParcela() {
		return valorParcela;
	}



	public void setValorParcela(String valorParcela) {
		this.valorParcela = valorParcela;
	}



	public String getDataExpiracao() {
		return dataExpiracao;
	}



	public void setDataExpiracao(String dataExpiracao) {
		this.dataExpiracao = dataExpiracao;
	}



	public String getLinkParcela() {
		return linkParcela;
	}



	public void setLinkParcela(String linkParcela) {
		this.linkParcela = linkParcela;
	}



	public String getLinkParcelaPdf() {
		return linkParcelaPdf;
	}



	public void setLinkParcelaPdf(String linkParcelaPdf) {
		this.linkParcelaPdf = linkParcelaPdf;
	}



	public String getCodigoBarras() {
		return codigoBarras;
	}



	public void setCodigoBarras(String codigoBarras) {
		this.codigoBarras = codigoBarras;
	}
	
	
	
	
}
