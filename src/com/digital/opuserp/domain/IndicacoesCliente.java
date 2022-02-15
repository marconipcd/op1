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

@Table(name="indicacoes_cliente") 
@Entity
public class IndicacoesCliente {

	@Id
	@Column(name="ID")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	
	@OneToOne
	@JoinColumn(name="COD_CLIENTE_QUE_INDICOU")
	private Cliente cliente_que_indicou;
	
	@OneToOne
	@JoinColumn(name="COD_CLIENTE_INDICADO")
	private Cliente cliente_indicado;
	
	@Column(name="STATUS_2")
	private String status;
	
	@Column(name="DATA_CADASTRO")
	private Date data_cadastro;
	
	@Column(name="BOLETO_ID")
	private String boleto;
	
	public IndicacoesCliente(){
		
	}

	public IndicacoesCliente(Integer id, Cliente cliente_que_indicou,
			Cliente cliente_indicado, String status, Date data_cadastro) {
		super();
		this.id = id;
		this.cliente_que_indicou = cliente_que_indicou;
		this.cliente_indicado = cliente_indicado;
		this.status = status;
		this.data_cadastro = data_cadastro;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Cliente getCliente_que_indicou() {
		return cliente_que_indicou;
	}

	public void setCliente_que_indicou(Cliente cliente_que_indicou) {
		this.cliente_que_indicou = cliente_que_indicou;
	}

	public Cliente getCliente_indicado() {
		return cliente_indicado;
	}

	public void setCliente_indicado(Cliente cliente_indicado) {
		this.cliente_indicado = cliente_indicado;
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

	public String getBoleto() {
		return boleto;
	}

	public void setBoleto(String boleto) {
		this.boleto = boleto;
	}
	
	
	
}
