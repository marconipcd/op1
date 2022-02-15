package com.digital.opuserp.view.modulos.pedido.pedidoProduto;

public class TotaisHash {

	private Integer formaPgto_id;
	private Double valor;
	private Integer qtdParc;
	
	public TotaisHash(){
		
	}
	
	public TotaisHash(Integer formaPgto_id, Double valor, Integer qtdParc){
		this.formaPgto_id = formaPgto_id;
		this.valor = valor;
		this.qtdParc = qtdParc;
	}

	public Integer getFormaPgto_id() {
		return formaPgto_id;
	}

	public void setFormaPgto_id(Integer formaPgto_id) {
		this.formaPgto_id = formaPgto_id;
	}

	public Double getValor() {
		return valor;
	}

	public void setValor(Double valor) {
		this.valor = valor;
	}

	public Integer getQtdParc() {
		return qtdParc;
	}

	public void setQtdParc(Integer qtdParc) {
		this.qtdParc = qtdParc;
	}
	
	
}
