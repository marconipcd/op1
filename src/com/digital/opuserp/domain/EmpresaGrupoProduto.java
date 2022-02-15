package com.digital.opuserp.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="empresa_grupo_produto")
public class EmpresaGrupoProduto {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID")
	private Integer id;
	@OneToOne
	@JoinColumn(name="EMPRESA_ID")
	private Empresa empresa;
	@OneToOne
	@JoinColumn(name="GRUPO_PRODUTO_ID")
	private GrupoProduto grupoProduto;
	
	public EmpresaGrupoProduto(){
		
	}

	
	public EmpresaGrupoProduto(Integer id, Empresa empresa,
			GrupoProduto grupoProduto) {
		
		this.id = id;
		this.empresa = empresa;
		this.grupoProduto = grupoProduto;
	}


	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Empresa getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}

	public GrupoProduto getGrupoProduto() {
		return grupoProduto;
	}

	public void setGrupoProduto(GrupoProduto grupoProduto) {
		this.grupoProduto = grupoProduto;
	}
	
	

}
