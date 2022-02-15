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
@Table(name="pendencias")
public class Pendencias {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID")
	private Integer id;
	
	@OneToOne
	@JoinColumn(name="ACESSO_CLIENTE_ID")
	private AcessoCliente contrato;
	
	@Column(name="PENDENCIA")
	private String pendencia;
	
	@Column(name="STATUS")
	private String status;
	
	@Column(name="OPERADOR")
	private String operador;
	
	
	@Column(name="DATA")
	private Date data;
	
	@Column(name="OPERADOR_CONCLUSAO")
	private String operador_conclusao;
	
	
	@Column(name="DATA_CONCLUSAO")
	private Date data_conclusao;
	
	
	
	public Pendencias(){
		
	}
	
	

	public Pendencias(Integer id, AcessoCliente contrato, String pendencia,
			String status, String operador, Date data,
			String operador_conclusao, Date data_conclusao) {
		super();
		this.id = id;
		this.contrato = contrato;
		this.pendencia = pendencia;
		this.status = status;
		this.operador = operador;
		this.data = data;
		this.operador_conclusao = operador_conclusao;
		this.data_conclusao = data_conclusao;
	}



	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public AcessoCliente getContrato() {
		return contrato;
	}

	public void setContrato(AcessoCliente contrato) {
		this.contrato = contrato;
	}

	public String getPendencia() {
		return pendencia;
	}

	public void setPendencia(String pendencia) {
		this.pendencia = pendencia;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getOperador() {
		return operador;
	}

	public void setOperador(String operador) {
		this.operador = operador;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}



	public String getOperador_conclusao() {
		return operador_conclusao;
	}



	public void setOperador_conclusao(String operador_conclusao) {
		this.operador_conclusao = operador_conclusao;
	}



	public Date getData_conclusao() {
		return data_conclusao;
	}



	public void setData_conclusao(Date data_conclusao) {
		this.data_conclusao = data_conclusao;
	}
	
	
}
