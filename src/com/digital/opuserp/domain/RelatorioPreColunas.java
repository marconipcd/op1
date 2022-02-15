package com.digital.opuserp.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


@Entity
@Table(name="relatorio_pre_colunas")
public class RelatorioPreColunas {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID")
	private Integer id;
	@ManyToOne
	@JoinColumn(name="COD_RELATORIO")
	private RelatorioPre relatorioPre;
	@Column(name="COLUNA")
	private String coluna;
	
	
	public RelatorioPreColunas(){
		
	}


	public RelatorioPreColunas(Integer id, RelatorioPre relatorioPre,
			String coluna) {
		super();
		this.id = id;
		this.relatorioPre = relatorioPre;
		this.coluna = coluna;
	}


	public Integer getId() {
		return id;
	}


	public void setId(Integer id) {
		this.id = id;
	}


	public RelatorioPre getRelatorioPre() {
		return relatorioPre;
	}


	public void setRelatorioPre(RelatorioPre relatorioPre) {
		this.relatorioPre = relatorioPre;
	}


	public String getColuna() {
		return coluna;
	}


	public void setColuna(String coluna) {
		this.coluna = coluna;
	}
	
	
}
