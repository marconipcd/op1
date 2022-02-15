package com.digital.opuserp.view.modulos.relatorio;

public class SearchParameters {
	
	private String id;
	private String campo;
	private String operador;
	private String valor;
	
	public SearchParameters(){
		
	}

	public SearchParameters(String id,String campo, String operador, String valor) {
		super();
		this.id = id;
		this.campo = campo;
		this.operador = operador;
		this.valor = valor;
	}

	
	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getCampo() {
		return campo;
	}

	public void setCampo(String campo) {
		this.campo = campo;
	}

	public String getOperador() {
		return operador;
	}

	public void setOperador(String operador) {
		this.operador = operador;
	}

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}
	
	
	
}
