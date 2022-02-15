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
@Table(name="haver_boleto")
public class HaverBoleto {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID", nullable=false, unique=true)
	private Integer id;
	@OneToOne
	@JoinColumn(name="CONTAS_RECEBER_ID", nullable=false)	
	private ContasReceber boleto;
	@OneToOne
	@JoinColumn(name="HAVER_ID", nullable=false)
	private Haver haver;
	@Column(name="VALOR_UTILIZADO", nullable=true)
	private double valor_utilizado;
	
	public HaverBoleto(){
		
	}

	public HaverBoleto(Integer id, ContasReceber boleto, Haver haver) {
		super();
		this.id = id;
		this.boleto = boleto;
		this.haver = haver;
	}
	public HaverBoleto(Integer id, ContasReceber boleto, Haver haver, double valor_utilizado) {
		super();
		this.id = id;
		this.boleto = boleto;
		this.haver = haver;
		this.valor_utilizado = valor_utilizado;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public ContasReceber getBoleto() {
		return boleto;
	}

	public void setBoleto(ContasReceber boleto) {
		this.boleto = boleto;
	}

	public Haver getHaver() {
		return haver;
	}

	public void setHaver(Haver haver) {
		this.haver = haver;
	}

	public double getValor_utilizado() {
		return valor_utilizado;
	}

	public void setValor_utilizado(double valor_utilizado) {
		this.valor_utilizado = valor_utilizado;
	}
	
	
}
