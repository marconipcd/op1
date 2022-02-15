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
@Table(name="alerta_pendencia")
public class AlertaPendencia {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID")
	private Integer id;
	
	@OneToOne
	@JoinColumn(name="EMPRESA_ID")
	private Empresa empresa;
	
	@Column(name="SUBMODULO_ID")
	private Integer submodulo_id;
	
	@Column(name="CODIGO_TABELA")
	private Integer codigo_tabela;
	
	@Column(name="DESCRICAO")
	private String descricao;
	
	@Column(name="STATUS")
	private String status;
	
	@Column(name="DATA_CADASTRO")
	private Date data_cadastro;
	
	public AlertaPendencia(){
		
	}

	public AlertaPendencia(Integer id, Empresa empresa, Integer submodulo_id,
			Integer codigo_tabela, String descricao, String status,
			Date data_cadastro) {
		super();
		this.id = id;
		this.empresa = empresa;
		this.submodulo_id = submodulo_id;
		this.codigo_tabela = codigo_tabela;
		this.descricao = descricao;
		this.status = status;
		this.data_cadastro = data_cadastro;
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

	public Integer getSubmodulo_id() {
		return submodulo_id;
	}

	public void setSubmodulo_id(Integer submodulo_id) {
		this.submodulo_id = submodulo_id;
	}

	public Integer getCodigo_tabela() {
		return codigo_tabela;
	}

	public void setCodigo_tabela(Integer codigo_tabela) {
		this.codigo_tabela = codigo_tabela;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
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
