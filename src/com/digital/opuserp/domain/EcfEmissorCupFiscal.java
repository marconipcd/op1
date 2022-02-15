package com.digital.opuserp.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="ecf_emissor_cup_fiscal")
public class EcfEmissorCupFiscal {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID", unique=true, nullable=false)
	private Integer id; 
	
	@Column(name="ECF_EMISSOR_COD", nullable=false)
	private String ecf_emissor_cod; 
	
	@Column(name="REFERENCIA", nullable=false)
	private String referencia;

	public EcfEmissorCupFiscal() {
		super();
	}

	public EcfEmissorCupFiscal(Integer id) {
		super();
		this.id = id;
	}

	public EcfEmissorCupFiscal(Integer id, String ecf_emissor_cod,
			String referencia) {
		super();
		this.id = id;
		this.ecf_emissor_cod = ecf_emissor_cod;
		this.referencia = referencia;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getEcf_emissor_cod() {
		return ecf_emissor_cod;
	}

	public void setEcf_emissor_cod(String ecf_emissor_cod) {
		this.ecf_emissor_cod = ecf_emissor_cod;
	}

	public String getReferencia() {
		return referencia;
	}

	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}

}
