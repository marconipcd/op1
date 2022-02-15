package com.digital.opuserp.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GenerationType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="ose_produto")
public class OseProduto {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID")
	private Integer id;
	
	@OneToOne
	@JoinColumn(name="OSE_ID")
	private Ose ose;
	
	@OneToOne
	@JoinColumn(name="PRODUTO_ID")
	private EstoqueMovel produto;
	
	@OneToOne
	@JoinColumn(name="PEDIDO_ID")
	private EcfPreVendaCabecalho pedido;
	
	@Column(name="QTD")
	private double qtd;
	
	public OseProduto(){
		
	}
	
	public OseProduto(Integer id, Ose ose, EstoqueMovel produto,EcfPreVendaCabecalho pedido, double qtd){
		
		this.id = id;
		this.ose = ose;
		this.produto = produto;
		this.qtd = qtd;
		this.pedido = pedido;
	}
	
	public Integer getId(){
		return id;
	}
	public void setId(Integer id){
		this.id = id;
	}
	
	public Ose getOse(){
		return ose;
	}
	public void setOse(Ose ose){
		this.ose = ose;
	}
	
	public EstoqueMovel getProduto(){
		return produto;
	}
	public void setProduto(EstoqueMovel produto){
		this.produto = produto;
	}
	
	public double getQtd(){
		return qtd;
	}
	public void setQtd(double qtd){
		this.qtd = qtd;
	}

	public EcfPreVendaCabecalho getPedido() {
		return pedido;
	}

	public void setPedido(EcfPreVendaCabecalho pedido) {
		this.pedido = pedido;
	}
	
}
