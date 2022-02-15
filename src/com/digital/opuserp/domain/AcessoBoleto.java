package com.digital.opuserp.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="acesso_boleto")
public class AcessoBoleto {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID", unique=true, nullable=false)
	private Integer id; 
	
	@Column(name="ACESSO_CLIENTE_ID", nullable=false)
	private Integer acesso_cliente_id; 
	
	@Column(name="CONTAS_RECEBER_ID", nullable=false)
	private Integer contas_receber_id; 
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATA", nullable=false)
	private Date data_cadastro;
	
	
	@PrePersist
	public void onCreate(){
		data_cadastro = new Date();
	}
	
	public AcessoBoleto(){
		
	}

	public AcessoBoleto(Integer acesso_cliente_id,
			Integer contas_receber_id) {
		super();
		
		this.acesso_cliente_id = acesso_cliente_id;
		this.contas_receber_id = contas_receber_id;
		
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getAcesso_cliente_id() {
		return acesso_cliente_id;
	}

	public void setAcesso_cliente_id(Integer acesso_cliente_id) {
		this.acesso_cliente_id = acesso_cliente_id;
	}

	public Integer getContas_receber_id() {
		return contas_receber_id;
	}

	public void setContas_receber_id(Integer contas_receber_id) {
		this.contas_receber_id = contas_receber_id;
	}

	public Date getData_cadastro() {
		return data_cadastro;
	}

	public void setData_cadastro(Date data_cadastro) {
		this.data_cadastro = data_cadastro;
	}
	
	
	
}
