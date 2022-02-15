package com.digital.opuserp.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name="contrato_manutencao")
public class PlanosManutencao {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID")
	private Integer id;
	@Column(name="EMPRESA_ID")
	private Integer empresa;
	@Column(name="NOME")
	private String nome;
	@Column(name="VALOR_MENSAL")
	private double valor_mensal;
	@Column(name="LIMITE_MENSAL")
	private Integer limite_mensal;
	@Column(name="NUMERO_ATIVOS")
	private Integer numeroAtivos;
	@Column(name="VIGENCIA")
	private Integer vigencia;
	
	public PlanosManutencao(){
		
	}
	
	public PlanosManutencao(Integer id,Integer empresa, String nome, double valor_mensal, Integer limite_mensal){
		this.id = id;
		this.empresa = empresa;
		this.nome = nome;
		this.valor_mensal = valor_mensal;
		this.limite_mensal = limite_mensal;
	}
	
	public Integer getId(){
		return id;
	}
	public void setId(Integer id){
		this.id = id;
	}
	public Integer getEmpresa(){
		return empresa;
	}
	public void setEmpresa(Integer empresa){
		this.empresa = empresa;
	}
	public String getNome(){
		return nome;
	}
	public void setNome(String nome){
		this.nome = nome;
	}
	public double getValor_mensal(){
		return valor_mensal;
	}
	public void setValor_mensal(double valor_mensal){
		this.valor_mensal = valor_mensal;
	}
	public Integer getLimite_mensal(){
		return limite_mensal;
	}
	public void setLimite_mensal(Integer limite_mensal){
		this.limite_mensal =limite_mensal;
	}

	public Integer getNumeroAtivos() {
		return numeroAtivos;
	}

	public void setNumeroAtivos(Integer numeroAtivos) {
		this.numeroAtivos = numeroAtivos;
	}

	public Integer getVigencia() {
		return vigencia;
	}

	public void setVigencia(Integer vigencia) {
		this.vigencia = vigencia;
	}
	
	
}
