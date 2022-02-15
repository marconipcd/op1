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
@Table(name="alteracoes_haver_cab")
public class AlteracoesHaverCab {

	@Id
	@Column(name="ID", nullable=false)
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	@Column(name="DATA_ALTERACAO")
	private Date data_alteracao;
	@OneToOne
	@JoinColumn(name="HAVER_CAB_ID", nullable=false)
	private HaverCab haver;
	@Column(name="TIPO")
	private String tipo;
	@Column(name="DESCRICAO")
	private String descricao;
	@Column(name="VALOR_HAVER")
	private double valor_haver;
	@Column(name="VALOR_SALDO")
	private double valor_saldo;
	@Column(name="VALOR_UTILIZADO")
	private double valor_utilizado;
	@Column(name="OPERADOR_ID")
	private Integer operador_id;
	
	public AlteracoesHaverCab(){
		
	}

	public AlteracoesHaverCab(Integer id, Date data_alteracao, HaverCab haver,
			String tipo, String descricao, double valor_haver,
			double valor_saldo, double valor_utilizado, Integer operador_id) {
		super();
		this.id = id;
		this.data_alteracao = data_alteracao;
		this.haver = haver;
		this.tipo = tipo;
		this.descricao = descricao;
		this.valor_haver = valor_haver;
		this.valor_saldo = valor_saldo;
		this.valor_utilizado = valor_utilizado;
		this.operador_id = operador_id;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getData_alteracao() {
		return data_alteracao;
	}

	public void setData_alteracao(Date data_alteracao) {
		this.data_alteracao = data_alteracao;
	}

	public HaverCab getHaver() {
		return haver;
	}

	public void setHaver(HaverCab haver) {
		this.haver = haver;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public double getValor_haver() {
		return valor_haver;
	}

	public void setValor_haver(double valor_haver) {
		this.valor_haver = valor_haver;
	}

	public double getValor_saldo() {
		return valor_saldo;
	}

	public void setValor_saldo(double valor_saldo) {
		this.valor_saldo = valor_saldo;
	}

	public double getValor_utilizado() {
		return valor_utilizado;
	}

	public void setValor_utilizado(double valor_utilizado) {
		this.valor_utilizado = valor_utilizado;
	}

	public Integer getOperador_id() {
		return operador_id;
	}

	public void setOperador_id(Integer operador_id) {
		this.operador_id = operador_id;
	}
	
	
}
