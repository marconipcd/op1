package com.digital.opuserp.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="log_produto")
public class LogProduto {

	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID")
	private Integer id;
	
	@OneToOne
	@JoinColumn(name="PRODUTO_ID")
	private Produto produto;	
	
	@Column(name="ACAO")
	private String acao;
	
	@Column(name="QTD_ESTOQUE_ANTES")
	private Float qtdEstoque_antes;
	
	@Column(name="QTD_ESTOQUE_DEPOIS")
	private Float qtdEstoque_depois;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATA")
	private Date data;
	
	@Column(name="USUARIO")
	private String usuario;
	
	public LogProduto(){
		
	}
	
	public LogProduto(Integer id, Produto produto, String acao, Float qtdEstoque_antes, Float qtdEstoque_depois, Date data, String usuario){
		this.id = id;
		this.produto = produto;
		this.acao = acao;
		this.qtdEstoque_antes = qtdEstoque_antes;
		this.qtdEstoque_depois = qtdEstoque_depois;
		this.data = data;
		this.usuario = usuario;
	}
	
	public Integer getId(){
		return id;
	}
	public void setId(Integer id){
		this.id = id;
	}
	
	public Produto getProduto(){
		return produto;
	}
	public void setProduto(Produto produto){
		this.produto = produto;
	}
	
	public String getAcao(){
		return acao;		
	}
	public void setAcao(String acao){
		this.acao = acao;
	}
	
	public Float getQtdEstoque_antes(){
		return qtdEstoque_antes;
	}
	public void setQtdEstoque_antes(Float qtdEstoque_antes){
		this.qtdEstoque_antes = qtdEstoque_antes;
	}
	
	public Float getQtdEstoque_depois(){
		return qtdEstoque_depois;
	}
	public void setQtdEstoque_despois(Float qtdEstoque_depois){
		this.qtdEstoque_depois = qtdEstoque_depois;
	}
	
	public Date getData(){
		return data;
	}
	public void setData(Date data){
		this.data = data;
	}
	
	public String getUsuario(){
		return usuario;
	}
	
	public void setUsuario(String usuario){
		this.usuario = usuario;
	}
	
}
