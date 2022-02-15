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
@Table(name="alteracores_haver")
public class AlteracoesHaver {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID", nullable=false, unique=true)
	private Integer id;
	
	@Column(name="TIPO", nullable=false)
	private String tipo;
	
	@OneToOne
	@JoinColumn(name="HAVER_ID", nullable=false)
	private Haver haver;
	
	@Column(name="VALOR_HAVER")
	private double valor_haver;
	@Column(name="VALOR_UTILIZADO")
	private double valor_utilizado;
	@Column(name="VALOR_SALDO")
	private double valor_saldo;
	
	@OneToOne
	@JoinColumn(name="OPERADOR_ID", nullable=false)
	private Usuario operador;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATA_ALTERACAO", nullable=false)
	private Date data_alteracao;
	
	public AlteracoesHaver(){
		
	}
	
	

	public AlteracoesHaver(Integer id, String tipo, Haver haver,
			double valor_haver, double valor_utilizado, double valor_saldo,
			Usuario operador, Date data_alteracao) {
		super();
		this.id = id;
		this.tipo = tipo;
		this.haver = haver;
		this.valor_haver = valor_haver;
		this.valor_utilizado = valor_utilizado;
		this.valor_saldo = valor_saldo;
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

	public Haver getHaver() {
		return haver;
	}

	public void setHaver(Haver haver) {
		this.haver = haver;
	}

	public double getValor_haver() {
		return valor_haver;
	}

	public void setValor_haver(double valor_haver) {
		this.valor_haver = valor_haver;
	}

	public double getValor_utilizado() {
		return valor_utilizado;
	}

	public void setValor_utilizado(double valor_utilizado) {
		this.valor_utilizado = valor_utilizado;
	}

	public double getValor_saldo() {
		return valor_saldo;
	}

	public void setValor_saldo(double valor_saldo) {
		this.valor_saldo = valor_saldo;
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
