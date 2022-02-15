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
@Table(name="alteracoes_rma")
public class AlteracoesRma {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID", nullable=false)
	private Integer id;
	@OneToOne
	@JoinColumn(name="RMA_ID", nullable=false)
	private RmaDetalhe rma;
	@Column(name="TIPO", nullable=false)
	private String tipo;
	@OneToOne
	@JoinColumn(name="OPERADOR_ID", nullable=false)
	private Usuario operador;
	@Temporal(TemporalType.TIMESTAMP)
	private Date data_alteracao;
	
	public AlteracoesRma(){
		
	}

	public AlteracoesRma(Integer id, RmaDetalhe rma, String tipo,
			Usuario operador, Date data_alteracao) {
		super();
		this.id = id;
		this.rma = rma;
		this.tipo = tipo;
		this.operador = operador;
		this.data_alteracao = data_alteracao;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public RmaDetalhe getRma() {
		return rma;
	}

	public void setRma(RmaDetalhe rma) {
		this.rma = rma;
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
	
	
	
	
}
