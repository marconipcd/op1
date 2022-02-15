package com.digital.opuserp.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="servico_plano")
public class ServicoPlano {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID", unique=true, nullable=false)
	private Integer id;	
	@Column(name="CODIGO")
	private String codigo;
	@Column(name="DESCRICAO")
	private String descricao;
	@Column(name="CODIGO_BARRAS")
	private String codigo_barras;
	@Column(name="UNIDADE")
	private String unidade;
	@Column(name="NCM")
	private String ncm;
	@Column(name="CLASSIFICACAO_MODELO_21")
	private String classificacao_modelo_21;	
	
	public ServicoPlano(){
		
	}
	public ServicoPlano(Integer id, String codigo, String descricao, String codigo_barras, String unidade,
			String ncm, String classificacao_modelo_21){
		
		super();		
		this.id = id;	
		this.codigo = codigo;
		this.descricao = descricao;
		this.codigo_barras = codigo_barras;
		this.unidade = unidade;
		this.ncm = ncm;
		this.classificacao_modelo_21 = classificacao_modelo_21;		
	}
	
	public Integer getId(){
		return id;
	}
	public void setId(Integer id){
		this.id = id;
	}	
	public String getCodigo(){
		return codigo;
	}
	public void setCodigo(String codigo){
		this.codigo = codigo;
	}
	public String getDescricao(){
		return descricao;
	}
	public void setDescricao(String descricao){
		this.descricao = descricao;
	}
	public String getCodigo_barras(){
		return codigo_barras;
	}
	public void setCodigo_barras(String codigo_barras){
		this.codigo_barras = codigo_barras;
	}
	public String getUnidade(){
		return unidade;
	}
	public void setUnidade(String unidade){
		this.unidade = unidade;
	}
	public String getNcm(){
		return ncm;
	}
	public void setNcm(String ncm){
		this.ncm = ncm;
	}
	public String getClassificacao_modelo_21(){
		return classificacao_modelo_21;
	}
	public void setClassificacao_modelo_21(String classidifcacao_modelo_21){
		this.classificacao_modelo_21 = classidifcacao_modelo_21;
	}
}
