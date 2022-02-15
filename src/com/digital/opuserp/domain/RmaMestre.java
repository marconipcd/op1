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

@Entity
@Table(name="rma_mestre")
public class RmaMestre {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID", nullable=false)
	private Integer id;
	@Column(name="EMPRESA_ID", nullable=false)
	private Integer empresa_id;
	@OneToOne
	@JoinColumn(name="FORNECEDOR_ID", nullable=false)
	private Fornecedor fornecedor;
	@Column(name="STATUS", nullable=false)
	private String status;	
	@Column(name="DATA_CADASTRO", nullable=false)
	private Date data_cadastro;
	
	public RmaMestre(){
		
	}

	public RmaMestre(Integer id, Integer empresa_id, Fornecedor fornecedor,
			String status, Date data_cadastro) {
		super();
		this.id = id;
		this.empresa_id = empresa_id;
		this.fornecedor = fornecedor;
		this.status = status;
		this.data_cadastro = data_cadastro;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getEmpresa_id() {
		return empresa_id;
	}

	public void setEmpresa_id(Integer empresa_id) {
		this.empresa_id = empresa_id;
	}

	public Fornecedor getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(Fornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getData_cadastro() {
		return data_cadastro;
	}

	public void setData_cadastro(Date data_cadastro) {
		this.data_cadastro = data_cadastro;
	}
	
	
	
		
}
