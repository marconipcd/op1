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
@Table(name="empresa_fornecedor")
public class EmpresaFornecedor {

	@Id
	@Column(name="ID")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	@OneToOne
	@JoinColumn(name="EMPRESA_ID")
	private Empresa empresa;
	@OneToOne
	@JoinColumn(name="FORNECEDOR_ID")
	private Fornecedor fornecedor;
	
	public EmpresaFornecedor(){
		
	}
	
	

	public EmpresaFornecedor(Integer id, Empresa empresa, Fornecedor fornecedor) {
		super();
		this.id = id;
		this.empresa = empresa;
		this.fornecedor = fornecedor;
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

	public Fornecedor getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(Fornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}
	
	
}
