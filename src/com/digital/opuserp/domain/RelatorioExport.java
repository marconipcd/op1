package com.digital.opuserp.domain;

import javax.persistence.Entity;

public class RelatorioExport {

	private String coluna;
	private Long qtd;
	
	public RelatorioExport(String coluna, Long qtd) {		
		this.coluna = coluna;
		this.qtd = qtd;
	}

	public String getColuna() {
		return coluna;
	}

	public void setColuna(String coluna) {
		this.coluna = coluna;
	}

	public Long getQtd() {
		return qtd;
	}

	public void setQtd(Long qtd) {
		this.qtd = qtd;
	}
	
	
}
