package com.digital.opuserp.domain;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="tabelas_preco")
@Cacheable(value=false)
public class TabelasPreco {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID", nullable=false, unique=true, length=10)
	private Integer id;
	@Column(name="NOME", nullable=true, unique=false, length=100)
	private String nome;
	@Column(name="DESCONTO", nullable=true, unique=false, length=18)
	private double desconto;
	
	@OneToOne
	@JoinColumn(name="EMPRESA_ID")
	private Empresa empresa;
	
	public TabelasPreco(){
		
	}

	public TabelasPreco(Integer id){
		super();
		this.id = id;
	}

	public TabelasPreco(Integer id, String nome, double desconto,
			Empresa empresa) {
		super();
		this.id = id;
		this.nome = nome;
		this.desconto = desconto;
		this.empresa = empresa;
	}



	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public double getDesconto() {
		return desconto;
	}

	public void setDesconto(double desconto) {
		this.desconto = desconto;
	}



	public Empresa getEmpresa() {
		return empresa;
	}



	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}
	
	
	
}
