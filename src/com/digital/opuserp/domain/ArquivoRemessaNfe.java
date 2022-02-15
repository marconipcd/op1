package com.digital.opuserp.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


@Entity
@Table(name="arquivo_remessa")
public class ArquivoRemessaNfe{


	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID", nullable=false, unique=true)
	private Integer id;
	@Column(name="MES_REFERENCIA")
	private String mes_referencia;
	@Column(name="ANO_REFERENCIA")
	private String ano_referencia;
	@Column(name="DESCRICAO")
	private String descricao;
	@Column(name="FINALIDADE")
	private String finalidade;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATA_CADASTRO")
	private Date data_cadastro;

	
	public ArquivoRemessaNfe(){
		
	}
	public ArquivoRemessaNfe(Integer id, String mes_referencia, String ano_referencia, String finalidade, Date data_cadastro){
		this.id = id;
		this.mes_referencia = mes_referencia;
		this.ano_referencia = ano_referencia;
		this.data_cadastro = data_cadastro;
		this.finalidade = finalidade;
	}
	
	public Integer getId(){
		return id;
	}
	public void setId(Integer id){
		this.id = id;
	}
	
	public String getMes_referencia(){
		return mes_referencia;
	}
	public void setMes_referencia(String mes_referencia){
		this.mes_referencia = mes_referencia;
	}
	
	public String getAno_referencia(){
		return ano_referencia;
	}
	public void setAno_referencia(String ano_referencia){
		this.ano_referencia = ano_referencia;
	}
	
	public String getDescricao(){
		return descricao;
	}
	public void setDescricao(String descricao){
		this.descricao = descricao;
	}
	
	public String getFinalidade(){
		return finalidade;
	}
	public void setFinalidade(String finalidade){
		this.finalidade = finalidade;
	}
	
	public Date getData_cadastro(){
		return data_cadastro;
	}
	public void setData_cadastro(Date data_cadastro){
		this.data_cadastro = data_cadastro;
	}
	

}