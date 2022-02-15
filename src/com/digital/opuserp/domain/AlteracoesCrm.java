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
@Table(name="alteracoes_contrato_crm")
public class AlteracoesCrm {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID", nullable=false, unique=true)
	private Integer id;
	
	@Column(name="TIPO", nullable=false)
	private String tipo;
	
	@OneToOne
	@JoinColumn(name="CRM_ID", nullable=false)
	private Crm crm_id;
	
	@OneToOne
	@JoinColumn(name="OPERADOR_ID", nullable=false)
	private Usuario operador;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATA_ALTERACAO", nullable=false)
	private Date data_alteracao;

	public AlteracoesCrm(){
		
	}
	
	public AlteracoesCrm(Integer id) {
		super();
		this.id = id;
	}
	
	public AlteracoesCrm(Integer id, String tipo, Crm crm_id,
			Usuario operador, Date data_alteracao) {
		super();
		this.id = id;
		this.tipo = tipo;
		this.crm_id = crm_id;
		this.operador = operador;
		this.data_alteracao = data_alteracao;
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

	public Usuario getOperador() {
		return operador;
	}

	public void setOperador(Usuario operador) {
		this.operador = operador;
	}

	public Date getData_alteracao() {
		return data_alteracao;
	}

	public void setData_alteracao(Date data_alteracao) {
		this.data_alteracao = data_alteracao;
	}

	public Crm getCrm_id() {
		return crm_id;
	}

	public void setCrm_id(Crm crm_id) {
		this.crm_id = crm_id;
	}
	
	
	
}

