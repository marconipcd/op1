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
@Table(name="historico_desbloqueio_72_horas")
public class HistoricoDesbloqueio72Horas {

	
	@Id
	@Column(name="ID")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	
	@Column(name="CONTRATO_ID")
	private Integer contrato_id;
	
	@Column(name="USUARIO_ID")
	private Integer usuario_id;
	
	@Column(name="DATA")
	private Date data;
	
	@Column(name="BOLETO_ID")
	private Integer boleto;
		
	public HistoricoDesbloqueio72Horas(){
		
	}
	
	
	public HistoricoDesbloqueio72Horas(Integer id, Integer contrato_id,
			Integer usuario_id, Date data) {
		super();
		this.id = id;
		this.contrato_id = contrato_id;
		this.usuario_id = usuario_id;
		this.data = data;
	}




	public Integer getId() {
		return id;
	}


	public void setId(Integer id) {
		this.id = id;
	}


	public Integer getContrato_id() {
		return contrato_id;
	}


	public void setContrato_id(Integer contrato_id) {
		this.contrato_id = contrato_id;
	}


	public Integer getUsuario_id() {
		return usuario_id;
	}


	public void setUsuario_id(Integer usuario_id) {
		this.usuario_id = usuario_id;
	}


	public Date getData() {
		return data;
	}


	public void setData(Date data) {
		this.data = data;
	}


	public Integer getBoleto() {
		return boleto;
	}


	public void setBoleto(Integer boleto) {
		this.boleto = boleto;
	}
	
	
	
}
