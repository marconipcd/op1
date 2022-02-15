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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="ocorrencia_contas_receber")
public class OcorrenciaContasReceber {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID")
	private Integer id;
	
	@Column(name="tipo")
	private String tipo;
	
	@OneToOne
	@JoinColumn(name="CONTAS_RECEBER_ID")
	private ContasReceber contas_receber;
	
	@Column(name="OPERADOR")
	private String operador;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATA", nullable=false)
	private Date data;
	
	public OcorrenciaContasReceber(){
		
	}

	public OcorrenciaContasReceber(Integer id, String tipo, ContasReceber contas_receber,
			String operador, Date data) {
		super();
		this.id = id;
		this.tipo = tipo;
		this.contas_receber = contas_receber;
		this.operador = operador;
		this.data = data;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public ContasReceber getContas_receber() {
		return contas_receber;
	}

	public void setContas_receber(ContasReceber contas_receber) {
		this.contas_receber = contas_receber;
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
}
