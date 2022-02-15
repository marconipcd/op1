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
@Table(name="swith")
public class Swith {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID", nullable=false, unique=true)
	private Integer id;
	
	@Column(name="EMPRESA_ID")
	private Integer empresa_id;
	
	@Column(name="IDENTIFICACAO")
	private String identificacao;
	
	@OneToOne
	@JoinColumn(name="CONCENTRADOR_ID")
	private Concentrador concentrador;
	
	@Column(name="MODELO")
	private String modelo;
	
	@Column(name="ENDERECO")
	private String endereco;
	
	@Column(name="NUMERO")
	private String numero;
	
	@Column(name="REFERENCIA")
	private String referencia;
	
	@Column(name="OLT")
	private String olt;
	
	@OneToOne
	@JoinColumn(name="OLT_ID")
	private Olts olt_id;
	
	@Column(name="PON")
	private String pon;
	
	@Column(name="SINAL_DB")
	private String sinal_db;
	
	@OneToOne
	@JoinColumn(name="CONTRATO_MONITORAMENTO")
	private AcessoCliente contrato_monitoramento;
	
	@Column(name="IP_MONITORAMENTO")
	private String ip_monitoramento;
	
	@Column(name="SITUACAO")
	private String situacao;
	
	public Swith(){
		
	}

	public Swith(Integer id, Integer empresa_id, String identificacao,
			Concentrador concentrador, String modelo, String endereco,
			String numero, String referencia) {
		super();
		this.id = id;
		this.empresa_id = empresa_id;
		this.identificacao = identificacao;
		this.concentrador = concentrador;
		this.modelo = modelo;
		this.endereco = endereco;
		this.numero = numero;
		this.referencia = referencia;
	}
	public Swith(Integer id, Integer empresa_id, String identificacao,
			Concentrador concentrador, String modelo, String endereco,
			String numero, String referencia, String pon) {
		super();
		this.id = id;
		this.empresa_id = empresa_id;
		this.identificacao = identificacao;
		this.concentrador = concentrador;
		this.modelo = modelo;
		this.endereco = endereco;
		this.numero = numero;
		this.referencia = referencia;
		this.pon = pon;
	}

	public Integer getId() {
		return id;
	}

	public String getReferencia() {
		return referencia;
	}

	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getIdentificacao() {
		return identificacao;
	}

	public void setIdentificacao(String identificacao) {
		this.identificacao = identificacao;
	}

	public String getModelo() {
		return modelo;
	}

	public void setModelo(String modelo) {
		this.modelo = modelo;
	}

	public String getEndereco() {
		return endereco;
	}

	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}



	public Integer getEmpresa_id() {
		return empresa_id;
	}



	public void setEmpresa_id(Integer empresa_id) {
		this.empresa_id = empresa_id;
	}







	public Concentrador getConcentrador() {
		return concentrador;
	}

	public void setConcentrador(Concentrador concentrador) {
		this.concentrador = concentrador;
	}

	public String getPon() {
		return pon;
	}

	public void setPon(String pon) {
		this.pon = pon;
	}

	public String getOlt() {
		return olt;
	}

	public void setOlt(String olt) {
		this.olt = olt;
	}

	public String getSinal_db() {
		return sinal_db;
	}

	public void setSinal_db(String sinal_db) {
		this.sinal_db = sinal_db;
	}

	public AcessoCliente getContrato_monitoramento() {
		return contrato_monitoramento;
	}

	public void setContrato_monitoramento(AcessoCliente contrato_monitoramento) {
		this.contrato_monitoramento = contrato_monitoramento;
	}

	public String getIp_monitoramento() {
		return ip_monitoramento;
	}

	public void setIp_monitoramento(String ip_monitoramento) {
		this.ip_monitoramento = ip_monitoramento;
	}

	public String getSituacao() {
		return situacao;
	}

	public void setSituacao(String situacao) {
		this.situacao = situacao;
	}

	public Olts getOlt_id() {
		return olt_id;
	}

	public void setOlt_id(Olts olt_id) {
		this.olt_id = olt_id;
	}

	
	
	
	
	
	
}
